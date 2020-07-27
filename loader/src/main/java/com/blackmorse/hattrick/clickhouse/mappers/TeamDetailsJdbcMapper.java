package com.blackmorse.hattrick.clickhouse.mappers;

import com.blackmorse.hattrick.clickhouse.model.TeamDetails;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class TeamDetailsJdbcMapper extends AbstractJdbcMapper<TeamDetails> {
    public TeamDetailsJdbcMapper(String databaseName) {
        super(databaseName + ".team_details");
    }

    @Override
    protected Map<String, Function<TeamDetails, Object>> initFieldsMap() {
        Map<String, Function<TeamDetails, Object>> map = new HashMap<>();

        map.put("season", TeamDetails::getSeason);
        map.put("league_id", TeamDetails::getLeagueId);
        map.put("division_level", TeamDetails::getDivisionLevel);
        map.put("league_unit_id", TeamDetails::getLeagueUnitId);
        map.put("league_unit_name", TeamDetails::getLeagueUnitName);
        map.put("team_id", TeamDetails::getTeamId);
        map.put("team_name", TeamDetails::getTeamName);
        map.put("round", TeamDetails::getRound);

        map.put("power_rating", TeamDetails::getPowerRating);
        map.put("home_flags", TeamDetails::getHomeFlags);
        map.put("away_flags", TeamDetails::getAwayFlags);
        map.put("fanclub_size", TeamDetails::getFanclubSize);
        map.put("trophies_number", TeamDetails::getTrophiesNumber);
        map.put("number_of_victories", TeamDetails::getNumberOfVictories);
        map.put("number_of_undefeated", TeamDetails::getNumberOfUndefeated);
        return map;
    }
}
