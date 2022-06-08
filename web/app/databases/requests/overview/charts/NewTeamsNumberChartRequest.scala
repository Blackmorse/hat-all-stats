package databases.requests.overview.charts

import anorm.RowParser
import databases.dao.RestClickhouseDAO
import databases.requests.ClickhouseRequest.implicits.{ClauseEntryExtended, SqlWithParametersExtended}
import databases.requests.OrderingKeyPath
import databases.requests.model.overview.NumbersChartModel
import databases.requests.overview.OverviewChartRequest
import sqlbuilder.functions.max
import sqlbuilder.{Select, SqlBuilder, WSelect, With}

//TODO ?!
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object NewTeamsNumberChartRequest extends OverviewChartRequest[NumbersChartModel] {
  override val rowParser: RowParser[NumbersChartModel] = NumbersChartModel.mapper

  override def execute(orderingKeyPath: OrderingKeyPath,
    currentSeason: Int,
    currentRound: Int)(implicit restClickhouseDAO: RestClickhouseDAO): Future[List[NumbersChartModel]] = {

    restClickhouseDAO.execute(builder(orderingKeyPath, currentSeason, currentRound).sqlWithParameters().build, rowParser)
      .map(numbers => numbers.filterNot(ncm => ncm.season == currentSeason && ncm.round > currentRound)) //filter out, because WITH FILL will fill out current season up to 14 round
  }

  def builder(orderingKeyPath: OrderingKeyPath,
                       currentSeason: Int,
                       currentRound: Int): SqlBuilder = {


    import sqlbuilder.SqlBuilder.implicits._

    Select(
      "season",
      "round",
      "count()" as "count"
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
          s"(dt - ((($currentSeason - season) * 16) * 7)) - (($currentRound - round) * 7)" as "league_match_day",
          "league_match_day - founded_date" as "diff"
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