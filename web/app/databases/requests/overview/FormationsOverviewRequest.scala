package databases.requests.overview

import anorm.RowParser
import databases.requests.ClickhouseOverviewRequest
import databases.requests.overview.model.FormationsOverview

object FormationsOverviewRequest extends ClickhouseOverviewRequest[FormationsOverview] {
  override val sql: String = """
     |SELECT
     |  formation, count() AS count
     |FROM hattrick.match_details
     |__where__
     |GROUP BY formation  ORDER BY count DESC""".stripMargin

  override val rowParser: RowParser[FormationsOverview] = FormationsOverview.mapper
}
