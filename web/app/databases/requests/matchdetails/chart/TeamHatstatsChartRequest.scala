package databases.requests.matchdetails.chart

import anorm.RowParser
import databases.requests.ClickhouseRequest.DBIO
import databases.requests.ClickhouseRequest.implicits.ClauseEntryExtended
import databases.requests.model.team.{TeamHatstats, TeamHatstatsChart}
import databases.requests.teamrankings.ClickhouseChartRequest
import databases.requests.{ClickhouseRequest, OrderingKeyPath}
import databases.sql.Fields.{hatstats, loddarStats}
import sqlbuilder.{Select, SqlBuilder}
import zio.ZIO

object TeamHatstatsChartRequest extends ClickhouseChartRequest[TeamHatstatsChart] {
  override val rowParser: RowParser[TeamHatstatsChart] = TeamHatstatsChart.teamRatingMapper

  override def sqlBuilder(orderingKeyPath: OrderingKeyPath, season: Int): SqlBuilder = {
    import SqlBuilder.implicits.*

    Select(
      "team_id",
      "league_id" `as` "league",
      "team_name",
      "league_unit_id",
      "league_unit_name",
      "season",
      "round",
      hatstats `as` "hatstats",
      "rating_midfield" `as` "midfield",
      "toInt32(rating_right_def + rating_left_def + rating_mid_def)" `as` "defense",
      "toInt32(rating_right_att + rating_mid_att + rating_left_att)" `as` "attack",
      loddarStats `as` "loddar_stats"
    ).from("hattrick.match_details")
      .where
      .season(season)
      .orderingKeyPath(orderingKeyPath)
      .isLeagueMatch
  }
}
