package databases.requests.overview.charts

object AverageGoalsChartRequest extends NumbersOverviewChartRequest {
  override protected val table: String = "hattrick.match_details"
  override protected val aggregateFunction: String = "toInt32(avg(goals) * 100)"
}
