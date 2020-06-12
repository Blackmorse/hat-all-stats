package com.blackmorse.hattrick.clickhouse;

import com.blackmorse.hattrick.clickhouse.mappers.AbstractJdbcMapper;
import com.blackmorse.hattrick.telegram.Telegram;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.List;

@Slf4j
public class ClickhouseWriter<T> {
    private final NamedParameterJdbcTemplate template;
    private final AbstractJdbcMapper<T> jdbcMapper;
    private final Telegram telegram;

    @Autowired
    public ClickhouseWriter(NamedParameterJdbcTemplate template,
                            AbstractJdbcMapper<T> jdbcMapper,
                            Telegram telegram) {
        this.template = template;
        this.jdbcMapper = jdbcMapper;
        this.telegram = telegram;
    }

    public void writeToClickhouse(List<T> batch) {
        try {
            log.debug("Writing {} lines with sql {}", batch.size(), jdbcMapper.getSql());

            MapSqlParameterSource[] mapSqlParameterSources = batch.stream().map(jdbcMapper.getTMapFunction())
                    .map(MapSqlParameterSource::new).toArray(MapSqlParameterSource[]::new);

            template.batchUpdate(jdbcMapper.getSql(), mapSqlParameterSources);
            log.info("{} rows successfully written", batch.size());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            telegram.send(e.getMessage());
            batch.forEach(entry -> log.trace(entry.toString()));
        }
    }
}
