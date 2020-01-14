package com.blackmorse.hattrick;

import com.blackmorse.hattrick.api.Hattrick;
import com.blackmorse.hattrick.api.leaguedetails.model.LeagueDetails;
import com.blackmorse.hattrick.api.matchdetails.model.HomeAwayTeam;
import com.blackmorse.hattrick.api.worlddetails.model.League;
import com.blackmorse.hattrick.clickhouse.model.MatchDetails;
import com.blackmorse.hattrick.clickhouse.model.PlayerRating;
import com.blackmorse.hattrick.model.TeamLeague;
import com.blackmorse.hattrick.model.TeamLeagueMatch;
import com.blackmorse.hattrick.model.enums.MatchType;
import com.blackmorse.hattrick.subscribers.MatchDetailsSubscriber;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.ParallelFlux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;

@Component
@Slf4j
public class HistoryLoader {

    private final Scheduler scheduler;
    private final Hattrick hattrick;

    private final MatchDetailsSubscriber matchDetailsSubscriber;

    private AtomicLong teams = new AtomicLong();
    private AtomicLong matchesCount = new AtomicLong();

    @Autowired
    public HistoryLoader(@Qualifier("apiExecutor") ExecutorService executorService,
                         Hattrick hattrick,
                         MatchDetailsSubscriber matchDetailsSubscriber) {
        this.scheduler = Schedulers.fromExecutor(executorService);
        this.hattrick = hattrick;
        this.matchDetailsSubscriber = matchDetailsSubscriber;
    }


    public void load(List<String> countryNames) {
        ParallelFlux<TeamLeagueMatch> matchesFlux = Flux.fromStream(
                        hattrick.getWorldDetails().getLeagueList().stream()
                            .filter(league -> countryNames.contains(league.getLeagueName())))
                .map(League::getNationalTeamId)
                .map(countryTeamId -> hattrick.getNationalTeamDetails(countryTeamId).getTeam().getLeague().getLeagueID())
                .map(leagueId -> hattrick.getLeagueUnitByName(leagueId, "II.1"))
                .flatMap(this::getAllLeagueIds)
                .flatMap(Flux::fromIterable)
                .parallel()
                .runOn(scheduler)
                .map(hattrick::getLeagueUnitById)
                .flatMap(this::teamsFromLeague)
                .flatMap(this::getMatches);

        matchesFlux
                .map(this::matchDetailsFromMatch)
                .subscribe(matchDetailsSubscriber::onNext, matchDetailsSubscriber::onError, matchDetailsSubscriber::onComplete);
    }


    private Flux<TeamLeague> teamsFromLeague(LeagueDetails league) {

        return Flux.fromStream(
                league.getTeams().
                        stream().
                        filter(team -> team.getUserId() != 0L)
                        .map(team -> TeamLeague.builder()
                                .leagueId(league.getLeagueId())
                                .leagueLevel(league.getLeagueLevel())
                                .leagueLevelUnitId(league.getLeagueLevelUnitId())
                                .leagueUnitName(league.getLeagueLevelUnitName())
                                .currentMatchRound(league.getCurrentMatchRound())
                                .teamId(team.getTeamId())
                                .teamName(team.getTeamName())
                            .build())
        );
    }

    private Flux<List<Long>> getAllLeagueIds(LeagueDetails twoOne) {
        return Mono.just(Collections.singletonList((long) (twoOne.getLeagueLevelUnitId() - 1)))
                .concatWith(Flux.fromStream(
                        IntStream.range(2, twoOne.getMaxLevel() + 1)
//                        IntStream.range(4,5)
                                .mapToObj(level -> hattrick.getLeagueUnitIdsForLevel(twoOne.getLeagueId(), level)))
                );
    }

    private Flux<TeamLeagueMatch> getMatches(TeamLeague teamLeague) {
        log.info("get {} teams", teams.incrementAndGet());
        return Flux.fromStream(
                hattrick.getArchiveMatches(teamLeague.getTeamId(), 61).getTeam().getMatchList()
                        .stream()
                        .filter(match -> match.getMatchType().equals(MatchType.LEAGUE_MATCH))
                        .map(match -> TeamLeagueMatch.builder()
                                .matchId(match.getMatchId())
                                .date(match.getMatchDate())
                                .teamLeague(teamLeague)
                            .build())
        );
    }

