package com.blackmorse.hattid.web.databases.requests.overview.charts

import anorm.RowParser
import com.blackmorse.hattid.web.databases.dao.RestClickhouseDAO
import com.blackmorse.hattid.web.databases.requests.ClickhouseRequest.DBIO
import com.blackmorse.hattid.web.databases.requests.ClickhouseRequest.implicits.ClauseEntryExtended
import com.blackmorse.hattid.web.databases.requests.OrderingKeyPath
import com.blackmorse.hattid.web.databases.requests.model.overview.FormationChartModel
import com.blackmorse.hattid.web.databases.requests.overview.OverviewChartRequest
import sqlbuilder.Select
import zio.ZIO

object FormationsChartRequest extends OverviewChartRequest[FormationChartModel] {
  override val rowParser: RowParser[FormationChartModel] = FormationChartModel.mapper

  def execute(orderingKeyPath: OrderingKeyPath, currentSeason: Int, currentRound: Int): DBIO[List[FormationChartModel]] = wrapErrors {
    import sqlbuilder.SqlBuilder.implicits._
    val builder = Select(
      "formation",
      "season",
      "round",
      "count()" `as` "count"
    ).from("hattrick.match_details")
      .where
        .orderingKeyPath(orderingKeyPath)
        .isLeagueMatch
        .season.greaterEqual(START_SEASON)
        .round.lessEqual(MAX_ROUND)
        .and(s"NOT (season = $currentSeason and round > $currentRound)")
        .and("formation in ('3-5-2','4-4-2','2-5-3','3-4-3','4-5-1','5-3-2','5-4-1','4-3-3','5-5-0','5-2-3')")
      .groupBy("season", "round", "formation")
      .orderBy("season".asc, "round".asc)
      .limitBy(10, "(season, round)")

    RestClickhouseDAO.executeZIO(builder.sqlWithParameters().build, rowParser)
  }
}
