package com.blackmorse.hattid.web.databases.requests.overview

import anorm.RowParser
import com.blackmorse.hattid.web.databases.requests.ClickhouseOverviewRequest
import com.blackmorse.hattid.web.databases.requests.model.overview.OverviewMatchAverages
import com.blackmorse.hattid.web.databases.sql.Fields.hatstats
import sqlbuilder.functions.{avg, avgIf}
import sqlbuilder.{Select, SqlBuilder}

object OverviewMatchAveragesRequest extends ClickhouseOverviewRequest[OverviewMatchAverages] {
  override val rowParser: RowParser[OverviewMatchAverages] = OverviewMatchAverages.mapper

  override def builder(season: Int,
                       round: Int,
                       leagueId: Option[Int],
                       divisionLevel: Option[Int]): SqlBuilder = {
    import SqlBuilder.implicits._
    Select(
      avgIf("sold_total", "is_home_match = 'home'").toInt32 `as` "avg_sold_total",
        avg(hatstats).toUInt16 `as` "avg_hatstats",
        avg("goals") `as` "avg_goals"
      )
      .from("hattrick.match_details")
      .where
        .round(round)
        .season(season)
        .leagueId(leagueId)
        .divisionLevel(divisionLevel)
        .isLeagueMatch
  }
}
