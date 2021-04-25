package clickhouse

case class SqlRequestParam(field: String, fieldAlias: String, request: String)

object TeamRankJoiner {
  private val base_fields = " season, league_id, round, division_level, league_unit_id, league_unit_name, team_id, team_name, match_id, "

  private val match_details_request = """SELECT
    |team_id,
    |team_name,
    |    {field_alias},
    |    rowNumberInAllBlocks() AS {field_alias}_position
    | FROM
    |(
    |    SELECT
    |team_id,
    |team_name,
    |        {field} AS {field_alias}
    |    FROM {database}.match_details
    |    WHERE {where}
    |    ORDER BY {field_alias} DESC, team_id ASC
    |)
    |ORDER BY team_id ASC""".stripMargin

  private val team_details_request = """SELECT
    |team_id,
    |team_name,
    |    {field_alias},
    |    rowNumberInAllBlocks() AS {field_alias}_position
    | FROM
    |(
    |    SELECT
    |team_id,
    |team_name,
    |        {field} AS {field_alias}
    |    FROM {database}.team_details
    |    WHERE {where}
    |    ORDER BY {field_alias} DESC, team_id ASC
    |)
    |ORDER BY team_id ASC""".stripMargin

  private val player_stats_request = """SELECT
        team_id,
        team_name,
        {field_alias},
        rowNumberInAllBlocks() AS {field_alias}_position
    FROM
    (
        SELECT
            team_id,
            team_name,
            {field} AS {field_alias}
        FROM {database}.player_stats
        WHERE {where}
        GROUP BY
            team_id,
            team_name
        ORDER BY {field_alias} DESC, team_id ASC
    )
    ORDER BY
        team_id ASC """.stripMargin

  def createSql(season: Int, leagueId: Int, round: Int, divisionLevel: Option[Int], database: String): String = {
    val field = "rating_midfield * 3 + rating_right_def + rating_left_def + rating_mid_def + rating_right_att + rating_mid_att + rating_left_att"
    val field_alias = "hatstats"
    val whereStatement = s"""(season = $season
      |) AND (league_id =  $leagueId )
      | AND (round = $round )
      |${divisionLevel.map(dl => s"AND (division_level = $dl)").getOrElse("")} """

    val base_request = s"""SELECT
      |$base_fields
      |    $field_alias,
      |    rowNumberInAllBlocks() AS ${field_alias}_position
      |FROM
      |(
      |   SELECT
      |$base_fields
      |        $field AS $field_alias
      |    FROM $database.match_details
      |    WHERE $whereStatement
      |    ORDER BY $field_alias DESC, team_id ASC
      |)
      |ORDER BY team_id ASC""".stripMargin

      val requestParams = Seq(
        SqlRequestParam("rating_right_att + rating_mid_att + rating_left_att", "attack", match_details_request),
        SqlRequestParam("rating_midfield", "midfield", match_details_request),
        SqlRequestParam("rating_right_def + rating_left_def + rating_mid_def", "defense", match_details_request),

        SqlRequestParam("sum(tsi)", "tsi", player_stats_request),
        SqlRequestParam("sum(salary)", "salary", player_stats_request),
        SqlRequestParam("sum(rating)", "rating", player_stats_request),
        SqlRequestParam("sum(rating_end_of_match)", "rating_end_of_match", player_stats_request),
        SqlRequestParam("avg((age * 112) + days)", "age", player_stats_request),
        SqlRequestParam("sumIf(injury_level, (played_minutes > 0) AND (injury_level > 0))", "injury", player_stats_request),
        SqlRequestParam("countIf(injury_level, (played_minutes > 0) AND (injury_level > 0))", "injury_count", player_stats_request),

        SqlRequestParam("power_rating", "power_rating", team_details_request)
      )

      var newFields = "hatstats, hatstats_position"

      var oldTablePrefix = ""
      var oldFieldAlias = "hatstats"
      var request = base_request

      for (sqlRequestParam <- requestParams) {
        newFields += s", ${sqlRequestParam.fieldAlias}, ${sqlRequestParam.fieldAlias}_position"

        val sqlParamRequest = sqlRequestParam.request.replace("{field}", sqlRequestParam.field)
          .replace("{field_alias}", sqlRequestParam.fieldAlias)
          .replace("{where}", whereStatement)
          .replace("{database}", database)

        request = s"""SELECT $base_fields  $newFields FROM (
          |$request) as  ${oldTablePrefix}_${oldFieldAlias}_table LEFT JOIN (
          |  $sqlParamRequest
          |) as ${sqlRequestParam.fieldAlias}_table
          |ON ${oldTablePrefix}_${oldFieldAlias}_table.team_id = ${sqlRequestParam.fieldAlias}_table.team_id""".stripMargin

        request = s"""SELECT ${if (divisionLevel.isDefined) "'division_level'" else "'league_id'"}, $base_fields $newFields  FROM (
         |$request)""".stripMargin

        oldTablePrefix = s"${oldTablePrefix}_$oldFieldAlias"
        oldFieldAlias = sqlRequestParam.fieldAlias
      }
    s"INSERT INTO $database.team_rankings $request"
  }
}
