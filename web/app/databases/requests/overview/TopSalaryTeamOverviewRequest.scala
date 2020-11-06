package databases.requests.overview

import anorm.RowParser
import databases.requests.ClickhouseOverviewRequest
import databases.requests.overview.model.TeamStatOverview

object TopSalaryTeamOverviewRequest extends ClickhouseOverviewRequest[TeamStatOverview] {
  override val sql: String = """
       |SELECT
       |  league_id,
       |  league_unit_id,
       |  league_unit_name,
       |  team_name,
       |  team_id,
       |  sum(salary) AS value
       |FROM hattrick.player_stats
       |__where__
       |GROUP BY  league_id, league_unit_id, league_unit_name, team_id, team_name
       |ORDER BY value DESC
       |__limit__
       |""".stripMargin

  override val rowParser: RowParser[TeamStatOverview] = TeamStatOverview.mapper
}
