package com.blackmorse.hattrick;

import com.blackmorse.hattrick.api.Hattrick;
import com.blackmorse.hattrick.api.matchdetails.model.HomeAwayTeam;
import com.blackmorse.hattrick.clickhouse.MatchDetails;
import com.blackmorse.hattrick.clickhouse.PlayerRating;
import com.blackmorse.hattrick.model.TeamLeague;
import com.blackmorse.hattrick.model.TeamLeagueMatch;
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
public class Loader {

    private final Scheduler scheduler;
    private final Hattrick hattrick;

    @Autowired
    public Loader(@Qualifier("apiExecutor") ExecutorService executorService,
                  Hattrick hattrick) {
        this.scheduler = Schedulers.fromExecutor(executorService);
        this.hattrick = hattrick;
    }

    public void load(List<Integer> countryTeamIds) {
        ParallelFlux<TeamLeagueMatch> matchesFlux = Flux.fromIterable(countryTeamIds)
                .map(countryTeamId -> hattrick.getNationalTeamDetails(countryTeamId).getTeam().getLeague().getLeagueID())
                .map(leagueId -> hattrick.getLeagueUnitByName(leagueId, "II.1"))
                .flatMap(twoOne -> Mono.just(Collections.singletonList((long) (twoOne.getLeagueLevelUnitId() - 1)))
                        .concatWith(Flux.fromStream(
                                IntStream.range(2, twoOne.getMaxLevel() + 1)
                                        .mapToObj(level -> hattrick.getLeagueUnitIdsForLevel(twoOne.getLeagueId(), level)))
                        ))

                .flatMap(Flux::fromIterable)
                .parallel()
                .runOn(scheduler)
                .map(hattrick::getLeagueUnitById)
                .flatMap(league -> Flux.fromStream(
                        league.getTeams().
                                stream().
                                filter(team -> team.getUserId() != 0L)
                                .map(team -> TeamLeague.builder()
                                        .leagueId(league.getLeagueId())
                                        .leagueLevel(league.getLeagueLevel())
                                        .leagueLevelUnitId(league.getLeagueLevelUnitId())
                                        .currentMatchRound(league.getCurrentMatchRound())
                                        .teamId(team.getTeamId())
                                        .teamName(team.getTeamName())
                                        .build())
                ))
                .runOn(scheduler)
                .flatMap(teamLeague -> Flux.fromStream(
                        hattrick.getArchiveMatches(teamLeague.getTeamId(), 61).getTeam().getMatchList()
                                .stream()
                                .map(match -> TeamLeagueMatch.builder()
                                        .matchId(match.getMatchId())
                                        .date(match.getMatchDate())
                                        .leagueTeamId(teamLeague)
                                        .build())
                ))
                .runOn(scheduler);

        matchesFlux
                .flatMap(teamLeagueMatch -> Flux.fromStream(hattrick.getMatchLineUp(teamLeagueMatch.getMatchId(), teamLeagueMatch.getMatchId())
                    .getTeam().getLineUp()
                        .stream()
                        .map(lineUpPlayer -> PlayerRating.builder()
                                .leagueId(teamLeagueMatch.getLeagueTeamId().getLeagueId())
                                .divisionLevel(teamLeagueMatch.getLeagueTeamId().getLeagueLevel())
                                .leagueUnitId(teamLeagueMatch.getLeagueTeamId().getLeagueLevelUnitId())
                                .teamId(teamLeagueMatch.getLeagueTeamId().getTeamId())
                                .teamName(teamLeagueMatch.getLeagueTeamId().getTeamName())
                                .date(teamLeagueMatch.getDate())
                                .round(teamLeagueMatch.getLeagueTeamId().getCurrentMatchRound())
                                .matchId(teamLeagueMatch.getMatchId())
                                .playerId(lineUpPlayer.getPlayerId())
                                .roleId(lineUpPlayer.getRoleId())
                                .firstName(lineUpPlayer.getFirstName())
                                .lastName(lineUpPlayer.getLastName())
                                .ratingStars(lineUpPlayer.getRatingStars())
                                .ratingStars(lineUpPlayer.getRatingStarsEndOfMatch())
                                .behaviour(lineUpPlayer.getBehaviour())
                                .build())
                        )
                )
                .subscribe(System.out::println);

        AtomicLong matches = new AtomicLong();

        matchesFlux
                .map(teamLeagueMatch -> {
                    com.blackmorse.hattrick.api.matchdetails.model.MatchDetails matchDetails = hattrick.getMatchDetails(teamLeagueMatch.getMatchId());
                    HomeAwayTeam homeAwayTeam;
                if (matchDetails.getMatch().getHomeTeam().getHomeTeamId().equals(teamLeagueMatch.getLeagueTeamId().getTeamId())) {
                    homeAwayTeam = matchDetails.getMatch().getHomeTeam();
                } else {
                    homeAwayTeam = matchDetails.getMatch().getAwayTeam();
                }
                matches.incrementAndGet();
                    System.out.println(matches.get());

                return MatchDetails.builder()
                        .leagueId(teamLeagueMatch.getLeagueTeamId().getLeagueId())
                        .divisionLevel(teamLeagueMatch.getLeagueTeamId().getLeagueLevel())
                        .leagueUnitId(teamLeagueMatch.getLeagueTeamId().getLeagueLevelUnitId())
                        .teamId(teamLeagueMatch.getLeagueTeamId().getTeamId())
                        .teamName(teamLeagueMatch.getLeagueTeamId().getTeamName())
                        .date(matchDetails.getMatch().getMatchDate())
                        .round(teamLeagueMatch.getLeagueTeamId().getCurrentMatchRound())
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
                })
                .subscribe(System.out::println);
    }
}
