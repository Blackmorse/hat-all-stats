package com.blackmorse.hattrick.clickhouse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;

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

//    public ClickhouseWriter<MatchDetails> createMatchDetails() {
//        String sql = "insert into hattrick.match_details (season, league_id, division_level, league_unit_id, league_unit_name, team_id, team_name, time, round, match_id, formation, tactic_type, tactic_skill, rating_midfield, rating_right_def, rating_left_def, rating_mid_def, rating_right_att, rating_mid_att, rating_left_att, rating_indirect_set_pieces_def, rating_indirect_set_pieces_att) values " +
//                "(:season, :league_id, :division_level, :league_unit_id, :league_unit_name, :team_id, :team_name, :time, :round, :match_id, :formation, :tactic_type, :tactic_skill, :rating_midfield, :rating_right_def, :rating_left_def, :rating_mid_def, :rating_right_att, :rating_mid_att, :rating_left_att, :rating_indirect_set_pieces_def, :rating_indirect_set_pieces_att)";
//        return ClickhouseWriter.<MatchDetails>builder()
//                .maxBatchSize(matchBatchSize)
//                .executorService(executorService)
//                .tableName("hattrick.match_details")
//                .template(jdbcTemplate)
//                .baseSql(sql)
//                .jdbcParamsCreator(matchDetailsMapper)
//                .build();
//    }

//    public ClickhouseWriter<PlayerLineUp> createPlayerRatings() {
//        String sql = "insert into hattrick.player_rating (league_id, division_level, league_unit_id, team_id, team_name, time, round, match_id, player_id, role_id, first_name, last_name, rating_stars, start_minute, end_minute, ratingStarsEndOfMatch, behaviour) " +
//                "values (:league_id, :division_level, :league_unit_id, :team_id, :team_name, :time, :round, :match_id, :player_id, :role_id, :first_name, :last_name, :rating_stars, :start_minute, :end_minute, :ratingStarsEndOfMatch, :behaviour)";
//
//        Function<PlayerLineUp, Map<String, Object>> jdbcParamsCreator = playerLineUp -> {
//            Map<String, Object> map = new HashMap<>();
//
//            map.put("league_id", playerLineUp.getLeagueId());
//            map.put("division_level", playerLineUp.getDivisionLevel());
//            map.put("league_unit_id", playerLineUp.getLeagueUnitId());
//            map.put("team_id", playerLineUp.getTeamId());
//            map.put("team_name", playerLineUp.getTeamName());
//            map.put("time", playerLineUp.getDate());
//            map.put("round", playerLineUp.getRound());
//            map.put("match_id", playerLineUp.getMatchId());
//            map.put("player_id", playerLineUp.getPlayerId());
//            map.put("role_id", playerLineUp.getRoleId());
//            map.put("first_name", playerLineUp.getFirstName());
//            map.put("last_name", playerLineUp.getLastName());
//            map.put("rating_stars", (playerLineUp.getRatingStars() == null) ? 0.0f : playerLineUp.getRatingStars());
//            map.put("start_minute", playerLineUp.getStartMinute());
//            map.put("end_minute", playerLineUp.getEndMinute());
//            map.put("ratingStarsEndOfMatch", (playerLineUp.getRatingStarsEndOfMatch() == null) ? 0.0f : playerLineUp.getRatingStarsEndOfMatch());
//            map.put("behaviour", (playerLineUp.getBehaviour() == null) ? -2 : playerLineUp.getBehaviour());
//
//            return map;
//        };
//
//        return ClickhouseWriter.<PlayerLineUp>builder()
//                .maxBatchSize(10000)
//                .executorService(executorService)
//                .tableName("hattick.player_rating")
//                .template(jdbcTemplate)
//                .baseSql(sql)
//                .jdbcParamsCreator(jdbcParamsCreator)
//                .build();
//    }
}
