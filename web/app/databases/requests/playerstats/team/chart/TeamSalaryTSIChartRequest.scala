package databases.requests.playerstats.team.chart

import anorm.RowParser
import databases.dao.RestClickhouseDAO
import databases.requests.ClickhouseRequest.DBIO
import databases.requests.{ClickhouseRequest, OrderingKeyPath}
import databases.requests.model.team.TeamSalaryTSIChart
import databases.requests.ClickhouseRequest.*
import databases.requests.ClickhouseRequest.implicits.ClauseEntryExtended
import models.web.BadRequestError
import sqlbuilder.Select
import sqlbuilder.functions.sum
import zio.ZIO

object TeamSalaryTSIChartRequest extends ClickhouseRequest[TeamSalaryTSIChart] {
  override val rowParser: RowParser[TeamSalaryTSIChart] = TeamSalaryTSIChart.mapper
  
  def execute(orderingKeyPath: OrderingKeyPath,
              season: Int,
              playedInLastMatch: Boolean,
              excludeZeroTsi: Boolean): DBIO[List[TeamSalaryTSIChart]] = wrapErrors {
    import sqlbuilder.SqlBuilder.implicits.*

    val playedMinutes = if (playedInLastMatch) Some(1) else None
    val tsiGreater = if (excludeZeroTsi) 1 else 0
    
    val builder = Select(
      "any(league_id)" `as` "league",
      "argMax(team_name, round)" `as` "team_name",
      "team_id",
      "league_unit_id",
      "league_unit_name",
      "season",
      "round",
      sum("tsi") `as` "team_tsi",
      sum("salary") `as` "sum_salary",
      "count()" `as` "players_count",
      "sum_salary / players_count".toInt64 `as` "avg_salary",
      "team_tsi / players_count".toInt64 `as` "avg_tsi",
      "if(team_tsi = 0, 0, sum_salary / team_tsi)" `as` "salary_per_tsi"
    ).from("hattrick.player_stats")
      .where
      .season(season)
      .orderingKeyPath(orderingKeyPath)
      .playedMinutes.greaterEqual(playedMinutes)
      .tsi.greaterEqual(tsiGreater)
      .isLeagueMatch
      .groupBy("team_id", "league_unit_id", "league_unit_name", "season", "round")

    for {
      restClickhouseDAO <- ZIO.service[RestClickhouseDAO]
      _                 <- if(orderingKeyPath.isLeagueUnitLevel) ZIO.unit 
                            else ZIO.fail(BadRequestError("Only league level is supported"))
      result            <- restClickhouseDAO.executeZIO(builder.sqlWithParameters().build, rowParser)
    } yield result
  }
}
