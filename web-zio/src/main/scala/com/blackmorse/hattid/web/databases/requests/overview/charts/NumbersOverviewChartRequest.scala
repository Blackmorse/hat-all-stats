package com.blackmorse.hattid.web.databases.requests.overview.charts

import anorm.RowParser
import com.blackmorse.hattid.web.databases.dao.RestClickhouseDAO
import com.blackmorse.hattid.web.databases.requests.ClickhouseRequest.DBIO
import com.blackmorse.hattid.web.databases.requests.ClickhouseRequest.implicits.ClauseEntryExtended
import com.blackmorse.hattid.web.databases.requests.OrderingKeyPath
import com.blackmorse.hattid.web.databases.requests.model.overview.NumbersChartModel
import com.blackmorse.hattid.web.databases.requests.overview.OverviewChartRequest
import sqlbuilder.{Select, SqlBuilder}
import zio.ZIO

trait NumbersOverviewChartRequest extends OverviewChartRequest[NumbersChartModel] {
  override val rowParser: RowParser[NumbersChartModel] = NumbersChartModel.mapper

  protected val table: String
  protected val aggregateFunction: String

  def execute(orderingKeyPath: OrderingKeyPath, currentSeason: Int, currentRound: Int): DBIO[List[NumbersChartModel]] = wrapErrors {
    ZIO.serviceWithZIO[RestClickhouseDAO](restClickhouseDAO =>
      restClickhouseDAO.executeZIO(builder(orderingKeyPath, currentSeason, currentRound).sqlWithParameters().build, rowParser))
  }

  def builder(orderingKeyPath: OrderingKeyPath, currentSeason: Int, currentRound: Int): SqlBuilder = {
    import sqlbuilder.SqlBuilder.implicits._
    Select(
      "season",
      "round",
      aggregateFunction `as` "count")
      .from(table)
      .where
        .orderingKeyPath(orderingKeyPath)
        .isLeagueMatch
        .season.greaterEqual(START_SEASON)
        .round.lessEqual(MAX_ROUND)
        .and(s" NOT (season = $currentSeason and round > $currentRound)")
      .groupBy("season", "round")
      .orderBy("season".asc, "round".asc)
  }
}
