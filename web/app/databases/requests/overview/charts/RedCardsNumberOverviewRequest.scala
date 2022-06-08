package databases.requests.overview.charts

import sqlbuilder.functions.sum
import sqlbuilder.SqlBuilder.implicits._

object RedCardsNumberOverviewRequest extends NumbersOverviewChartRequest {
  override protected val table: String = "hattrick.player_stats"
  override protected val aggregateFunction: String = sum("red_cards").toString
}
