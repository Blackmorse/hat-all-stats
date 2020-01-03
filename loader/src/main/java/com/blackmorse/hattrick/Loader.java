package com.blackmorse.hattrick;

import com.blackmorse.hattrick.api.Hattrick;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
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
        Flux.fromIterable(countryTeamIds)
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
                        league.getTeams().stream().filter(team -> team.getUserId() != 0L)
                ))
                .runOn(scheduler)
                .flatMap(team -> Flux.fromIterable(hattrick.getArchiveMatches(team.getTeamId(), 61).getTeam().getMatchList()));
//                .subscribe(match -> System.out.println(match.getHomeTeam().getHomeTeamName() + " vs " + match.getAwayTeam().getAwayTeamName() + ":"  +
//                                " on thread: " + Thread.currentThread().getName()));

    }
}
