package databases.requests.overview.charts

object AverageSpectatorsChartRequest extends NumbersOverviewChartRequest {
  override protected val table: String = "hattrick.match_details"
  override protected val aggregateFunction: String = "toInt32(avgIf(sold_total, is_home_match = 'home'))"
}
