package clickhouse

import chpp.worlddetails.models.League
import com.crobox.clickhouse.ClickhouseClient
import com.typesafe.config.Config
import hattid.LoddarStatsUtils
import org.slf4j.LoggerFactory

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Success, Try}

case class SqlRequestParam(field: String,
                           fieldAlias: String,
                           request: String,
                           cupLevelField: Boolean,
                           direction: String = "DESC")

object TeamRankJoiner {
  private val logger = LoggerFactory.getLogger(this.getClass)

  def joinTeamRankings(config: Config, league: League)
                      (implicit executionContext: ExecutionContext): Future[Try[Unit]] = {
    logger.info(s"Executing team_rankings join request for (${league.leagueId}, ${league.leagueName})")
    val client = new ClickhouseClient(Some(config))
    val seqFuture = (1 to league.numberOfLevels).map(Some(_)).concat(Seq(None))
      .map(level => {
        val sql = TeamRankJoiner.createSql(
          season = league.season - league.seasonOffset,
          leagueId = league.leagueId,
          round = league.matchRound - 1,
          divisionLevel = level,
          database = config.getString("database_name")
        )
        client.execute(sql)
      })

    Future.sequence(seqFuture).map(_ => Success(()))
  }

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
    |""".stripMargin

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
    |    ORDER BY {field_alias} {direction}, team_id ASC
    |)
    |""".stripMargin

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
        """.stripMargin

  def createSql(season: Int, leagueId: Int, round: Int, divisionLevel: Option[Int], database: String): String = {
    val field = "rating_midfield * 3 + rating_right_def + rating_left_def + rating_mid_def + rating_right_att + rating_mid_att + rating_left_att"
    val field_alias = "hatstats"
    def whereStatement(cupLevelField: Boolean) = s"""(season = $season
      |) AND (league_id =  $leagueId )
      | AND (round = $round )
      |${divisionLevel.map(dl => s" AND (division_level = $dl)").getOrElse("")}
      |${if (cupLevelField) s" AND (cup_level = 0)" else ""}
      |"""

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
      |    WHERE ${whereStatement(true)}
      |    ORDER BY $field_alias DESC, team_id ASC
      |)
      |""".stripMargin

      val requestParams = Seq(
        SqlRequestParam("rating_right_att + rating_mid_att + rating_left_att", "attack", match_details_request, cupLevelField = true),
        SqlRequestParam("rating_midfield", "midfield", match_details_request, cupLevelField = true),
        SqlRequestParam("rating_right_def + rating_left_def + rating_mid_def", "defense", match_details_request, cupLevelField = true),
        SqlRequestParam(LoddarStatsUtils.homeLoddarStats, "loddar_stats", match_details_request, cupLevelField = true),

        SqlRequestParam("sum(tsi)", "tsi", player_stats_request, cupLevelField = true),
        SqlRequestParam("sum(salary)", "salary", player_stats_request, cupLevelField = true),
        SqlRequestParam("sum(rating)", "rating", player_stats_request, cupLevelField = true),
        SqlRequestParam("sum(rating_end_of_match)", "rating_end_of_match", player_stats_request, cupLevelField = true),
        SqlRequestParam("avg((age * 112) + days)", "age", player_stats_request, cupLevelField = true),
        SqlRequestParam("sumIf(injury_level, (played_minutes > 0) AND (injury_level > 0))", "injury", player_stats_request, cupLevelField = true),
        SqlRequestParam("countIf(injury_level, (played_minutes > 0) AND (injury_level > 0))", "injury_count", player_stats_request, cupLevelField = true),


        SqlRequestParam("power_rating", "power_rating", team_details_request, cupLevelField = false),
        SqlRequestParam("founded_date", "founded", team_details_request, cupLevelField = false, direction = "ASC")
      )

      var newFields = "hatstats, hatstats_position"

      var oldTablePrefix = ""
      var oldFieldAlias = "hatstats"
      var request = base_request

      for (sqlRequestParam <- requestParams) {
        newFields += s", ${sqlRequestParam.fieldAlias}, ${sqlRequestParam.fieldAlias}_position"

        val sqlParamRequest = sqlRequestParam.request.replace("{field}", sqlRequestParam.field)
          .replace("{field_alias}", sqlRequestParam.fieldAlias)
          .replace("{where}", whereStatement(sqlRequestParam.cupLevelField))
          .replace("{database}", database)
          .replace("{direction}", sqlRequestParam.direction)

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
