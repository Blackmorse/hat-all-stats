package databases.requests.playerstats.team

import anorm.RowParser
import databases.dao.RestClickhouseDAO
import databases.requests.{ClickhouseRequest, OrderingKeyPath}
import databases.requests.model.team.TeamSalaryTSI
import databases.sqlbuilder.{Select, SqlBuilder}
import models.web.{RestStatisticsParameters, Round}

import scala.concurrent.Future

object TeamSalaryTSIRequest extends ClickhouseRequest[TeamSalaryTSI] {
  val sortingColumns: Seq[String] = Seq("tsi", "salary", "players_count", "avg_salary", "avg_tsi", "salary_per_tsi")

  def execute(orderingKeyPath: OrderingKeyPath,
              parameters: RestStatisticsParameters,
              playedInLastMatch: Boolean)
             (implicit restClickhouseDAO: RestClickhouseDAO): Future[List[TeamSalaryTSI]] = {
    if(!sortingColumns.contains(parameters.sortBy))
      throw new Exception("Looks like SQL injection")

    val round = parameters.statsType match {
      case Round(r) => r
    }

    val playedMinutes = if(playedInLastMatch) Some(1) else None

    import SqlBuilder.implicits._
    val builder = Select(
        "any(league_id)" as "league",
        "argMax(team_name, round)" as "team_name",
        "team_id",
        "league_unit_id",
        "league_unit_name",
        "sum(tsi)" as "tsi",
        "sum(salary)" as "salary",
        "count()" as "players_count",
        "salary / players_count".toInt64 as "avg_salary",
        "tsi / players_count".toInt64 as "avg_tsi",
        "salary / tsi" as "salary_per_tsi"
      ).from("hattrick.player_stats")
      .where
        .season(parameters.season)
        .orderingKeyPath(orderingKeyPath)
        .playedMinutes.greaterEqual(playedMinutes)
        .round(round)
        .isLeagueMatch
      .groupBy("team_id", "league_unit_id", "league_unit_name")
      .orderBy(
        parameters.sortBy.to(parameters.sortingDirection),
        "team_id".asc
      ).limit(page = parameters.page, pageSize = parameters.pageSize)


    restClickhouseDAO.execute(builder.build, rowParser)
  }

  override val rowParser: RowParser[TeamSalaryTSI] = TeamSalaryTSI.mapper
}
