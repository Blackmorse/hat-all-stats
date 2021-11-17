package databases.requests.overview.charts

object PlayersNumberOverviewChartRequest extends NumbersOverviewChartRequest {
  override protected val table: String = "hattrick.player_stats"
  override protected val condition: Option[String] = None
  override protected val aggregateFunction: String = "count()"
}
