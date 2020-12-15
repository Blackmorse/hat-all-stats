package databases.requests.overview

import anorm.RowParser
import databases.requests.ClickhouseOverviewRequest
import databases.requests.overview.model.PlayerStatOverview

object TopSalaryPlayerOverviewRequest extends ClickhouseOverviewRequest[PlayerStatOverview] {
  override val sql: String = """
       |SELECT
       |  league_id,
       |  league_unit_id,
       |  league_unit_name,
       |  team_id,
       |  team_name,
       |  player_id,
       |  first_name,
       |  last_name,
       |  salary AS value,
       |  nationality
       |FROM hattrick.player_stats
       |__where__
       |ORDER BY value DESC
       |__limit__
       |""".stripMargin
  override val rowParser: RowParser[PlayerStatOverview] = PlayerStatOverview.mapper
}
