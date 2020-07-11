package com.blackmorse.hattrick.clickhouse.mappers;

import com.blackmorse.hattrick.promotions.model.PromoteTeam;
import com.blackmorse.hattrick.promotions.model.Promotion;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class PromotionsMapper extends AbstractJdbcMapper<Promotion> {
    public PromotionsMapper(String databaseName) {
        super(databaseName + ".promotions");
    }

    @Override
    protected Map<String, Function<Promotion, Object>> initFieldsMap() {
        Map<String, Function<Promotion, Object>> map = new HashMap<>();

        map.put("season", Promotion::getSeason);
        map.put("league_id", Promotion::getLeagueId);
        map.put("up_division_level", Promotion::getUpDivisionLevel);
        map.put("promotion_type", promotion -> promotion.getPromoteType().getStringValue());

        map.put("going_down_teams.team_id", fieldFromPromoteTeamFunction(Promotion::getDownTeams, promoteTeam -> promoteTeam.getTeam().getId()));
        map.put("going_down_teams.team_name", fieldFromPromoteTeamFunction(Promotion::getDownTeams, promoteTeam -> promoteTeam.getTeam().getName()));
        map.put("going_down_teams.division_level", fieldFromPromoteTeamFunction(Promotion::getDownTeams, promoteTeam -> promoteTeam.getTeam().getLeagueUnit().getLevel()));
        map.put("going_down_teams.league_unit_id", fieldFromPromoteTeamFunction(Promotion::getDownTeams, promoteTeam -> promoteTeam.getTeam().getLeagueUnit().getId()));
        map.put("going_down_teams.league_unit_name", fieldFromPromoteTeamFunction(Promotion::getDownTeams, promoteTeam -> promoteTeam.getTeam().getLeagueUnit().getName()));
        map.put("going_down_teams.position", fieldFromPromoteTeamFunction(Promotion::getDownTeams, PromoteTeam::getPosition));
        map.put("going_down_teams.points", fieldFromPromoteTeamFunction(Promotion::getDownTeams, PromoteTeam::getPoints));
        map.put("going_down_teams.diff", fieldFromPromoteTeamFunction(Promotion::getDownTeams, PromoteTeam::getDiff));
        map.put("going_down_teams.scored", fieldFromPromoteTeamFunction(Promotion::getDownTeams, PromoteTeam::getScored));

        map.put("going_up_teams.team_id", fieldFromPromoteTeamFunction(Promotion::getUpTeams, promoteTeam -> promoteTeam.getTeam().getId()));
        map.put("going_up_teams.team_name", fieldFromPromoteTeamFunction(Promotion::getUpTeams, promoteTeam -> promoteTeam.getTeam().getName()));
        map.put("going_up_teams.division_level", fieldFromPromoteTeamFunction(Promotion::getUpTeams, promoteTeam -> promoteTeam.getTeam().getLeagueUnit().getLevel()));
        map.put("going_up_teams.league_unit_id", fieldFromPromoteTeamFunction(Promotion::getUpTeams, promoteTeam -> promoteTeam.getTeam().getLeagueUnit().getId()));
        map.put("going_up_teams.league_unit_name", fieldFromPromoteTeamFunction(Promotion::getUpTeams, promoteTeam -> promoteTeam.getTeam().getLeagueUnit().getName()));
        map.put("going_up_teams.position", fieldFromPromoteTeamFunction(Promotion::getUpTeams, PromoteTeam::getPosition));
        map.put("going_up_teams.points", fieldFromPromoteTeamFunction(Promotion::getUpTeams, PromoteTeam::getPoints));
        map.put("going_up_teams.diff", fieldFromPromoteTeamFunction(Promotion::getUpTeams, PromoteTeam::getDiff));
        map.put("going_up_teams.scored", fieldFromPromoteTeamFunction(Promotion::getUpTeams, PromoteTeam::getScored));

        return map;
    }

    private Function<Promotion, Object> fieldFromPromoteTeamFunction(Function<Promotion, List<PromoteTeam>> listFunction,
                                                                     Function<PromoteTeam, Object> fieldFunction) {
        return promotion ->
                listFunction.apply(promotion).stream()
                    .map(fieldFunction::apply)
                    .toArray();
    }
}
