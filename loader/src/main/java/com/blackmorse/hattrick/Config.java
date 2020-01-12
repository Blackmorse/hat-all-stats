package com.blackmorse.hattrick;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@EnableScheduling
@Configuration
public class Config {
    @Bean("apiExecutor")
    public ExecutorService apiExecutorService(@Value("${api.threads}") int apiThreads) {
        return Executors.newFixedThreadPool(apiThreads);
    }

    @Bean("clickhouseExecutor")
    public ExecutorService clickhouseExecutorService() {
        return Executors.newSingleThreadExecutor();
    }

    @Bean
    public HattrickApi hattrickApi(@Value("${api.customerKey}") String customerKey,
                                   @Value("${api.customerSecret}") String customerSecret,
                                   @Value("${api.accessToken}") String accessToken,
                                   @Value("${api.accessTokenSecret}") String accessTokenSecret) {
        return new HattrickApi(customerKey, customerSecret, accessToken, accessTokenSecret);
    }
}
