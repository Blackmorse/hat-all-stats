package com.blackmorse.hattrick.clickhouse.mappers;

import com.blackmorse.hattrick.clickhouse.model.MatchDetails;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class MatchDetailsJdbcMapper extends AbstractJdbcMapper<MatchDetails> {
    public MatchDetailsJdbcMapper(String databaseName) {
        super(databaseName + ".match_details");
    }

    @Override
    protected Map<String, Function<MatchDetails, Object>> initFieldsMap() {
        Map<String, Function<MatchDetails, Object>> map = new HashMap<>();

        map.put("season", MatchDetails::getSeason);
        map.put("league_id", MatchDetails::getLeagueId);
        map.put("division_level", MatchDetails::getDivisionLevel);
        map.put("league_unit_id", MatchDetails::getLeagueUnitId);
        map.put("league_unit_name", MatchDetails::getLeagueUnitName);
        map.put("team_id", MatchDetails::getTeamId);
        map.put("team_name", MatchDetails::getTeamName);
        map.put("time", MatchDetails::getDate);
        map.put("round", MatchDetails::getRound);
        map.put("match_id", MatchDetails::getMatchId);

        map.put("is_home_match", matchDetails -> matchDetails.getIsHomeMatch().getStringValue());
        map.put("goals", MatchDetails::getGoals);
        map.put("enemy_goals", MatchDetails::getEnemyGoals);

        map.put("sold_total", MatchDetails::getSoldTotal);

        map.put("formation", MatchDetails::getFormation);
        map.put("tactic_type", MatchDetails::getTacticType);
        map.put("tactic_skill", MatchDetails::getTacticSkill);
        map.put("rating_midfield", MatchDetails::getRatingMidfield);
        map.put("rating_right_def", MatchDetails::getRatingRightDef);
        map.put("rating_left_def", MatchDetails::getRatingLeftDef);
        map.put("rating_mid_def", MatchDetails::getRatingMidDef);
        map.put("rating_right_att", MatchDetails::getRatingRightAtt);
        map.put("rating_mid_att", MatchDetails::getRatingMidAtt);
        map.put("rating_left_att", MatchDetails::getRatingLeftAtt);
        map.put("rating_indirect_set_pieces_def", MatchDetails::getRatingIndirectSetPiecesDef);
        map.put("rating_indirect_set_pieces_att", MatchDetails::getRatingIndirectSetPiecesAtt);

        map.put("opposite_team_id", MatchDetails::getOppositeTeamId);
        map.put("opposite_team_name", MatchDetails::getOppositeTeamName);
        map.put("opposite_formation", MatchDetails::getOppositeFormation);
        map.put("opposite_tactic_type", MatchDetails::getOppositeTacticType);
        map.put("opposite_tactic_skill", MatchDetails::getOppositeTacticSkill);
        map.put("opposite_rating_midfield", MatchDetails::getOppositeRatingMidfield);
        map.put("opposite_rating_right_def", MatchDetails::getOppositeRatingRightDef);
        map.put("opposite_rating_left_def", MatchDetails::getOppositeRatingLeftDef);
        map.put("opposite_rating_mid_def", MatchDetails::getOppositeRatingMidDef);
        map.put("opposite_rating_right_att", MatchDetails::getOppositeRatingRightAtt);
        map.put("opposite_rating_mid_att", MatchDetails::getOppositeRatingMidAtt);
        map.put("opposite_rating_left_att", MatchDetails::getOppositeRatingLeftAtt);
        map.put("opposite_rating_indirect_set_pieces_def", MatchDetails::getOppositeRatingIndirectSetPiecesDef);
        map.put("opposite_rating_indirect_set_pieces_att", MatchDetails::getOppositeRatingIndirectSetPiecesAtt);

        return map;
    }
}
