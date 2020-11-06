package databases.requests.overview

import anorm.RowParser
import databases.requests.ClickhouseOverviewRequest
import databases.requests.overview.model.OverviewMatchAverages

object OverviewMatchAveragesRequest extends ClickhouseOverviewRequest[OverviewMatchAverages] {
  override val sql: String = """
     |SELECT
     |    toUInt32(avgIf(sold_total, is_home_match = 'home')) AS avg_sold_total,
     |    toUInt16(avg(rating_midfield * 3 + rating_left_att + rating_right_att + rating_mid_att + rating_left_def + rating_right_def + rating_mid_def)) AS avg_hatstats,
     |    avg(goals) AS avg_goals
     |FROM hattrick.match_details
     |__where__
     |""".stripMargin

  override val rowParser: RowParser[OverviewMatchAverages] = OverviewMatchAverages.mapper
}
