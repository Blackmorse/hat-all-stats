package com.blackmorse.hattrick;

import com.blackmorse.hattrick.api.Hattrick;
import com.blackmorse.hattrick.api.leaguedetails.model.LeagueDetails;
import com.blackmorse.hattrick.api.matchdetails.model.HomeAwayTeam;
import com.blackmorse.hattrick.clickhouse.model.MatchDetails;
import com.blackmorse.hattrick.clickhouse.model.PlayerRating;
import com.blackmorse.hattrick.model.LeagueInfo;
import com.blackmorse.hattrick.model.TeamLeague;
import com.blackmorse.hattrick.model.TeamLeagueMatch;
import com.blackmorse.hattrick.model.enums.MatchType;
import com.blackmorse.hattrick.subscribers.MatchDetailsSubscriber;
import lombok.Data;
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
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
@Slf4j
public class HistoryLoader {
    @Data
    private static class LeagueInfoWithLeagueUnitDetails {
        private final LeagueInfo leagueInfo;
        private final LeagueDetails leagueDetails;
    }

    @Data
    private static class LeagueInfoWithLeagueUnitId {
        private final LeagueInfo leagueInfo;
        private final Long leagueUnitId;
    }

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


    public void load(List<String> countryNames, Integer seasonNumber) {
        ParallelFlux<TeamLeagueMatch> matchesFlux = Flux.fromStream(
                hattrick.getWorldDetails().getLeagueList().stream()
                        .filter(league -> countryNames.contains(league.getLeagueName())))
                .map(league ->
                        new LeagueInfo(league.getLeagueId(), league.getSeriesMatchDate(), league.getMatchRound(),
                                league.getSeasonOffset()))
                .map(leagueInfo -> new LeagueInfoWithLeagueUnitDetails(leagueInfo, hattrick.getLeagueUnitByName(leagueInfo.getLeagueId(), "II.1")))
                .flatMap(leagueInfoWithTwoOneDetails -> getAllLeagueIds2(leagueInfoWithTwoOneDetails.getLeagueInfo(), leagueInfoWithTwoOneDetails.leagueDetails))
                .flatMap(Flux::fromIterable)
                .parallel()

                .runOn(scheduler)
                .map(leagueInfoWithLeagueUnitId -> new LeagueInfoWithLeagueUnitDetails(leagueInfoWithLeagueUnitId.getLeagueInfo(), hattrick.getLeagueUnitById(leagueInfoWithLeagueUnitId.getLeagueUnitId())))
                .flatMap(this::teamsFromLeague)
                .flatMap((TeamLeague teamLeague) -> getMatches(teamLeague, seasonNumber));

        matchesFlux
                .map(this::matchDetailsFromMatch)
                .subscribe(matchDetailsSubscriber::onNext, matchDetailsSubscriber::onError, matchDetailsSubscriber::onComplete);
    }


    private Flux<TeamLeague> teamsFromLeague(LeagueInfoWithLeagueUnitDetails info) {

        return Flux.fromStream(
                info.getLeagueDetails().getTeams().
                        stream().
                        filter(team -> team.getUserId() != 0L)
                        .map(team -> TeamLeague.builder()
                                .leagueId(info.getLeagueDetails().getLeagueId())
                                .leagueLevel(info.getLeagueDetails().getLeagueLevel())
                                .leagueLevelUnitId(info.getLeagueDetails().getLeagueLevelUnitId())
                                .leagueUnitName(info.getLeagueDetails().getLeagueLevelUnitName())
                                .nextMatchRound(info.getLeagueInfo().getNextRound())
                                .nextRoundDate(info.getLeagueInfo().getNextLeagueMatch())
                                .teamId(team.getTeamId())
                                .teamName(team.getTeamName())
                                .seasonOffset(info.getLeagueInfo().getSeasonOffset())
                            .build())
        );
    }

    private Flux<List<LeagueInfoWithLeagueUnitId>> getAllLeagueIds2(LeagueInfo leagueInfo, LeagueDetails twoOne) {
        return Mono.just(Collections.singletonList(new LeagueInfoWithLeagueUnitId(leagueInfo, twoOne.getLeagueLevelUnitId() - 1)))
                .concatWith(Flux.fromStream(IntStream.range(2, twoOne.getMaxLevel() + 1)
                            .mapToObj(level -> hattrick.getLeagueUnitIdsForLevel(twoOne.getLeagueId(), level)
                                                .stream().map(id -> new LeagueInfoWithLeagueUnitId(leagueInfo, id))
                                                    .collect(Collectors.toList()))));
    }

    private Flux<TeamLeagueMatch> getMatches(TeamLeague teamLeague, Integer seasonNumber) {
        log.info("get {} teams", teams.incrementAndGet());
        return Flux.fromStream(
                hattrick.getArchiveMatches(teamLeague.getTeamId(), seasonNumber + teamLeague.getSeasonOffset()).getTeam().getMatchList()
                        .stream()
                        .filter(match -> match.getMatchType().equals(MatchType.LEAGUE_MATCH))
                        .map(match -> TeamLeagueMatch.builder()
                                .matchId(match.getMatchId())
                                .date(match.getMatchDate())
                                .matchRound(roundNumber(match.getMatchDate(), teamLeague.getNextRoundDate(), teamLeague.getNextMatchRound()))
                                .teamLeague(teamLeague)
                                .season(seasonNumber)
                            .build())
        );
    }

    private Integer roundNumber(Date matchDate, Date lastLeagueMatchDate, Integer currentRound) {
        long daysBefore = (lastLeagueMatchDate.getTime() - matchDate.getTime()) / (1000 * 60 * 60 * 24 * 7);
        return (int)(currentRound - daysBefore);
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
                        .round(teamLeagueMatch.getTeamLeague().getNextMatchRound())
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
                .season(teamLeagueMatch.getSeason())
                .leagueId(teamLeagueMatch.getTeamLeague().getLeagueId())
                .divisionLevel(teamLeagueMatch.getTeamLeague().getLeagueLevel())
                .leagueUnitId(teamLeagueMatch.getTeamLeague().getLeagueLevelUnitId())
                .leagueUnitName(teamLeagueMatch.getTeamLeague().getLeagueUnitName())
                .teamId(teamLeagueMatch.getTeamLeague().getTeamId())
                .teamName(teamLeagueMatch.getTeamLeague().getTeamName())
                .date(matchDetails.getMatch().getMatchDate())
                .round(teamLeagueMatch.getMatchRound())
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
