package databases.requests.matchdetails

import anorm.RowParser
import databases.dao.RestClickhouseDAO
import databases.requests.{ClickhouseRequest, ClickhouseStatisticsRequest, OrderingKeyPath}
import databases.requests.model.team.TeamGoalPoints
import databases.sqlbuilder.SqlBuilder
import models.web.{Accumulate, MultiplyRoundsType, RestStatisticsParameters, Round}

import scala.concurrent.Future

object TeamGoalPointsRequest extends ClickhouseRequest[TeamGoalPoints] {
  val sortingColumns: Seq[String] = Seq("won", "lost", "draw", "goals_for",
      "goals_against", "goals_difference", "points")
  val aggregateSql: String = ""
  val oneRoundSql: String = """
         |SELECT
         |    any(league_id) as league,
         |    team_id,
         |    team_name,
         |    league_unit_id,
         |    league_unit_name,
         |    sum(goals) AS goals_for,
         |    sum(enemy_goals) AS goals_against,
         |    goals_for  - goals_against as goals_difference,
         |    countIf(goals > enemy_goals) AS won,
         |    countIf(goals = enemy_goals) AS draw,
         |    countIf(goals < enemy_goals) AS lost,
         |    (3 * won) + draw AS points
         |FROM hattrick.match_details
         |__where__
         |GROUP BY
         |    team_name,
         |    team_id,
         |    league_unit_id,
         |    league_unit_name
         |__having__
         |ORDER BY
         |    __sortBy__ __sortingDirection__,
         |    goals_difference __sortingDirection__,
         |    team_id __sortingDirection__
         |__limit__""".stripMargin

  override val rowParser: RowParser[TeamGoalPoints] = TeamGoalPoints.mapper

  def execute(orderingKeyPath: OrderingKeyPath,
              parameters: RestStatisticsParameters,
              playedAllMatches: Boolean,
              currentRound: Int)
             (implicit restClickhouseDAO: RestClickhouseDAO): Future[List[TeamGoalPoints]] = {
    val sortBy = parameters.sortBy
    if(!sortingColumns.contains(sortBy))
      throw new Exception("Looks like SQL injection")

    val (sql, round) = parameters.statsType match {
      case Round(r) =>
        val tmp = oneRoundSql
        if(playedAllMatches) {
          val matches = Math.min(r, currentRound)
          (tmp.replace("__having__", s"HAVING count() >= $matches"), r)
        } else {
          (tmp.replace("__having__", ""), r)
        }
    }

    restClickhouseDAO.execute(SqlBuilder(sql)
      .where
        .applyParameters(parameters)
        .applyParameters(orderingKeyPath)
        .round.lessEqual(round)
      .sortBy(sortBy)
      .build, rowParser)
  }
}
