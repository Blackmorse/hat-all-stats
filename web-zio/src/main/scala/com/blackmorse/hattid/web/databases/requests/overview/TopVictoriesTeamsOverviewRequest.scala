package com.blackmorse.hattid.web.databases.requests.overview

import anorm.RowParser
import com.blackmorse.hattid.web.databases.requests.ClickhouseOverviewRequest
import com.blackmorse.hattid.web.databases.requests.model.overview.TeamStatOverview
import sqlbuilder.{Select, SqlBuilder}

object TopVictoriesTeamsOverviewRequest extends ClickhouseOverviewRequest[TeamStatOverview] {
  override val rowParser: RowParser[TeamStatOverview] = TeamStatOverview.mapper

  override def builder(season: Int,
                       round: Int,
                       leagueId: Option[Int],
                       divisionLevel: Option[Int]): SqlBuilder = {
    import SqlBuilder.implicits._
    Select(
        "league_id",
        "league_unit_id",
        "league_unit_name",
        "team_id",
        "team_name",
        "number_of_victories" `as` "value"
      )
      .from("hattrick.team_details")
      .where
        .round(round)
        .season(season)
        .leagueId(leagueId)
        .divisionLevel(divisionLevel)
      .orderBy("value".desc)
      .limit(limit)
  }
}
