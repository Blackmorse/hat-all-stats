package databases.requests.overview.charts

import sqlbuilder.functions.countIf

object InjuriesNumberOverviewChartRequest extends NumbersOverviewChartRequest {
  override protected val table: String = "hattrick.player_stats"
  override protected val aggregateFunction: String = countIf("injury_level > 0").toString
}
