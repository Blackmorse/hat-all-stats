package databases.requests.teamdetails.chart

import anorm.RowParser
import databases.requests.ClickhouseRequest.implicits.ClauseEntryExtended
import databases.requests.model.team.TeamStreakTrophiesChart
import databases.requests.teamrankings.ClickhouseChartRequest
import databases.requests.{ClickhouseRequest, OrderingKeyPath}
import sqlbuilder.{Select, SqlBuilder}

object TeamStreakTrophiesChartRequest extends ClickhouseChartRequest[TeamStreakTrophiesChart] {
  override val rowParser: RowParser[TeamStreakTrophiesChart] = TeamStreakTrophiesChart.mapper

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
      "trophies_number",
      "number_of_victories",
      "number_of_undefeated"
    )
      .from("hattrick.team_details")
      .where
      .season(season)
      .orderingKeyPath(orderingKeyPath)
  }
}
