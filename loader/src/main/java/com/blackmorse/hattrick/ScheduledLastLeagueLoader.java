package com.blackmorse.hattrick;

import com.blackmorse.hattrick.api.Hattrick;
import com.blackmorse.hattrick.api.worlddetails.model.League;
import com.blackmorse.hattrick.api.worlddetails.model.WorldDetails;
import lombok.Data;
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
public class ScheduledLastLeagueLoader {
    private final Hattrick hattrick;
    private final CountriesLastLeagueMatchLoader countriesLastLeagueMatchLoader;

    private static final Map<Integer, Integer> countriesToMinutesOffset = new HashMap<>();

    static {
        countriesToMinutesOffset.put(24, 90); //Poland
        countriesToMinutesOffset.put(4, 30); //Italy
        countriesToMinutesOffset.put(36, 60); //Spain
        countriesToMinutesOffset.put(46,135); //Switzerland
        countriesToMinutesOffset.put(3, 30); //Germany
    }

    @Autowired
    public ScheduledLastLeagueLoader(Hattrick hattrick,
                                     CountriesLastLeagueMatchLoader countriesLastLeagueMatchLoader) {
        this.hattrick = hattrick;
        this.countriesLastLeagueMatchLoader = countriesLastLeagueMatchLoader;
    }

    public void load() {
        AtomicBoolean isOver = new AtomicBoolean(false);

        Timer timer = new Timer();
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        WorldDetails worldDetails = hattrick.getWorldDetails();

        Integer leaguesSize = worldDetails.getLeagueList().size();
        AtomicInteger loadedCountries = new AtomicInteger(0);
        countriesLastLeagueMatchLoader.setCallback(() -> {
            Integer progress = loadedCountries.incrementAndGet();
            if (progress >= leaguesSize) {
                isOver.set(true);
            }
        });
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
        worldDetails.getLeagueList().stream().sorted(Comparator.comparing(League::getSeriesMatchDate))
                .forEach(league -> {
                   Integer minutesOffset = countriesToMinutesOffset.getOrDefault(league.getLeagueId(), 0);

                   Date time = new Date(league.getSeriesMatchDate().getTime() + 1000 * 60 * 60 * 3 + minutesOffset * 60 * 1000);
                   timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            executorService.submit(() -> countriesLastLeagueMatchLoader.load(Arrays.asList(league.getLeagueName())));
                        }
                    }, time);

                    log.info("Scheduled loading ({}, {}) to {}", league.getLeagueName(), league.getLeagueId(), format.format(time));
                });

        while(true) {
            try {
                if (isOver.get()) {
                    System.exit(0);
                }
                TimeUnit.SECONDS.sleep(10L);
            } catch (InterruptedException e) {
                log.error(e.getMessage(), e);
            }
        }
    }


    @Data
    public static class DateWithLeagues {
        private final Date date;
        private final List<League> countries;
    }
}
