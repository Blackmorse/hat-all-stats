package com.blackmorse.hattrick.clickhouse.mappers;

import com.blackmorse.hattrick.clickhouse.model.PlayerEvents;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class PlayerEventsJdbcMapper extends AbstractJdbcMapper<PlayerEvents> {
    public PlayerEventsJdbcMapper() {
        super("hattrick.player_events");
    }

    @Override
    protected Map<String, Function<PlayerEvents, Object>> initFieldsMap() {
        Map<String, Function<PlayerEvents, Object>> map = new HashMap<>();

        map.put("season", PlayerEvents::getSeason);
        map.put("round", PlayerEvents::getRound);
        map.put("player_id", PlayerEvents::getPlayerId);
        map.put("yellow_cards", PlayerEvents::getYellowCards);
        map.put("red_cards", PlayerEvents::getRedCards);
        map.put("goals", PlayerEvents::getGoals);
        map.put("injury", PlayerEvents::getInjury);

        return map;
    }
}
