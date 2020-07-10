package com.blackmorse.hattrick;

import com.blackmorse.hattrick.clickhouse.ClickhouseWriter;
import com.blackmorse.hattrick.clickhouse.mappers.MatchDetailsJdbcMapper;
import com.blackmorse.hattrick.clickhouse.mappers.PlayerEventsJdbcMapper;
import com.blackmorse.hattrick.clickhouse.mappers.PlayerInfoJdbcMapper;
import com.blackmorse.hattrick.clickhouse.mappers.PromotionsMapper;
import com.blackmorse.hattrick.clickhouse.model.MatchDetails;
import com.blackmorse.hattrick.clickhouse.model.PlayerEvents;
import com.blackmorse.hattrick.clickhouse.model.PlayerInfo;
import com.blackmorse.hattrick.promotions.model.Promotion;
import com.blackmorse.hattrick.telegram.Telegram;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@EnableScheduling
@Configuration
public class Config {
    @Value("${clickhouse.databaseName}")
    private String databaseName;

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

    @Bean("playerEventsWriter")
    public ClickhouseWriter<PlayerEvents> playerEventsClickhouseWriter(NamedParameterJdbcTemplate template,
                                                                       Telegram telegram) {
        return new ClickhouseWriter<>(template, new PlayerEventsJdbcMapper(databaseName), telegram);
    }

    @Bean("matchDetailsWriter")
    public ClickhouseWriter<MatchDetails> matchDetailsClickhouseWriter(NamedParameterJdbcTemplate template,
                                                                       Telegram telegram) {
        return new ClickhouseWriter<>(template, new MatchDetailsJdbcMapper(databaseName), telegram);
    }

    @Bean("playerInfoWriter")
    public ClickhouseWriter<PlayerInfo> playerInfoClickhouseWriter(NamedParameterJdbcTemplate template,
                                                                   Telegram telegram) {
        return new ClickhouseWriter<>(template, new PlayerInfoJdbcMapper(databaseName), telegram);
    }

    @Bean("promotionsWriter")
    public ClickhouseWriter<Promotion> promotionClickhouseWriter(NamedParameterJdbcTemplate template,
                                                                 Telegram telegram) {
        return new ClickhouseWriter<>(template, new PromotionsMapper(databaseName), telegram);
    }
}
