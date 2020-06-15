package com.blackmorse.hattrick.clickhouse;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Forms and executes SQL with team ranking by different parameters
 */
@Component
@Slf4j
public class TeamRankJoiner {
    @AllArgsConstructor
    public static class SqlRequestParam {
        final String field;
        final String fieldAlias;
        final String request;
    }
    
    private static final String base_fields = " season, league_id, round, division_level, league_unit_id, league_unit_name, team_id, team_name, match_id, ";
    private static final String match_details_request = "SELECT " +
            "team_id, " +
            "team_name, " +
            "    {field_alias}, " +
            "    rowNumberInAllBlocks() AS {field_alias}_position" +
            " FROM " +
            "(" +
            "    SELECT " +
            "team_id, " +
            "team_name, " +
            "        {field} AS {field_alias}" +
            "    FROM {database}.match_details" +
            "    WHERE {where}" +
            "    ORDER BY {field_alias} DESC, team_id ASC" +
            ") " +
            "ORDER BY team_id ASC";


    private static final String player_stats_request = "SELECT " +
            "    team_id, " +
            "    team_name, " +
            "    {field_alias}, " +
            "    rowNumberInAllBlocks() AS {field_alias}_position "  +
            "FROM " +
            "(" +
            "    SELECT " +
            "        team_id, " +
            "        team_name, " +
            "        {field} AS {field_alias}" +
            "    FROM {database}.player_stats" +
            "    WHERE {where}" +
            "    GROUP BY " +
            "        team_id, " +
            "        team_name" +
            "    ORDER BY {field_alias} DESC, team_id ASC" +
            ")" +
            "ORDER BY " +
            "    team_id ASC ";

    private final String database;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public TeamRankJoiner(@Value("${clickhouse.databaseName}") String database,
                          JdbcTemplate jdbcTemplate) {
        this.database = database;
        this.jdbcTemplate = jdbcTemplate;
    }

    public static void main(String[] args) {
        System.out.println(new TeamRankJoiner("hattrick", null).createSql(75, 136, 1, null));
    }

    public void join(Integer season, Integer leagueId, Integer round, Integer divisionLevel) {
        String sql = createSql(season, leagueId, round, divisionLevel);

        log.info("Calculating team ranking for league {}, divisionLevel {}", leagueId, divisionLevel);
        jdbcTemplate.execute(sql);
    }

    private String createSql(Integer season, Integer leagueId, Integer round, Integer divisionLevel) {
        String whereStatement = "(season = " + season +
                ") AND (league_id = " + leagueId + ") " +
                " AND (round = " + round + ") " +
                (divisionLevel == null ? "" : "AND (division_level = " + divisionLevel + ") ");

        String base_request = "SELECT " +
                base_fields +
                "    {field_alias}, " +
                "    rowNumberInAllBlocks() AS {field_alias}_position " +
                "FROM " +
                "(" +
                "    SELECT " +
                base_fields +
                "        {field} AS {field_alias}" +
                "    FROM {database}.match_details" +
                "    WHERE {where}" +
                "    ORDER BY {field_alias} DESC, team_id ASC" +
                ")" +
                "ORDER BY team_id ASC";

        String request = base_request
                .replace("{field}", "rating_midfield * 3 + rating_right_def + rating_left_def + rating_mid_def + rating_right_att + rating_mid_att + rating_left_att")
                .replace("{field_alias}", "hatstats")
                .replace("{where}", whereStatement)
                .replace("{database}", database);

        List<SqlRequestParam> requestsParams = new ArrayList<>();
        requestsParams.add(new SqlRequestParam("rating_right_att + rating_mid_att + rating_left_att", "attack", match_details_request));
        requestsParams.add(new SqlRequestParam("rating_midfield", "midfield", match_details_request));
        requestsParams.add(new SqlRequestParam("rating_right_def + rating_left_def + rating_mid_def", "defense", match_details_request));

        requestsParams.add(new SqlRequestParam("sum(tsi)", "tsi", player_stats_request));
        requestsParams.add(new SqlRequestParam("sum(salary)", "salary", player_stats_request));
        requestsParams.add(new SqlRequestParam("sum(rating)", "rating", player_stats_request));
        requestsParams.add(new SqlRequestParam("sum(rating_end_of_match)", "rating_end_of_match", player_stats_request));
        requestsParams.add(new SqlRequestParam("avg((age * 112) + days)", "age", player_stats_request));
        requestsParams.add(new SqlRequestParam("sumIf(injury_level, (played_minutes > 0) AND (injury_level > 0))", "injury", player_stats_request));
        requestsParams.add(new SqlRequestParam("countIf(injury_level, (played_minutes > 0) AND (injury_level > 0))", "injury_count", player_stats_request));

        String newFields = "hatstats, hatstats_position";

        String oldTablePrefix = "";
        String oldFieldAlias = "hatstats";

        for (SqlRequestParam sqlRequestParam : requestsParams) {
            newFields += ", " + sqlRequestParam.fieldAlias + ", " + sqlRequestParam.fieldAlias + "_position";

            request = "SELECT " + base_fields  + newFields + " FROM (" +
                    request + ") as " + oldTablePrefix + "_" + oldFieldAlias + "_table " + "LEFT JOIN ("
                    + sqlRequestParam.request.replace("{field}", sqlRequestParam.field)
                                            .replace("{field_alias}", sqlRequestParam.fieldAlias)
                                            .replace("{where}", whereStatement)
                                            .replace("{database}", database)
                    + ") as " + sqlRequestParam.fieldAlias + "_table "
                    + " ON " + oldTablePrefix + "_" + oldFieldAlias + "_table.team_id = " + sqlRequestParam.fieldAlias + "_table.team_id";

            oldTablePrefix = oldTablePrefix + "_" + oldFieldAlias;
            oldFieldAlias = sqlRequestParam.fieldAlias;
        }

        request = "SELECT " + (divisionLevel == null ? "'league_id'" : "'division_level'") + ", "  + base_fields + newFields + " FROM ("
                + request + ")";

        return  "INSERT INTO " + database + ".team_rankings " + request;
    }
}
