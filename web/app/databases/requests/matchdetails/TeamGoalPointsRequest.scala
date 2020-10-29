package databases.requests.matchdetails

import anorm.RowParser
import databases.requests.ClickhouseStatisticsRequest
import databases.requests.model.team.TeamGoalPoints

object TeamGoalPointsRequest extends ClickhouseStatisticsRequest[TeamGoalPoints] {
  override val sortingColumns: Seq[String] = Seq("won", "lost", "draw", "goals_for",
      "goals_against", "goals_difference", "points")
  override val aggregateSql: String = ""
  override val oneRoundSql: String = """
         |SELECT
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
         |__where__ and round <= __round__
         |GROUP BY
         |    team_name,
         |    team_id,
         |    league_unit_id,
         |    league_unit_name
         |ORDER BY
         |    __sortBy__ __sortingDirection__,
         |    goals_difference __sortingDirection__,
         |    team_id __sortingDirection__
         |__limit__""".stripMargin

  override val rowParser: RowParser[TeamGoalPoints] = TeamGoalPoints.mapper
}
