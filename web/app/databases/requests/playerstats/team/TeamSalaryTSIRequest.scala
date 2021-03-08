package databases.requests.playerstats.team

import anorm.RowParser
import databases.SqlBuilder
import databases.dao.RestClickhouseDAO
import databases.requests.{ClickhouseRequest, OrderingKeyPath}
import databases.requests.model.team.TeamSalaryTSI
import models.web.{RestStatisticsParameters, Round}

import scala.concurrent.Future

object TeamSalaryTSIRequest extends ClickhouseRequest[TeamSalaryTSI] {
  val sortingColumns: Seq[String] = Seq("tsi", "salary", "players_count", "avg_salary", "avg_tsi")

  val oneRoundSql: String =
    """SELECT
      |    any(league_id) as league,
      |    argMax(team_name, round) as team_name,
      |    team_id,
      |    league_unit_id,
      |    league_unit_name,
      |    sum(tsi) AS tsi,
      |    sum(salary) AS salary,
      |    count() as players_count,
      |    toUInt64(salary / players_count) as avg_salary,
      |    toUInt64(tsi / players_count) as avg_tsi
      |FROM hattrick.player_stats
      |__where__ AND (round = __round__)
      |GROUP BY
      |    team_id,
      |    league_unit_id,
      |    league_unit_name
      |ORDER BY __sortBy__ __sortingDirection__, team_id asc
      |__limit__
      |""".stripMargin

  def execute(orderingKeyPath: OrderingKeyPath,
              parameters: RestStatisticsParameters,
              playedInLastMatch: Boolean)
             (implicit restClickhouseDAO: RestClickhouseDAO): Future[List[TeamSalaryTSI]] = {
    if(!sortingColumns.contains(parameters.sortBy))
      throw new Exception("Looks like SQL injection")

    val sql = parameters.statsType match {
      case Round(round) => oneRoundSql.replace("__round__", round.toString).replace("__sortBy__", parameters.sortBy)
    }

    val playedMinutes = if(playedInLastMatch) Some(1) else None

    restClickhouseDAO.execute(SqlBuilder(sql)
      .applyParameters(parameters)
      .where
        .applyParameters(orderingKeyPath)
        .playedMinutes.greaterEqual(playedMinutes)
      .build, rowParser)
  }

  override val rowParser: RowParser[TeamSalaryTSI] = TeamSalaryTSI.mapper
}
