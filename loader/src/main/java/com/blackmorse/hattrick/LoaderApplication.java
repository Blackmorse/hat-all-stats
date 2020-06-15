package com.blackmorse.hattrick;

import com.blackmorse.hattrick.api.Hattrick;
import com.blackmorse.hattrick.clickhouse.TeamRankJoiner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.retry.annotation.EnableRetry;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@SpringBootApplication
@EnableRetry
@Slf4j
public class LoaderApplication {
    public static void main(String[] args) {
        SpringApplication.run(LoaderApplication.class, args);
    }

    @Bean
    public CommandLineRunner runner(ApplicationContext ctx) {
        return args -> {
            if (args.length == 0) {
                log.info("Possible commands: schedule, load <list_of_countries>");
            } else if (args[0].equals("schedule")) {
                log.info("Command of scheduling next round...");
                ctx.getBean(ScheduledCountryLoader.class).loadFrom(Optional.empty());
            } else if (args[0].equals("scheduleFrom")) {
                log.info("Command of scheduling from {}", args[1]);
                ctx.getBean(ScheduledCountryLoader.class).loadFrom(Optional.of(args[1]));
            } else if (args[0].equals("load")) {
                String countries = args[1];
                List<String> countriesList = Arrays.stream(countries.split(",")).map(String::trim).collect(Collectors.toList());
                log.info("Command to load countries: {}", countriesList);
                ctx.getBean(CountriesLastLeagueMatchLoader.class).load(countriesList);
            } else if (args[0].equals("rankJoin")) {
                TeamRankJoiner rankJoiner = ctx.getBean(TeamRankJoiner.class);
                AtomicInteger counter = new AtomicInteger();
                ctx.getBean(Hattrick.class).getWorldDetails().getLeagueList().forEach(league -> {
                    counter.incrementAndGet();
                    for (int round = 1; round <= 2; round ++) {
                        log.info("{}. Joining all leagues for ({}, {}), round {}", counter.get(), league.getLeagueName(), league.getLeagueId(), round);
                        rankJoiner.join(75, league.getLeagueId(), round, null);
                        for (int divisionLevel = 1; divisionLevel <= league.getNumberOfLevels(); divisionLevel ++) {
                            log.info("{}. Joining {} division for ({}, {}), round {}", counter.get(), divisionLevel, league.getLeagueName(), league.getLeagueId(), round);
                            rankJoiner.join(75, league.getLeagueId(), round, divisionLevel);
                        }
                    }
                });
            }
        };
    }
}
