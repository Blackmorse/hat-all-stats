package com.blackmorse.hattid.web.databases.requests.overview

import anorm.RowParser
import com.blackmorse.hattid.web.databases.requests.ClickhouseOverviewRequest
import com.blackmorse.hattid.web.databases.requests.model.overview.FormationsOverview
import sqlbuilder.{Select, SqlBuilder}


object FormationsOverviewRequest extends ClickhouseOverviewRequest[FormationsOverview] {
  override val rowParser: RowParser[FormationsOverview] = FormationsOverview.mapper

  override def builder(season: Int,
                       round: Int,
                       leagueId: Option[Int],
                       divisionLevel: Option[Int]): SqlBuilder = {
    import SqlBuilder.implicits._

    Select(
        "formation",
        "count()" `as` "count"
      )
      .from("hattrick.match_details")
      .where
        .round(round)
        .season(season)
        .leagueId(leagueId)
        .divisionLevel(divisionLevel)
        .isLeagueMatch
      .limit(limit)
      .groupBy("formation")
      .orderBy("count".desc)
  }
}