    private Flux<PlayerRating> playerRatingFromMatch(TeamLeagueMatch teamLeagueMatch) {
        return Flux.fromStream(hattrick.getMatchLineUp(teamLeagueMatch.getMatchId(), teamLeagueMatch.getMatchId())
                .getTeam().getLineUp()
                .stream()
                .map(lineUpPlayer -> PlayerRating.builder()
                        .leagueId(teamLeagueMatch.getTeamLeague().getLeagueId())
                        .divisionLevel(teamLeagueMatch.getTeamLeague().getLeagueLevel())
                        .leagueUnitId(teamLeagueMatch.getTeamLeague().getLeagueLevelUnitId())
                        .teamId(teamLeagueMatch.getTeamLeague().getTeamId())
                        .teamName(teamLeagueMatch.getTeamLeague().getTeamName())
                        .date(teamLeagueMatch.getDate())
                        .round(teamLeagueMatch.getTeamLeague().getCurrentMatchRound())
                        .matchId(teamLeagueMatch.getMatchId())
                        .playerId(lineUpPlayer.getPlayerId())
                        .roleId(lineUpPlayer.getRoleId().getValue())
                        .firstName(lineUpPlayer.getFirstName())
                        .lastName(lineUpPlayer.getLastName())
                        .ratingStars(lineUpPlayer.getRatingStars())
                        .ratingStars(lineUpPlayer.getRatingStarsEndOfMatch())
                        .behaviour(lineUpPlayer.getBehaviour())
                    .build())
        );
    }

    private MatchDetails matchDetailsFromMatch(TeamLeagueMatch teamLeagueMatch) {
        log.info("Got {} matches", matchesCount.incrementAndGet());
        com.blackmorse.hattrick.api.matchdetails.model.MatchDetails matchDetails = hattrick.getMatchDetails(teamLeagueMatch.getMatchId());
        HomeAwayTeam homeAwayTeam;
        if (matchDetails.getMatch().getHomeTeam().getHomeTeamId().equals(teamLeagueMatch.getTeamLeague().getTeamId())) {
            homeAwayTeam = matchDetails.getMatch().getHomeTeam();
        } else {
            homeAwayTeam = matchDetails.getMatch().getAwayTeam();
        }

        return MatchDetails.builder()
                .leagueId(teamLeagueMatch.getTeamLeague().getLeagueId())
                .divisionLevel(teamLeagueMatch.getTeamLeague().getLeagueLevel())
                .leagueUnitId(teamLeagueMatch.getTeamLeague().getLeagueLevelUnitId())
                .leagueUnitName(teamLeagueMatch.getTeamLeague().getLeagueUnitName())
                .teamId(teamLeagueMatch.getTeamLeague().getTeamId())
                .teamName(teamLeagueMatch.getTeamLeague().getTeamName())
                .date(matchDetails.getMatch().getMatchDate())
                .round(teamLeagueMatch.getTeamLeague().getCurrentMatchRound())
                .matchId(matchDetails.getMatch().getMatchId())
                .formation(homeAwayTeam.getFormation())
                .tacticType(homeAwayTeam.getTacticType())
                .tacticSkill(homeAwayTeam.getTacticSkill())
                .ratingMidfield(homeAwayTeam.getRatingMidfield())
                .ratingLeftDef(homeAwayTeam.getRatingLeftDef())
                .ratingMidDef(homeAwayTeam.getRatingMidDef())
                .ratingRightDef(homeAwayTeam.getRatingRightDef())
                .ratingLeftAtt(homeAwayTeam.getRatingLeftAtt())
                .ratingMidAtt(homeAwayTeam.getRatingMidAtt())
                .ratingRightAtt(homeAwayTeam.getRatingRightAtt())
                .ratingIndirectSetPiecesDef(homeAwayTeam.getRatingIndirectSetPiecesDef())
                .ratingIndirectSetPiecesAtt(homeAwayTeam.getRatingIndirectSetPiecesAtt())
            .build();
    }
}
