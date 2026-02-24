package com.blackmorse.hattid.web.databases.requests.teamdetails.chart

import anorm.RowParser
import com.blackmorse.hattid.web.databases.requests.ClickhouseRequest.implicits.ClauseEntryExtended
import com.blackmorse.hattid.web.databases.requests.model.team.TeamStreakTrophiesChart
import com.blackmorse.hattid.web.databases.requests.teamrankings.ClickhouseChartRequest
import com.blackmorse.hattid.web.databases.requests.{ClickhouseRequest, OrderingKeyPath}
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
