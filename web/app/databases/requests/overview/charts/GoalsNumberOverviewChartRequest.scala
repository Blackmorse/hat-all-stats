package databases.requests.overview.charts

object GoalsNumberOverviewChartRequest extends NumbersOverviewChartRequest {
  override protected val table: String = "hattrick.player_stats"
  override protected val aggregateFunction: String = "sum(goals)"
}