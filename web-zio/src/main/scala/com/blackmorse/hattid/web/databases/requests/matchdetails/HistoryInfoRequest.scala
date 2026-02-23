package com.blackmorse.hattid.web.databases.requests.matchdetails

import anorm.RowParser
import com.blackmorse.hattid.web.databases.dao.RestClickhouseDAO
import com.blackmorse.hattid.web.databases.requests.ClickhouseRequest
import com.blackmorse.hattid.web.databases.requests.ClickhouseRequest.DBIO
import com.blackmorse.hattid.web.models.clickhouse.HistoryInfo
import sqlbuilder.{Select, SqlBuilder}
import zio.ZIO

object HistoryInfoRequest extends ClickhouseRequest[HistoryInfo] {
  override val rowParser: RowParser[HistoryInfo] = HistoryInfo.mapper

  def execute(leagueId: Option[Int],
              season: Option[Int],
              round: Option[Int]): DBIO[List[HistoryInfo]] = wrapErrors {
      RestClickhouseDAO.executeZIO(builder(leagueId, season, round).sqlWithParameters().build, rowParser)
  }
  

  def builder(leagueId: Option[Int],
              season: Option[Int],
              round: Option[Int]): SqlBuilder = {
    import sqlbuilder.SqlBuilder.implicits.*
    Select(
        "season",
        "league_id",
        "division_level",
        "round",
        "count()" `as` "cnt"
      ).from("hattrick.match_details")
      .where
        .leagueId(leagueId)
        .season(season)
        .round(round)
        .isLeagueMatch
      .groupBy(
        "season",
        "league_id",
        "division_level",
        "round"
      ).orderBy(
        "season".asc,
        "league_id".asc,
        "division_level".asc,
        "round".asc
    )
  }
}
