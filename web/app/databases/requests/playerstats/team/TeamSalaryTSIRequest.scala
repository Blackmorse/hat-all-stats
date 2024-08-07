package databases.requests.playerstats.team

import anorm.RowParser
import databases.dao.RestClickhouseDAO
import databases.requests.ClickhouseRequest.implicits.ClauseEntryExtended
import databases.requests.{ClickhouseRequest, OrderingKeyPath}
import databases.requests.model.team.TeamSalaryTSI
import models.web.{RestStatisticsParameters, Round}
import sqlbuilder.Select
import sqlbuilder.functions.sum

import scala.concurrent.Future

object TeamSalaryTSIRequest extends ClickhouseRequest[TeamSalaryTSI] {
  val sortingColumns: Seq[String] = Seq("team_tsi", "sum_salary", "players_count", "avg_salary", "avg_tsi", "salary_per_tsi")

  def execute(orderingKeyPath: OrderingKeyPath,
              parameters: RestStatisticsParameters,
              playedInLastMatch: Boolean,
              excludeZeroTsi: Boolean)
             (implicit restClickhouseDAO: RestClickhouseDAO): Future[List[TeamSalaryTSI]] = {
    if(!sortingColumns.contains(parameters.sortBy))
      throw new Exception(s"Looks like SQL injection. Field: ${parameters.sortBy}" )

    val round = parameters.statsType match {
      case Round(r) => r
    }

    val playedMinutes = if(playedInLastMatch) Some(1) else None
    val tsiGreater = if (excludeZeroTsi) 1 else 0

    import sqlbuilder.SqlBuilder.implicits._
    val builder = Select(
        "any(league_id)" as "league",
        "argMax(team_name, round)" as "team_name",
        "team_id",
        "league_unit_id",
        "league_unit_name",
        sum("tsi") as "team_tsi",
        sum("salary") as "sum_salary",
        "count()" as "players_count",
        "sum_salary / players_count".toInt64 as "avg_salary",
        "team_tsi / players_count".toInt64 as "avg_tsi",
        "if(team_tsi = 0, 0, sum_salary / team_tsi)" as "salary_per_tsi"
      ).from("hattrick.player_stats")
      .where
        .season(parameters.season)
        .orderingKeyPath(orderingKeyPath)
        .playedMinutes.greaterEqual(playedMinutes)
        .round(round)
        .tsi.greaterEqual(tsiGreater)
        .isLeagueMatch

      .groupBy("team_id", "league_unit_id", "league_unit_name")
      .orderBy(
        parameters.sortBy.to(parameters.sortingDirection.toSql),
        "team_id".asc
      ).limit(page = parameters.page, pageSize = parameters.pageSize)


    restClickhouseDAO.execute(builder.sqlWithParameters().build, rowParser)
  }

  override val rowParser: RowParser[TeamSalaryTSI] = TeamSalaryTSI.mapper
}
