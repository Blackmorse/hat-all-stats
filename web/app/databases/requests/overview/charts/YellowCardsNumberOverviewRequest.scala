package databases.requests.overview.charts

case object YellowCardsNumberOverviewRequest extends NumbersOverviewChartRequest {
  override protected val table: String = "hattrick.player_stats"
  override protected val aggregateFunction: String = "sum(yellow_cards)"
}
