package databases.requests.overview

import anorm.RowParser
import databases.requests.ClickhouseOverviewRequest
import databases.requests.overview.model.TeamStatOverview

object TopVictoriesTeamsOverviewRequest extends ClickhouseOverviewRequest[TeamStatOverview] {
  override val sql: String = """
                               |SELECT
                               |  league_id,
                               |  league_unit_id,
                               |  league_unit_name,
                               |  team_id,
                               |  team_name,
                               |  number_of_victories as value
                               |FROM hattrick.team_details __where__ ORDER BY value DESC
                               | __limit__
                               |""".stripMargin

  override val rowParser: RowParser[TeamStatOverview] = TeamStatOverview.mapper
}
