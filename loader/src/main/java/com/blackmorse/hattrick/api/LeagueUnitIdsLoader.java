package com.blackmorse.hattrick.api;

import com.blackmorse.hattrick.api.search.model.Result;
import com.blackmorse.hattrick.model.League;
import com.blackmorse.hattrick.model.LeagueUnitId;
import io.reactivex.Flowable;
import io.reactivex.Scheduler;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

@Component
@Slf4j
public class LeagueUnitIdsLoader {
    private final Hattrick hattrick;
    private final Scheduler scheduler;
    private final AtomicLong leagueUnitIdCounter = new AtomicLong();

    @Autowired
    public LeagueUnitIdsLoader(Hattrick hattrick,
                               @Qualifier("apiExecutor") ExecutorService executorService) {
        this.hattrick = hattrick;
        this.scheduler = io.reactivex.schedulers.Schedulers.from(executorService);
    }

    public List<LeagueUnitId> load(List<String> countryNames) {

        /*Flowable<LeagueUnitId> leagueIds = */ return Flowable.fromIterable(
                hattrick.getWorldDetails().getLeagueList()
                        .stream()
                        .filter(league -> countryNames.contains(league.getLeagueName())).collect(Collectors.toList()))
                .map(league -> League.builder()
                        .id(league.getLeagueId())
                        .nextRound(league.getMatchRound())
                        .seasonOffset(league.getSeasonOffset())
                        .maxLevel(league.getNumberOfLevels())
                        .build())
        .flatMap(league -> Flowable.fromIterable(
                IntStream.range(2, league.getMaxLevel() + 1).mapToObj(level -> new LeagueWithLevel(league, level)).collect(Collectors.toList())
        ))
                .parallel()
                .runOn(scheduler)
                .flatMap(leagueWithLevel -> {
                    log.info("{} league levels loaded", leagueUnitIdCounter.incrementAndGet());
                    if (leagueWithLevel.league.getId() != 1 /* Sweden */) {

                        Long baseNumber;
                        if (leagueWithLevel.getLevel() == 1) {
                            List<Result> searchResults = hattrick.searchLeagueUnits(leagueWithLevel.getLeague().getId(), "II.1", 0)
                                    .getSearchResults();
                            baseNumber = searchResults.get(0).getResultId() - 1;
                        } else {
                            List<Result> searchResults = hattrick.searchLeagueUnits(leagueWithLevel.getLeague().getId(), Hattrick.arabToRomans.get(leagueWithLevel.getLevel()) + ".1", 0)
                                    .getSearchResults();
                            baseNumber = searchResults.get(0).getResultId();
                        }

                        List<LeagueUnitId> leagueUnitIdList = LongStream.range(baseNumber, baseNumber + Hattrick.leagueLevelNumberTeams.get(leagueWithLevel.getLevel()))
                                .mapToObj(number -> LeagueUnitId.builder()
                                        .league(leagueWithLevel.league)
                                        .id(number)
                                        .build())
                                .collect(Collectors.toList());

                        return Flowable.fromIterable(leagueUnitIdList);
                    } else {
                        Long baseNumber;
                        if (leagueWithLevel.getLevel() == 1) {
                            List<Result> searchResults = hattrick.searchLeagueUnits(leagueWithLevel.getLeague().getId(), "Ia", 0)
                                    .getSearchResults();
                            baseNumber = searchResults.get(0).getResultId() - 1;
                        } else if (leagueWithLevel.getLevel() == 2) {
                            List<Result> searchResults = hattrick.searchLeagueUnits(leagueWithLevel.getLeague().getId(), "Ia", 0)
                                    .getSearchResults();
                            baseNumber = searchResults.get(0).getResultId();
                        } else if (leagueWithLevel.getLevel() == 3) {
                            List<Result> searchResults = hattrick.searchLeagueUnits(leagueWithLevel.getLeague().getId(), "IIa", 0)
                                    .getSearchResults();
                            baseNumber = searchResults.get(0).getResultId();
                        } else {
                            List<Result> searchResults = hattrick.searchLeagueUnits(leagueWithLevel.getLeague().getId(), Hattrick.arabToRomans.get(leagueWithLevel.getLevel() - 1) + ".1", 0)
                                    .getSearchResults();
                            baseNumber = searchResults.get(0).getResultId();
                        }


                        List<LeagueUnitId> leagueUnitIdList = LongStream.range(baseNumber, baseNumber + Hattrick.leagueLevelNumberTeams.get(leagueWithLevel.getLevel()) + 1)
                                .mapToObj(number -> LeagueUnitId.builder()
                                        .league(leagueWithLevel.league)
                                        .id(number)
                                        .build())
                                .collect(Collectors.toList());

                        return Flowable.fromIterable(leagueUnitIdList);
                    }
                }).sequential().toList().blockingGet();
    }

    @Data
    private static class LeagueWithLevel {
        private final League league;
        private final Integer level;
    }
}
