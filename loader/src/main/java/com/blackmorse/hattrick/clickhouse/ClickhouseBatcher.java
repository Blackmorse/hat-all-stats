package com.blackmorse.hattrick.clickhouse;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;

@Slf4j
@Builder
public class ClickhouseBatcher<T> {
    private final List<T> batch = new ArrayList<>();
    private final int maxBatchSize;
    private final ExecutorService executorService;
    private final String tableName;
    private final NamedParameterJdbcTemplate template;
    private final String baseSql;
    private final Function<T, Map<String, Object>> jdbcParamsCreator;

    public void addToBatch(T t) {
        synchronized (this) {
            batch.add(t);
            if (batch.size() >= maxBatchSize) {
                List<T> tmpList = new ArrayList<>(batch);
                batch.clear();
                log.info("Batch rolled");
                executorService.submit(() -> writeToClickhouse(tmpList));
            }
        }
    }

    private void writeToClickhouse(List<T> batch) {
        try {
            log.info("Writing {} lines to table {}", batch.size(), tableName);

            MapSqlParameterSource[] mapSqlParameterSources = batch.stream().map(jdbcParamsCreator)
                    .map(MapSqlParameterSource::new).toArray(MapSqlParameterSource[]::new);

            template.batchUpdate(baseSql, mapSqlParameterSources);
            log.info("Success");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            batch.forEach(entry -> log.trace(entry.toString()));
        }
    }


    public void flush() {
        synchronized (this) {
            executorService.submit(() -> writeToClickhouse(batch));
        }
    }
}
