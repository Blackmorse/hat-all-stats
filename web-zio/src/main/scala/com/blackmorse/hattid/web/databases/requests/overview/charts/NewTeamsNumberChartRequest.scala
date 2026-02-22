package com.blackmorse.hattid.web.databases.requests.overview.charts

import anorm.RowParser
import com.blackmorse.hattid.web.databases.dao.RestClickhouseDAO
import com.blackmorse.hattid.web.databases.requests.ClickhouseRequest.DBIO
import com.blackmorse.hattid.web.databases.requests.ClickhouseRequest.implicits.ClauseEntryExtended
import com.blackmorse.hattid.web.databases.requests.OrderingKeyPath
import com.blackmorse.hattid.web.databases.requests.model.overview.NumbersChartModel
import com.blackmorse.hattid.web.databases.requests.overview.OverviewChartRequest
import sqlbuilder.functions.max
import sqlbuilder.{Select, SqlBuilder, WSelect, With}
import zio.ZIO


object NewTeamsNumberChartRequest extends OverviewChartRequest[NumbersChartModel] {
  override val rowParser: RowParser[NumbersChartModel] = NumbersChartModel.mapper

  override def execute(orderingKeyPath: OrderingKeyPath,
                       currentSeason: Int,
                       currentRound: Int): DBIO[List[NumbersChartModel]] = wrapErrors {
    ZIO.serviceWithZIO[RestClickhouseDAO](restClickhouseDAO =>
      restClickhouseDAO.executeZIO(builder(orderingKeyPath, currentSeason, currentRound).sqlWithParameters().build, rowParser)
        .map(numbers => numbers.filterNot(ncm => ncm.season == currentSeason && ncm.round > currentRound)) //filter out, because WITH FILL will fill out current season up to 14 round
    )
  }

  def builder(orderingKeyPath: OrderingKeyPath,
                       currentSeason: Int,
                       currentRound: Int): SqlBuilder = {


    import sqlbuilder.SqlBuilder.implicits._

    Select(
      "season",
      "round",
      "count()" `as` "count"
    ).from(
      With(
        WSelect(
          max("dt")
        ).from("hattrick.match_details")
          .where
            .season(currentSeason)
            .round(currentRound)
            .orderingKeyPath(orderingKeyPath)
            .isLeagueMatch
      ).as("dt", "nested")
        .select(
          "season",
          "round",
          s"(dt - ((($currentSeason - season) * 16) * 7)) - (($currentRound - round) * 7)" `as` "league_match_day",
          "league_match_day - founded_date" `as` "diff"
        ).from("hattrick.team_details")
        .where
          .season.greaterEqual(START_SEASON)
          .round.lessEqual(MAX_ROUND)
          .and(s" NOT (season = $currentSeason and round > $currentRound)")
          .orderingKeyPath(orderingKeyPath)
          .and("diff <= multiIf(round = 1, 21, 7)")
    ).groupBy("season", "round")
      .orderBy(s"season".asc.withFillTo(s"$currentSeason + 1"), "round".asc.withFillTo("14 + 1"))
  }
}