package com.blackmorse.hattrick.clickhouse.mappers;

import com.blackmorse.hattrick.clickhouse.model.PlayerInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class PlayerInfoJdbcMapper extends AbstractJdbcMapper<PlayerInfo> {
    public PlayerInfoJdbcMapper(String databaseName) {
        super(databaseName + ".player_info");
    }

    @Override
    protected Map<String, Function<PlayerInfo, Object>> initFieldsMap() {
        Map<String, Function<PlayerInfo, Object>> map = new HashMap<>();

        map.put("season", PlayerInfo::getSeason);
        map.put("league_id", PlayerInfo::getLeagueId);
        map.put("division_level", PlayerInfo::getDivisionLevel);
        map.put("league_unit_id", PlayerInfo::getLeagueUnitId);
        map.put("league_unit_name", PlayerInfo::getLeagueUnitName);
        map.put("team_id", PlayerInfo::getTeamId);
        map.put("team_name", PlayerInfo::getTeamName);
        map.put("time", PlayerInfo::getDate);
        map.put("round", PlayerInfo::getRound);
        map.put("match_id", PlayerInfo::getMatchId);

        map.put("player_id", PlayerInfo::getPlayerId);
        map.put("first_name", PlayerInfo::getFirstName);
        map.put("last_name", PlayerInfo::getLastName);
        map.put("age", PlayerInfo::getAge);
        map.put("days", PlayerInfo::getDays);
        map.put("role_id", PlayerInfo::getRoleId);
        map.put("played_minutes", PlayerInfo::getPlayedMinutes);
        map.put("rating", PlayerInfo::getRating);
        map.put("rating_end_of_match", PlayerInfo::getRatingEndOfMatch);

        map.put("injury_level", PlayerInfo::getInjuryLevel);
        map.put("tsi", PlayerInfo::getTSI);
        map.put("salary", PlayerInfo::getSalary);

        return map;
    }
}
