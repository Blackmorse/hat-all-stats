package com.blackmorse.hattrick;

import com.blackmorse.hattrick.api.Hattrick;
import com.blackmorse.hattrick.api.worlddetails.model.League;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
        Timer timer = new Timer();
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        hattrick.getWorldDetails().getLeagueList().stream().sorted(Comparator.comparing(League::getSeriesMatchDate))
                .forEach(league -> {
                   Integer minutesOffset = countriesToMinutesOffset.getOrDefault(league.getLeagueId(), 0);
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            executorService.submit(() -> countriesLastLeagueMatchLoader.load(Arrays.asList(league.getLeagueName())));
                        }
                    }, new Date(league.getSeriesMatchDate().getTime() + 1000 * 60 * 60 * 3 + minutesOffset * 60 * 1000));
                });
    }


    @Data
    public static class DateWithLeagues {
        private final Date date;
        private final List<League> countries;
    }
}
