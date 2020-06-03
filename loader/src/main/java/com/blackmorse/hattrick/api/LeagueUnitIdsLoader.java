package com.blackmorse.hattrick.api;

import com.blackmorse.hattrick.api.search.model.Result;
import com.blackmorse.hattrick.api.search.model.Search;
import com.blackmorse.hattrick.model.League;
import com.blackmorse.hattrick.model.LeagueUnitId;
import com.sun.tools.javac.comp.Flow;
import io.reactivex.Flowable;
import io.reactivex.Scheduler;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    private static final Map<Integer, Long> higherLeagueMap = new HashMap<>();

    static {
        higherLeagueMap.put(1, 1L);
        higherLeagueMap.put(2, 512L);
        higherLeagueMap.put(3, 427L);
        higherLeagueMap.put(4, 724L);
        higherLeagueMap.put(5, 703L);
        higherLeagueMap.put(6, 682L);
        higherLeagueMap.put(7, 342L);
        higherLeagueMap.put(8, 597L);
        higherLeagueMap.put(9, 2110L);
        higherLeagueMap.put(11, 1769L);
        higherLeagueMap.put(12, 2280L);
        higherLeagueMap.put(14, 2195L);
        higherLeagueMap.put(15, 3208L);
        higherLeagueMap.put(16, 3229L);
        higherLeagueMap.put(17, 3314L);
        higherLeagueMap.put(18, 3335L);
        higherLeagueMap.put(19, 3377L);
        higherLeagueMap.put(20, 3488L);
        higherLeagueMap.put(21, 3573L);
        higherLeagueMap.put(22, 3594L);
        higherLeagueMap.put(23, 3615L);
        higherLeagueMap.put(24, 3620L);
        higherLeagueMap.put(25, 3705L);
        higherLeagueMap.put(26, 3166L);
        higherLeagueMap.put(27, 3161L);
        higherLeagueMap.put(28, 3013L);
        higherLeagueMap.put(29, 3008L);
        higherLeagueMap.put(30, 3140L);
        higherLeagueMap.put(31, 3119L);
        higherLeagueMap.put(32, 3098L);
        higherLeagueMap.put(33, 3398L);
        higherLeagueMap.put(34, 3356L);
        higherLeagueMap.put(35, 3187L);
        higherLeagueMap.put(36, 3403L);
        higherLeagueMap.put(37, 3854L);
        higherLeagueMap.put(38, 4200L);
        higherLeagueMap.put(39, 4205L);
        higherLeagueMap.put(44, 8714L);
        higherLeagueMap.put(45, 4213L);
        higherLeagueMap.put(46, 4206L);
        higherLeagueMap.put(47, 4211L);
        higherLeagueMap.put(50, 11345L);
        higherLeagueMap.put(51, 11324L);
        higherLeagueMap.put(52, 11303L);
        higherLeagueMap.put(53, 11450L);
        higherLeagueMap.put(54, 11408L);
        higherLeagueMap.put(55, 11429L);
        higherLeagueMap.put(56, 11366L);
        higherLeagueMap.put(57, 11471L);
        higherLeagueMap.put(58, 11387L);
        higherLeagueMap.put(59, 13508L);
        higherLeagueMap.put(60, 13531L);
        higherLeagueMap.put(61, 16623L);
        higherLeagueMap.put(62, 14234L);
        higherLeagueMap.put(63, 13680L);
        higherLeagueMap.put(64, 14213L);
        higherLeagueMap.put(66, 29747L);
        higherLeagueMap.put(67, 29768L);
        higherLeagueMap.put(68, 33138L);
        higherLeagueMap.put(69, 29726L);
        higherLeagueMap.put(70, 28425L);
        higherLeagueMap.put(71, 32093L);
        higherLeagueMap.put(72, 42133L);
        higherLeagueMap.put(73, 34841L);
        higherLeagueMap.put(74, 34840L);
        higherLeagueMap.put(75, 34872L);
        higherLeagueMap.put(76, 34871L);
        higherLeagueMap.put(77, 34870L);
        higherLeagueMap.put(79, 48896L);
        higherLeagueMap.put(80, 53781L);
        higherLeagueMap.put(81, 56879L);
        higherLeagueMap.put(83, 56880L);
        higherLeagueMap.put(84, 57433L);
        higherLeagueMap.put(85, 57518L);
        higherLeagueMap.put(88, 57539L);
        higherLeagueMap.put(89, 57560L);
        higherLeagueMap.put(91, 60146L);
        higherLeagueMap.put(93, 60150L);
        higherLeagueMap.put(94, 60148L);
        higherLeagueMap.put(95, 60149L);
        higherLeagueMap.put(96, 60151L);
        higherLeagueMap.put(97, 60147L);
        higherLeagueMap.put(98, 88340L);
        higherLeagueMap.put(99, 88257L);
        higherLeagueMap.put(100, 88256L);
        higherLeagueMap.put(101, 88258L);
        higherLeagueMap.put(102, 88341L);
        higherLeagueMap.put(103, 88259L);
        higherLeagueMap.put(104, 88382L);
        higherLeagueMap.put(105, 88385L);
        higherLeagueMap.put(106, 88390L);
        higherLeagueMap.put(107, 88447L);
        higherLeagueMap.put(110, 98772L);
        higherLeagueMap.put(111, 98793L);
        higherLeagueMap.put(112, 98814L);
        higherLeagueMap.put(113, 98835L);
        higherLeagueMap.put(117, 123048L);
        higherLeagueMap.put(118, 123069L);
        higherLeagueMap.put(119, 123090L);
        higherLeagueMap.put(120, 123111L);
        higherLeagueMap.put(121, 123132L);
        higherLeagueMap.put(122, 123133L);
        higherLeagueMap.put(123, 123188L);
        higherLeagueMap.put(124, 123209L);
        higherLeagueMap.put(125, 123210L);
        higherLeagueMap.put(126, 123211L);
        higherLeagueMap.put(127, 200087L);
        higherLeagueMap.put(128, 200092L);
        higherLeagueMap.put(129, 201137L);
        higherLeagueMap.put(130, 209686L);
        higherLeagueMap.put(131, 209708L);
        higherLeagueMap.put(132, 209729L);
        higherLeagueMap.put(133, 225688L);
        higherLeagueMap.put(134, 225713L);
        higherLeagueMap.put(135, 225734L);
        higherLeagueMap.put(136, 229917L);
        higherLeagueMap.put(137, 229916L);
        higherLeagueMap.put(138, 237126L);
        higherLeagueMap.put(139, 238747L);
        higherLeagueMap.put(140, 238748L);
        higherLeagueMap.put(141, 238789L);
        higherLeagueMap.put(142, 238790L);
        higherLeagueMap.put(143, 245936L);
        higherLeagueMap.put(144, 245935L);
        higherLeagueMap.put(145, 252316L);
        higherLeagueMap.put(146, 252313L);
        higherLeagueMap.put(147, 252358L);
        higherLeagueMap.put(148, 252357L);
        higherLeagueMap.put(149, 258094L);
        higherLeagueMap.put(151, 258136L);
        higherLeagueMap.put(152, 258052L);
        higherLeagueMap.put(153, 258115L);
        higherLeagueMap.put(154, 258073L);
        higherLeagueMap.put(155, 258477L);
        higherLeagueMap.put(156, 258498L);
        higherLeagueMap.put(157, 258519L);
        higherLeagueMap.put(158, 258540L);
        higherLeagueMap.put(159, 258561L);
        higherLeagueMap.put(160, 258582L);
        higherLeagueMap.put(1000, 256687L);
    }

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
                IntStream.range(1, league.getMaxLevel() + 1).mapToObj(level -> new LeagueWithLevel(league, level)).collect(Collectors.toList())
        ))
                .parallel()
                .runOn(scheduler)
                .flatMap(leagueWithLevel -> {
                    List<LeagueUnitId> res = new ArrayList<>();
                    if (leagueWithLevel.league.getId() != 1 /* Sweden */) {
                        if (leagueWithLevel.getLevel() == 1) {
                            res.add(LeagueUnitId.builder().league(leagueWithLevel.league).id(higherLeagueMap.get(leagueWithLevel.league.getId())).build());
                            log.debug("{} league units loaded", leagueUnitIdCounter.incrementAndGet());
                        } else {
                            Search search = hattrick.searchLeagueUnits(leagueWithLevel.league.getId(), Hattrick.arabToRomans.get(leagueWithLevel.level) + ".", 0);
                            IntStream.range(0, search.getPages()).forEach(page -> {
                                Search searchPage = hattrick.searchLeagueUnits(leagueWithLevel.league.getId(), Hattrick.arabToRomans.get(leagueWithLevel.level) + ".", page);
                                searchPage.getSearchResults().forEach(result -> {
                                    log.debug("{} league units loaded", leagueUnitIdCounter.incrementAndGet());
                                    res.add(LeagueUnitId.builder().league(leagueWithLevel.league).id(result.getResultId()).build());
                                });
                            });
                        }
                    } else {
                        if (leagueWithLevel.getLevel() == 1) {
                            res.add(LeagueUnitId.builder().league(leagueWithLevel.league).id(higherLeagueMap.get(leagueWithLevel.league.getId())).build());
                            log.debug("{} league units loaded", leagueUnitIdCounter.incrementAndGet());
                        } else if (leagueWithLevel.getLevel() == 2) {
                            List<Result> searchResults = hattrick.searchLeagueUnits(leagueWithLevel.getLeague().getId(), "Ia", 0)
                                    .getSearchResults();
                            Long baseNumber = searchResults.get(0).getResultId();

                            IntStream.range(0, 4).mapToObj(i -> LeagueUnitId.builder().league(leagueWithLevel.league).id(baseNumber + i).build())
                                .forEach(leagueUnitId -> {
                                    log.debug("{} league units loaded", leagueUnitIdCounter.incrementAndGet());
                                    res.add(leagueUnitId);
                                });
                        } else if (leagueWithLevel.getLevel() == 3) {
                            List<Result> searchResults = hattrick.searchLeagueUnits(leagueWithLevel.getLeague().getId(), "IIa", 0)
                                    .getSearchResults();
                            Long baseNumber = searchResults.get(0).getResultId();

                            IntStream.range(0, 16).mapToObj(i -> LeagueUnitId.builder().league(leagueWithLevel.league).id(baseNumber + i).build())
                                    .forEach(leagueUnitId -> {
                                        log.debug("{} league units loaded", leagueUnitIdCounter.incrementAndGet());
                                        res.add(leagueUnitId);
                                    });
                        } else {
                            Search search = hattrick.searchLeagueUnits(leagueWithLevel.league.getId(), Hattrick.arabToRomans.get(leagueWithLevel.level - 1) + ".", 0);
                            IntStream.range(0, search.getPages()).forEach(page -> {
                                Search searchPage = hattrick.searchLeagueUnits(leagueWithLevel.league.getId(), Hattrick.arabToRomans.get(leagueWithLevel.level - 1) + ".", page);
                                searchPage.getSearchResults().forEach(result -> {
                                    log.debug("{} league units loaded", leagueUnitIdCounter.incrementAndGet());
                                    res.add(LeagueUnitId.builder().league(leagueWithLevel.league).id(result.getResultId()).build());
                                });
                            });
                        }
                    }


//                    }
                    return Flowable.fromIterable(res);
                })

                .sequential().toList().blockingGet();
    }

    @Data
    private static class LeagueWithLevel {
        private final League league;
        private final Integer level;
    }

    @Data
    private static class LeagueWithLevelWithPage {
        private final League league;
        private final Integer level;
        private final Integer page;
    }
}
