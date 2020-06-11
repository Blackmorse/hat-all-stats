package com.blackmorse.hattrick;

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
                log.info("Posiible commands: schedule, load <list_of_contries>");
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
            }
        };
    }
}
