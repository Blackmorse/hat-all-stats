package databases.requests.teamdetails.chart

import anorm.RowParser
import databases.requests.ClickhouseRequest.implicits.ClauseEntryExtended
import databases.requests.model.team.TeamPowerRatingChart
import databases.requests.teamrankings.ClickhouseChartRequest
import databases.requests.{ClickhouseRequest, OrderingKeyPath}
import sqlbuilder.{Select, SqlBuilder}

object TeamPowerRatingsChartRequest extends ClickhouseChartRequest[TeamPowerRatingChart] {

  override val rowParser: RowParser[TeamPowerRatingChart] = TeamPowerRatingChart.mapper

  override def sqlBuilder(orderingKeyPath: OrderingKeyPath, season: Int): SqlBuilder = {
    import SqlBuilder.implicits.*
    Select(
      "league_id",
      "team_id",
      "team_name",
      "league_unit_id",
      "league_unit_name",
      "season",
      "round",
      "power_rating"
    )
      .from("hattrick.team_details")
      .where
      .season(season)
      .orderingKeyPath(orderingKeyPath)
  }
}
