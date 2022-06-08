package databases.requests.overview.charts

import sqlbuilder.functions.avgIf
import sqlbuilder.SqlBuilder.implicits._

object AverageSpectatorsChartRequest extends NumbersOverviewChartRequest {
  override protected val table: String = "hattrick.match_details"
  override protected val aggregateFunction: String = avgIf("sold_total", "is_home_match = 'home'").toInt32.toString
}
