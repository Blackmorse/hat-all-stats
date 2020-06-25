package com.blackmorse.hattrick;

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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

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
    private final static class LeagueTime {
        public Integer id;
        public String league;
        public Date time;
    }

    private final Hattrick hattrick;
    private final CountriesLastLeagueMatchLoader countriesLastLeagueMatchLoader;
    private final Telegram telegram;

    @Autowired
    public ScheduledCountryLoader(Hattrick hattrick,
                                  CountriesLastLeagueMatchLoader countriesLastLeagueMatchLoader, Telegram telegram) {
        this.hattrick = hattrick;
        this.countriesLastLeagueMatchLoader = countriesLastLeagueMatchLoader;
        this.telegram = telegram;
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
                        return new LeagueTime(league.getLeagueId(), league.getLeagueName(),
                                new Date(seriesMatchDate.getTime() + 1000 * 60 * 60 * 3 + minutesOffset * 60 * 1000));
                    })
                    .sorted(Comparator.comparing(LeagueTime::getTime))
                    .dropWhile(leagueTime -> !leagueTime.league.equals(country.get()))
                    .collect(Collectors.toList());
        } else {
            leagueTimes = worldDetails.getLeagueList().stream()
                    .map(league -> {
                        Integer minutesOffset = countriesToMinutesOffset.getOrDefault(league.getLeagueId(), 0);

                        Date time = new Date(league.getSeriesMatchDate().getTime() + 1000 * 60 * 60 * 3 + minutesOffset * 60 * 1000);

                        return new LeagueTime(league.getLeagueId(), league.getLeagueName(), time);
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

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
        leagueTimes.forEach(leagueTime -> {

            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    executorService.submit(() -> countriesLastLeagueMatchLoader.load(Arrays.asList(leagueTime.league)));
                }
            }, leagueTime.getTime());

            log.info("Scheduled loading ({}, {}) to {}", leagueTime.league, leagueTime.id, format.format(leagueTime.getTime()));
        });

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
}
