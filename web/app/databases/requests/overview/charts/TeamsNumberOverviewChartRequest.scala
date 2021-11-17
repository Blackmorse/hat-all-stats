package databases.requests.overview.charts

object TeamsNumberOverviewChartRequest extends NumbersOverviewChartRequest {
  override protected val table: String = "hattrick.match_details"
  override protected val condition: Option[String] = None
  override protected val aggregateFunction: String = "count()"
}
