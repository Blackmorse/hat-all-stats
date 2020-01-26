package com.blackmorse.hattrick;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.retry.annotation.EnableRetry;

import java.util.Arrays;

@SpringBootApplication
@EnableRetry
public class LoaderApplication {
    public static void main(String[] args) {
        SpringApplication.run(LoaderApplication.class, args);
    }

    @Bean
    public CommandLineRunner runner(ApplicationContext ctx) {
        return args -> {
//            ctx.getBean(HistoryLoader.class).load(Arrays.asList("Россия"));
            ctx.getBean(LastLeagueMatchLoader.class).load(Arrays.asList("Россия"));
//            ctx.getBean(LastLeagueMatchLoader.class).load(Arrays.asList("Hattrick International"));
//            ctx.getBean(SeasonHistoryLoader.class).load(Arrays.asList("Россия"), 71);
        };
    }
}
