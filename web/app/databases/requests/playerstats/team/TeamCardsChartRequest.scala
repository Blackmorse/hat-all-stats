package databases.requests.playerstats.team

import anorm.RowParser
import databases.dao.RestClickhouseDAO
import databases.requests.ClickhouseRequest.DBIO
import databases.requests.ClickhouseRequest.implicits.ClauseEntryExtended
import databases.requests.model.team.TeamCardsChart
import databases.requests.{ClickhouseRequest, OrderingKeyPath}
import sqlbuilder.{Select, SqlBuilder}
import sqlbuilder.functions.*
import zio.ZIO

object TeamCardsChartRequest extends ClickhouseRequest[TeamCardsChart] {
  override val rowParser: RowParser[TeamCardsChart] = TeamCardsChart.mapper
  
  def builder(orderingKeyPath: OrderingKeyPath, season: Int): SqlBuilder = {
    import sqlbuilder.SqlBuilder.implicits.*

    Select(
      any("league_id") `as` "league",
      argMax("team_name", "round") `as` "team_name",
      "team_id",
      "league_unit_id",
      "league_unit_name",
      sum("yellow_cards") `as` "yellow_cards_round",
      sum("red_cards") `as` "red_cards_round",
      "round",
      sum("yellow_cards_round").over(partitionBy = "team_id", orderBy = "round") `as` "yellow_cards_sum",
      sum("red_cards_round").over(partitionBy = "team_id", orderBy = "round") `as` "red_cards_sum"
    ).from("hattrick.player_stats")
      .where
      .season(season)
      .orderingKeyPath(orderingKeyPath)
      .groupBy("team_id", "league_unit_id", "league_unit_name", "round")
      .orderBy("team_id".asc, "round".asc)
      
  }
  
  def execute(orderingKeyPath: OrderingKeyPath, season: Int): DBIO[List[TeamCardsChart]] =  wrapErrors {
    val simpleSql = builder(orderingKeyPath, season).sqlWithParameters().build

    ZIO.serviceWithZIO[RestClickhouseDAO](restClickhouseDAO => restClickhouseDAO.executeZIO(simpleSql, rowParser))
  }
}
