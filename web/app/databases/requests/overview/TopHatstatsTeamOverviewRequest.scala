package databases.requests.overview

import anorm.RowParser
import databases.requests.ClickhouseOverviewRequest
import databases.requests.overview.model.TeamStatOverview

object TopHatstatsTeamOverviewRequest extends ClickhouseOverviewRequest[TeamStatOverview] {
  override val sql: String = """
     |SELECT
     |  league_id,
     |  league_unit_id,
     |  league_unit_name,
     |  team_id,
     |  team_name,
     |  rating_midfield * 3 + rating_left_def + rating_mid_def + rating_right_def + rating_left_att + rating_right_att + rating_mid_att AS value
     |FROM hattrick.match_details __where__ ORDER BY value DESC
     | __limit__
     |""".stripMargin

  override val rowParser: RowParser[TeamStatOverview] = TeamStatOverview.mapper
}
