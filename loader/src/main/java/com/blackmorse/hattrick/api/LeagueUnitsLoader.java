package com.blackmorse.hattrick.api;

import com.blackmorse.hattrick.api.search.model.Result;
import com.blackmorse.hattrick.api.search.model.Search;
import com.blackmorse.hattrick.common.CommonData;
import com.blackmorse.hattrick.model.League;
import com.blackmorse.hattrick.model.LeagueUnit;
import io.reactivex.Flowable;
import io.reactivex.Scheduler;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.blackmorse.hattrick.common.CommonData.higherLeagueMap;

@Component
@Slf4j
public class LeagueUnitsLoader {
    private final Hattrick hattrick;
    private final Scheduler scheduler;

    @Autowired
    public LeagueUnitsLoader(Hattrick hattrick,
                             @Qualifier("apiExecutor") ExecutorService executorService) {
        this.hattrick = hattrick;
        this.scheduler = io.reactivex.schedulers.Schedulers.from(executorService);
    }

    public List<LeagueUnit> load(String countryName) {
        AtomicLong leagueUnitIdCounter = new AtomicLong();
        return Flowable.fromIterable(
                hattrick.getWorldDetails().getLeagueList()
                        .stream()
                        .filter(league -> countryName.equals(league.getLeagueName())).collect(Collectors.toList()))
                .map(league -> League.builder()
                        .id(league.getLeagueId())
                        .nextRound(league.getMatchRound())
                        .seasonOffset(league.getSeasonOffset())
                        .maxLevel(league.getNumberOfLevels())
                        .build())
        .flatMap(league -> Flowable.fromIterable(
                IntStream.range(1, league.getMaxLevel() + 1).mapToObj(level -> new LeagueWithLevel(league, level)).collect(Collectors.toList())
        ))
                .parallel()
                .runOn(scheduler)
                .flatMap(leagueWithLevel -> {
                    List<LeagueUnit> res = new ArrayList<>();
                    if (leagueWithLevel.league.getId() != 1 /* Sweden */) {
                        if (leagueWithLevel.getLevel() == 1) {
                            res.add(LeagueUnit.builder()
                                    .level(leagueWithLevel.level)
                                    .league(leagueWithLevel.league)
                                    .name(higherLeagueMap.get(leagueWithLevel.getLeague().getId()).getLeagueUnitName())
                                    .id(higherLeagueMap.get(leagueWithLevel.league.getId()).getLeagueUnitId())
                                .build());
                            log.debug("{} league units loaded", leagueUnitIdCounter.incrementAndGet());
                        } else {
                            Search search = hattrick.searchLeagueUnits(leagueWithLevel.league.getId(), CommonData.arabToRomans.get(leagueWithLevel.level) + ".", 0);
                            IntStream.range(0, search.getPages()).forEach(page -> {
                                Search searchPage = hattrick.searchLeagueUnits(leagueWithLevel.league.getId(), CommonData.arabToRomans.get(leagueWithLevel.level) + ".", page);
                                searchPage.getSearchResults().forEach(result -> {
                                    log.debug("{} league units loaded", leagueUnitIdCounter.incrementAndGet());
                                    res.add(LeagueUnit.builder()
                                            .league(leagueWithLevel.league)
                                            .level(leagueWithLevel.level)
                                            .name(result.getResultName())
                                            .id(result.getResultId())
                                        .build());
                                });
                            });
                        }
                    } else {
                        if (leagueWithLevel.getLevel() == 1) {
                            res.add(LeagueUnit.builder()
                                    .level(leagueWithLevel.level)
                                    .league(leagueWithLevel.league)
                                    .id(higherLeagueMap.get(leagueWithLevel.league.getId()).getLeagueUnitId())
                                    .name(higherLeagueMap.get(leagueWithLevel.league.getId()).getLeagueUnitName())
                                .build());
                            log.debug("{} league units loaded", leagueUnitIdCounter.incrementAndGet());
                        } else if (leagueWithLevel.getLevel() == 2) {
                            List<Result> searchResults = hattrick.searchLeagueUnits(leagueWithLevel.getLeague().getId(), "Ia", 0)
                                    .getSearchResults();
                            Long baseNumber = searchResults.get(0).getResultId();

                            IntStream.range(0, 4).mapToObj(i -> LeagueUnit.builder()
                                        .league(leagueWithLevel.league)
                                        .level(leagueWithLevel.level)
                                        .name(String.valueOf('a' + i))
                                        .id(baseNumber + i)
                                    .build())
                                .forEach(leagueUnitId -> {
                                    log.debug("{} league units loaded", leagueUnitIdCounter.incrementAndGet());
                                    res.add(leagueUnitId);
                                });
                        } else if (leagueWithLevel.getLevel() == 3) {
                            List<Result> searchResults = hattrick.searchLeagueUnits(leagueWithLevel.getLeague().getId(), "IIa", 0)
                                    .getSearchResults();
                            Long baseNumber = searchResults.get(0).getResultId();

                            IntStream.range(0, 16).mapToObj(i -> LeagueUnit.builder()
                                            .level(leagueWithLevel.level)
                                            .league(leagueWithLevel.league)
                                            .name(String.valueOf('a' + i))
                                            .id(baseNumber + i)
                                        .build())
                                    .forEach(leagueUnitId -> {
                                        log.debug("{} league units loaded", leagueUnitIdCounter.incrementAndGet());
                                        res.add(leagueUnitId);
                                    });
                        } else {
                            Search search = hattrick.searchLeagueUnits(leagueWithLevel.league.getId(), CommonData.arabToRomans.get(leagueWithLevel.level - 1) + ".", 0);
                            IntStream.range(0, search.getPages()).forEach(page -> {
                                Search searchPage = hattrick.searchLeagueUnits(leagueWithLevel.league.getId(), CommonData.arabToRomans.get(leagueWithLevel.level - 1) + ".", page);
                                searchPage.getSearchResults().forEach(result -> {
                                    log.debug("{} league units loaded", leagueUnitIdCounter.incrementAndGet());
                                    res.add(LeagueUnit.builder()
                                            .level(leagueWithLevel.level)
                                            .name(result.getResultName())
                                            .league(leagueWithLevel.league)
                                            .id(result.getResultId())
                                        .build());
                                });
                            });
                        }
                    }
                    return Flowable.fromIterable(res);
                })
                .sequential().toList().blockingGet();
    }

    @Data
    private static class LeagueWithLevel {
        private final League league;
        private final Integer level;
    }
}
