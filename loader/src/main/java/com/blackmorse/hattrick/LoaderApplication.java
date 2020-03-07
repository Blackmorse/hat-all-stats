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
//            ctx.getBean(CountriesLastLeagueMatchLoader.class).load(Arrays.asList( "Эстония", "Чехия", "Латвия", "Черногория", "Италия"
//            ));

            ctx.getBean(CountriesLastLeagueMatchLoader.class).load(Arrays.asList("Россия"));
//
//            ctx.getBean(ScheduledLastLeagueLoader.class).load();{"data":{"playerId":403314670,"positionId":113},"channel":"position-player","offset":{"x":27,"y":45}}
        };
    }
}
