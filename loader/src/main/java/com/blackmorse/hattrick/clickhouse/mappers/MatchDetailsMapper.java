package com.blackmorse.hattrick.clickhouse.mappers;

import com.blackmorse.hattrick.clickhouse.model.MatchDetails;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class MatchDetailsMapper implements Function<MatchDetails, Map<String, Object>> {
    @Override
    public Map<String, Object> apply(MatchDetails matchDetails) {
        Map<String, Object> map = new HashMap<>();

        map.put("season", matchDetails.getSeason());
        map.put("league_id", matchDetails.getLeagueId());
        map.put("division_level", matchDetails.getDivisionLevel());
        map.put("league_unit_id", matchDetails.getLeagueUnitId());
        map.put("league_unit_name", matchDetails.getLeagueUnitName());
        map.put("team_id", matchDetails.getTeamId());
        map.put("team_name", matchDetails.getTeamName());
        map.put("time", matchDetails.getDate());
        map.put("round", matchDetails.getRound());
        map.put("match_id", matchDetails.getMatchId());
        map.put("formation", matchDetails.getFormation());
        map.put("tactic_type", matchDetails.getTacticType());
        map.put("tactic_skill", matchDetails.getTacticSkill());
        map.put("rating_midfield", matchDetails.getRatingMidfield());
        map.put("rating_right_def", matchDetails.getRatingRightDef());
        map.put("rating_left_def", matchDetails.getRatingLeftDef());
        map.put("rating_mid_def", matchDetails.getRatingMidDef());
        map.put("rating_right_att", matchDetails.getRatingRightAtt());
        map.put("rating_mid_att", matchDetails.getRatingMidAtt());
        map.put("rating_left_att", matchDetails.getRatingLeftAtt());
        map.put("rating_indirect_set_pieces_def", matchDetails.getRatingIndirectSetPiecesDef());
        map.put("rating_indirect_set_pieces_att", matchDetails.getRatingIndirectSetPiecesAtt());

        return map;
    }
}
