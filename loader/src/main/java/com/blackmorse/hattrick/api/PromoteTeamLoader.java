package com.blackmorse.hattrick.api;

import com.blackmorse.hattrick.api.leaguedetails.model.LeagueDetails;
import com.blackmorse.hattrick.api.leaguefixtures.model.LeagueFixtures;
import com.blackmorse.hattrick.model.LeagueUnit;
import com.blackmorse.hattrick.model.Team;
import com.blackmorse.hattrick.promotions.model.PromoteTeam;
import com.google.common.collect.Streams;
import io.reactivex.Flowable;
import io.reactivex.Scheduler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@Slf4j
public class PromoteTeamLoader {
    private final Scheduler scheduler;
    private final Hattrick hattrick;

    public PromoteTeamLoader(@Qualifier("apiExecutor") ExecutorService executorService,
                             Hattrick hattrick) {
        this.scheduler = io.reactivex.schedulers.Schedulers.from(executorService);
        this.hattrick = hattrick;
    }

    public List<PromoteTeam> getPromoteTeams(List<LeagueUnit> leagueUnits) {
        AtomicLong pomotionsLeaguesCounter = new AtomicLong();
        return Flowable.fromIterable(leagueUnits)
                .parallel()
                .runOn(scheduler)
                .flatMap(leagueUnit -> {
                    LeagueDetails leagueDetails = hattrick.getLeagueUnitById(leagueUnit.getId());

                    log.debug("Loaded {} leagues for promotions", pomotionsLeaguesCounter.incrementAndGet());

                    return Flowable.fromIterable(leagueDetails.getTeams().stream()
                        .map(htTeam -> {
                            Team team = Team.builder()
                                    .leagueUnit(leagueUnit)
                                    .id(htTeam.getTeamId())
                                    .name(htTeam.getTeamName())
                                    .build();

                            return PromoteTeam.builder()
                                    .team(team)
                                    .position(htTeam.getPosition())
                                    .points(htTeam.getPoints())
                                    .diff(htTeam.getGoalsFor() - htTeam.getGoalsAgainst())
                                    .scored(htTeam.getGoalsFor())
                                    .season(hattrick.getSeason())
                                .build();
                        }).collect(Collectors.toList()));
                })
                .sequential()
                .toList()
                .blockingGet();
    }

    public List<PromoteTeam> getHistoryPromoteTeams(List<LeagueUnit> leagueUnits, Integer season) {

        return Flowable.fromIterable(leagueUnits)
                .parallel()
                .runOn(scheduler)
                .flatMap(leagueUnit -> {
                    Integer ofsettedSeason = season + leagueUnit.getLeague().getSeasonOffset();
                    LeagueFixtures leagueFixture = hattrick.getLeagueFixture(leagueUnit.getId(), ofsettedSeason);
                    log.debug("Loaded {} leagues for promotions", pomotionsLeaguesCounter.incrementAndGet());

                    return Flowable.fromIterable(getTeamsFromFixture(leagueFixture, leagueUnit, season));
                })
                .sequential().toList().blockingGet();
    }

    private List<PromoteTeam> getTeamsFromFixture(LeagueFixtures leagueFixture, LeagueUnit leagueUnit, Integer season) {
        Map<Long, PromoteTeam> collect = leagueFixture.getMatches().stream().skip(52)
            .flatMap(matc ->
                Stream.of(
                        PromoteTeam.builder()
                            .team(Team.builder()
                                        .leagueUnit(leagueUnit)
                                        .id(matc.getHomeTeam().getHomeTeamId())
                                        .name(matc.getHomeTeam().getHomeTeamName())
                                    .build()
                            )
                            .season(season)
                            .points(0)
                            .diff(0)
                            .scored(0)
                        .build(),
                        PromoteTeam.builder()
                            .team(Team.builder()
                                        .leagueUnit(leagueUnit)
                                        .id(matc.getAwayTeam().getAwayTeamId())
                                        .name(matc.getAwayTeam().getAwayTeamName())
                                    .build())
                            .season(season)
                            .points(0)
                            .diff(0)
                            .scored(0)
                        .build()
                ))
                .collect(Collectors.toMap(promoteTeam -> promoteTeam.getTeam().getId(), Function.identity()));

        for (com.blackmorse.hattrick.api.leaguefixtures.model.Match match : leagueFixture.getMatches()) {
            PromoteTeam homeTeamRatings = collect.get(match.getHomeTeam().getHomeTeamId());
            PromoteTeam awayTeamRatings = collect.get(match.getAwayTeam().getAwayTeamId());

            homeTeamRatings.diff += match.getHomeGoals() - match.getAwayGoals();
            awayTeamRatings.diff += match.getAwayGoals() - match.getHomeGoals();

            homeTeamRatings.scored += match.getHomeGoals();
            awayTeamRatings.scored += match.getAwayGoals();

            if (match.getHomeGoals() > match.getAwayGoals()) {
                homeTeamRatings.points += 3;
            } else if(match.getAwayGoals() > match.getHomeGoals()) {
                awayTeamRatings.points += 3;
            } else {
                homeTeamRatings.points += 1;
                awayTeamRatings.points += 1;
            }
        }

        return Streams.mapWithIndex(
                collect.entrySet().stream().map(Map.Entry::getValue)
                        .sorted(Comparator.comparing(PromoteTeam::getPoints).thenComparing(PromoteTeam::getDiff).thenComparing(PromoteTeam::getScored).reversed()),
                (teamRating, index) -> {
                    teamRating.position = (int) index + 1;
                    return teamRating;
                }
        ).collect(Collectors.toList());
    }
}
