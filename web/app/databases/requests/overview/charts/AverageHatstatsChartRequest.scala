package databases.requests.overview.charts

import databases.sqlbuilder.SqlBuilder.fields.hatstats

object AverageHatstatsChartRequest extends NumbersOverviewChartRequest {
  override protected val table: String = "hattrick.match_details"
  override protected val aggregateFunction: String = s"toInt32(avg($hatstats))"
}
