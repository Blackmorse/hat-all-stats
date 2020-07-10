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
            } else if (args[0].equals("teamRank")) {
                Integer season = Integer.valueOf(args[1]);
                Integer leagueId = Integer.valueOf(args[2]);
                Integer round = Integer.valueOf(args[3]);

                Integer numberOfLevels = ctx.getBean(Hattrick.class).getWorldDetails().getLeagueList()
                        .stream()
                        .filter(league -> league.getLeagueId().equals(leagueId))
                        .findFirst()
                        .get()
                        .getNumberOfLevels();

                ctx.getBean(TeamRankJoiner.class).join(season, leagueId, round, null);
                for (int i = 1; i <= numberOfLevels; i++) {
                    ctx.getBean(TeamRankJoiner.class).join(season, leagueId, round, i);
                }
            } else if (args[0].equals("promotions")) {
                String country = args[1];

                ctx.getBean(PromotionsLoader.class).load(country);
            }
        };
    }
}
