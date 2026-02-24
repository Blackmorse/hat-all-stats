package com.blackmorse.hattid.web.databases.requests.playerstats.team.chart

import anorm.RowParser
import com.blackmorse.hattid.web.databases.requests.ClickhouseRequest.implicits.ClauseEntryExtended
import com.blackmorse.hattid.web.databases.requests.model.team.TeamCardsChart
import com.blackmorse.hattid.web.databases.requests.teamrankings.ClickhouseChartRequest
import com.blackmorse.hattid.web.databases.requests.{ClickhouseRequest, OrderingKeyPath}
import sqlbuilder.functions.*
import sqlbuilder.{Select, SqlBuilder}

object TeamCardsChartRequest extends ClickhouseChartRequest[TeamCardsChart] {
  override val rowParser: RowParser[TeamCardsChart] = TeamCardsChart.mapper
  
  def sqlBuilder(orderingKeyPath: OrderingKeyPath, season: Int): SqlBuilder = {
    import sqlbuilder.SqlBuilder.implicits.*
    Select(
      any("league_id") `as` "league",
      argMax("team_name", "round") `as` "team_name",
      "team_id",
      "league_unit_id",
      "league_unit_name",
      sum("yellow_cards") `as` "yellow_cards_round",
      sum("red_cards") `as` "red_cards_round",
      "season",
      "round",
      sum("yellow_cards_round").over(partitionBy = "team_id", orderBy = "round") `as` "yellow_cards_sum",
      sum("red_cards_round").over(partitionBy = "team_id", orderBy = "round") `as` "red_cards_sum"
    ).from("hattrick.player_stats")
      .where
      .season(season)
      .orderingKeyPath(orderingKeyPath)
      .groupBy("team_id", "league_unit_id", "league_unit_name", "season", "round")
      .orderBy("team_id".asc, "round".asc)
  }
}
