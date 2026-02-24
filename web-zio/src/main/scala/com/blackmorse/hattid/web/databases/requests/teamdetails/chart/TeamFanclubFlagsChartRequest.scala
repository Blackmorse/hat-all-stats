package com.blackmorse.hattid.web.databases.requests.teamdetails.chart

import anorm.RowParser
import com.blackmorse.hattid.web.databases.requests.ClickhouseRequest.implicits.ClauseEntryExtended
import com.blackmorse.hattid.web.databases.requests.model.team.TeamFanclubFlagsChart
import com.blackmorse.hattid.web.databases.requests.teamrankings.ClickhouseChartRequest
import com.blackmorse.hattid.web.databases.requests.{ClickhouseRequest, OrderingKeyPath}
import sqlbuilder.{Select, SqlBuilder}

object TeamFanclubFlagsChartRequest extends ClickhouseChartRequest[TeamFanclubFlagsChart] {
  override val rowParser: RowParser[TeamFanclubFlagsChart] = TeamFanclubFlagsChart.mapper
  
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
      "fanclub_size",
      "home_flags",
      "away_flags",
      "home_flags + away_flags" `as` "all_flags"
    )
      .from("hattrick.team_details")
      .where
      .season(season)
      .orderingKeyPath(orderingKeyPath)
  }  
}
