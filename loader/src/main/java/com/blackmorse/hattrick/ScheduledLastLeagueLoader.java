package com.blackmorse.hattrick;

import com.blackmorse.hattrick.api.Hattrick;
import com.blackmorse.hattrick.api.worlddetails.model.League;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ScheduledLastLeagueLoader {
    private final Hattrick hattrick;
    private final CountriesLastLeagueMatchLoader countriesLastLeagueMatchLoader;
    private BlockingQueue<DateWithLeagues> queue;

    @Autowired
    public ScheduledLastLeagueLoader(Hattrick hattrick,
                                     CountriesLastLeagueMatchLoader countriesLastLeagueMatchLoader) {
        this.hattrick = hattrick;
        this.countriesLastLeagueMatchLoader = countriesLastLeagueMatchLoader;
    }

    public void load() {
        Timer timer = new Timer();


        Map<Date, List<League>> collect = hattrick.getWorldDetails().getLeagueList().stream().collect(Collectors.groupingBy(League::getSeriesMatchDate));

        queue = new ArrayBlockingQueue<>(collect.size());

        collect
                .entrySet().stream().sorted(Comparator.comparing(Map.Entry::getKey))
                .map(entry -> new DateWithLeagues(entry.getKey(),
                        entry.getValue().stream().map(League::getLeagueName).collect(Collectors.toList())))
                .forEach(dateWithLeagues -> {
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            queue.add(dateWithLeagues);
                        }
                    }, new Date(dateWithLeagues.getDate().getTime() + 1000 * 60 * 120));
                });

        while(true) {
            try {
                DateWithLeagues dateWithLeagues = queue.take();
               countriesLastLeagueMatchLoader.load(dateWithLeagues.getCountries());
            } catch (InterruptedException e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    @Data
    public static class DateWithLeagues {
        private final Date date;
        private final List<String> countries;
    }
}
