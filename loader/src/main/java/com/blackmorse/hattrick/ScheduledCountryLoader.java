package com.blackmorse.hattrick;

import com.blackmorse.hattrick.api.AlltidLike;
import com.blackmorse.hattrick.api.Hattrick;
import com.blackmorse.hattrick.api.worlddetails.model.League;
import com.blackmorse.hattrick.api.worlddetails.model.WorldDetails;
import com.blackmorse.hattrick.telegram.Telegram;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@Slf4j
public class ScheduledCountryLoader {
    static final Map<Integer, Integer> countriesToMinutesOffset = new HashMap<>();

    static {
        countriesToMinutesOffset.put(24, 90); //Poland
        countriesToMinutesOffset.put(4, 30); //Italy
        countriesToMinutesOffset.put(36, 60); //Spain
        countriesToMinutesOffset.put(46,135); //Switzerland
        countriesToMinutesOffset.put(3, 30); //Germany
    }

    @AllArgsConstructor
    @Getter
    public final static class LeagueTime {
        public League league;
        public Date time;
    }

    private final Hattrick hattrick;
    private final CountriesLastLeagueMatchLoader countriesLastLeagueMatchLoader;
    private final Telegram telegram;
    private final AlltidLike alltidLike;

    @Autowired
    public ScheduledCountryLoader(Hattrick hattrick,
                                  CountriesLastLeagueMatchLoader countriesLastLeagueMatchLoader,
                                  Telegram telegram,
                                  AlltidLike alltidLike) {
        this.hattrick = hattrick;
        this.countriesLastLeagueMatchLoader = countriesLastLeagueMatchLoader;
        this.telegram = telegram;
        this.alltidLike = alltidLike;
    }

    public void loadFrom(Optional<String> country) {
        AtomicBoolean isOver = new AtomicBoolean(false);

        Timer timer = new Timer();
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        WorldDetails worldDetails = hattrick.getWorldDetails();

        List<LeagueTime> leagueTimes;
        if (country.isPresent()) {
            leagueTimes = worldDetails.getLeagueList().stream()
                    .map(league -> {
                        Date seriesMatchDate = null;
                        if (league.getSeriesMatchDate().getTime() - new Date().getTime()
                                > 1000 * 3600 * 24 * 3) {
                            seriesMatchDate = new Date(league.getSeriesMatchDate().getTime() - 1000 * 3600 * 24 * 7);
                        } else {
                            seriesMatchDate = league.getSeriesMatchDate();
                        }

                        Integer minutesOffset = countriesToMinutesOffset.getOrDefault(league.getLeagueId(), 0);
                        return new LeagueTime(league,
                                new Date(seriesMatchDate.getTime() + 1000 * 60 * 60 * 3 + minutesOffset * 60 * 1000));
                    })
                    .sorted(Comparator.comparing(LeagueTime::getTime))
                    .dropWhile(leagueTime -> !leagueTime.league.getLeagueName().equals(country.get()))
                    .collect(Collectors.toList());
        } else {
            leagueTimes = worldDetails.getLeagueList().stream()
                    .flatMap(league -> {
                        if (league.getMatchRound() - 1 > 14) {
                            log.info("Round {} for country ({}, {}). Nothing to load", league.getMatchRound(), league.getLeagueId(), league.getEnglishName());
                            return Stream.empty();
                        }
                        Integer minutesOffset = countriesToMinutesOffset.getOrDefault(league.getLeagueId(), 0);

                        Date time = new Date(league.getSeriesMatchDate().getTime() + 1000 * 60 * 60 * 3 + minutesOffset * 60 * 1000);

                        return Stream.of(new LeagueTime(league, time));
                    })
                    .sorted(Comparator.comparing(LeagueTime::getTime))
                    .collect(Collectors.toList());
        }

        Integer leaguesSize = leagueTimes.size();
        AtomicInteger loadedCountries = new AtomicInteger(0);
        countriesLastLeagueMatchLoader.setCallback(() -> {
            Integer progress = loadedCountries.incrementAndGet();
            if (progress >= leaguesSize) {
                isOver.set(true);
            }
        });

        schedule(leagueTimes, executorService, timer);

        while(true) {
            try {
                if (isOver.get()) {
                    System.exit(0);
                }
                TimeUnit.SECONDS.sleep(10L);
            } catch (InterruptedException e) {
                log.error(e.getMessage(), e);
                telegram.send(e.getMessage());
            }
        }
    }

    private void schedule(List<LeagueTime> leagueTimes, ExecutorService executorService, Timer timer ) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
        leagueTimes.forEach(leagueTime -> {

            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Future<?> submit = executorService.submit(() -> countriesLastLeagueMatchLoader.load(Arrays.asList(leagueTime.league.getLeagueName())));
                    try {
                        submit.get();
                    } catch (Exception e) {
                        LeagueTime newLeagueTime = new LeagueTime(leagueTime.league,
                                new Date(leagueTime.getTime().getTime() + 30 * 60 * 1000));
                        schedule(Arrays.asList(newLeagueTime), executorService, timer);
                    }
                }
            }, leagueTime.getTime());

            log.info("Scheduled loading ({}, {}) with {} active teams to {}", leagueTime.league.getLeagueName(),
                    leagueTime.league.getLeagueId(), leagueTime.league.getActiveTeams(), format.format(leagueTime.getTime()));
        });

        alltidLike.scheduleInfo(leagueTimes);
    }
}
