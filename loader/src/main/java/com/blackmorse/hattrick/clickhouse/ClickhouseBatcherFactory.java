package com.blackmorse.hattrick.clickhouse;

import com.blackmorse.hattrick.clickhouse.model.MatchDetails;
import com.blackmorse.hattrick.clickhouse.model.PlayerRating;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

@Component
public class ClickhouseBatcherFactory {
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final ExecutorService executorService;
    private final Integer matchBatchSize;

    @Autowired
    public ClickhouseBatcherFactory(NamedParameterJdbcTemplate jdbcTemplate,
                                    @Qualifier("clickhouseExecutor") ExecutorService executorService,
                                    @Value("${clickhouse.batchSize.match}") Integer matchBatchSize) {
        this.jdbcTemplate = jdbcTemplate;
        this.executorService = executorService;
        this.matchBatchSize = matchBatchSize;
    }

    public  ClickhouseBatcher<MatchDetails> createMatchDetails() {
        String sql = "insert into hattrick.match_details (league_id, division_level, league_unit_id, team_id, team_name, time, round, match_id, formation, tactic_type, tactic_skill, rating_midfield, rating_right_def, rating_left_def, rating_mid_def, rating_right_att, rating_mid_att, rating_left_att, rating_indirect_set_pieces_def, rating_indirect_set_pieces_att) values " +
                "(:league_id, :division_level, :league_unit_id, :team_id, :team_name, :time, :round, :match_id, :formation, :tactic_type, :tactic_skill, :rating_midfield, :rating_right_def, :rating_left_def, :rating_mid_def, :rating_right_att, :rating_mid_att, :rating_left_att, :rating_indirect_set_pieces_def, :rating_indirect_set_pieces_att)";


        Function<MatchDetails, Map<String, Object>> jdbcParamsCreator = matchDetails -> {
            Map<String, Object> map = new HashMap<>();

            map.put("league_id", matchDetails.getLeagueId());
            map.put("division_level", matchDetails.getDivisionLevel());
            map.put("league_unit_id", matchDetails.getLeagueUnitId());
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
        };

        return ClickhouseBatcher.<MatchDetails>builder()
                .maxBatchSize(matchBatchSize)
                .executorService(executorService)
                .tableName("hattrick.match_details")
                .template(jdbcTemplate)
                .baseSql(sql)
                .jdbcParamsCreator(jdbcParamsCreator)
                .build();
    }

    public ClickhouseBatcher<PlayerRating> createPlayerRatings() {
        String sql = "insert into hattrick.player_rating (league_id, division_level, league_unit_id, team_id, team_name, time, round, match_id, player_id, role_id, first_name, last_name, rating_stars, ratingStarsEndOfMatch, behaviour) " +
                "values (:league_id, :division_level, :league_unit_id, :team_id, :team_name, :time, :round, :match_id, :player_id, :role_id, :first_name, :last_name, :rating_stars, :ratingStarsEndOfMatch, :behaviour)";

        Function<PlayerRating, Map<String, Object>> jdbcParamsCreator = playerRating -> {
            Map<String, Object> map = new HashMap<>();

            map.put("league_id", playerRating.getLeagueId());
            map.put("division_level", playerRating.getDivisionLevel());
            map.put("league_unit_id", playerRating.getLeagueUnitId());
            map.put("team_id", playerRating.getTeamId());
            map.put("team_name", playerRating.getTeamName());
            map.put("time", playerRating.getDate());
            map.put("round", playerRating.getRound());
            map.put("match_id", playerRating.getMatchId());
            map.put("player_id", playerRating.getPlayerId());
            map.put("role_id", playerRating.getRoleId());
            map.put("first_name", playerRating.getFirstName());
            map.put("last_name", playerRating.getLastName());
            map.put("rating_stars", (playerRating.getRatingStars() == null) ? 0.0f : playerRating.getRatingStars());
            map.put("ratingStarsEndOfMatch", (playerRating.getRatingStarsEndOfMatch() == null) ? 0.0f : playerRating.getRatingStarsEndOfMatch());
            map.put("behaviour", (playerRating.getBehaviour() == null) ? -2 : playerRating.getBehaviour());

            return map;
        };

        return ClickhouseBatcher.<PlayerRating>builder()
                .maxBatchSize(10000)
                .executorService(executorService)
                .tableName("hattick.player_rating")
                .template(jdbcTemplate)
                .baseSql(sql)
                .jdbcParamsCreator(jdbcParamsCreator)
                .build();
    }
}
