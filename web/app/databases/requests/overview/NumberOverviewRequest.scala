package databases.requests.overview

import anorm.RowParser
import databases.requests.ClickhouseOverviewRequest
import databases.requests.overview.model.NumberOverview

object NumberOverviewRequest extends ClickhouseOverviewRequest[NumberOverview] {
  override val sql: String = """
                               |SELECT
                               |  uniq(team_id) as numberOfTeams,
                               |  count() as numberOfPlayers,
                               |  countIf(injury_level > 0) AS injuried,
                               |  sum(goals) as goals,
                               |  sum(yellow_cards) as yellow_cards,
                               |  sum(red_cards) as red_cards
                               |FROM hattrick.player_stats
                               |  __where__""".stripMargin

  override val rowParser: RowParser[NumberOverview] = NumberOverview.mapper
}
