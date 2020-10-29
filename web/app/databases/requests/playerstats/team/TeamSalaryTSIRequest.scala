package databases.requests.playerstats.team

import anorm.RowParser
import databases.requests.ClickhouseStatisticsRequest
import databases.requests.model.team.TeamSalaryTSI

object TeamSalaryTSIRequest extends ClickhouseStatisticsRequest[TeamSalaryTSI] {
  override val sortingColumns: Seq[String] = Seq("tsi", "salary")
  override val aggregateSql: String = ""
  override val oneRoundSql: String =
    """SELECT
      |    argMax(team_name, round) as team_name,
      |    team_id,
      |    league_unit_id,
      |    league_unit_name,
      |    sum(tsi) AS tsi,
      |    sum(salary) AS salary
      |FROM hattrick.player_stats
      |__where__ AND (round = __round__)
      |GROUP BY
      |    team_id,
      |    league_unit_id,
      |    league_unit_name
      |ORDER BY __sortBy__ __sortingDirection__, team_id asc
      |__limit__
      |""".stripMargin

  override val rowParser: RowParser[TeamSalaryTSI] = TeamSalaryTSI.mapper
}
