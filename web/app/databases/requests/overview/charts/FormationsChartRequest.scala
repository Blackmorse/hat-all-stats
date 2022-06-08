package databases.requests.overview.charts

import anorm.RowParser
import databases.dao.RestClickhouseDAO
import databases.requests.ClickhouseRequest.implicits.{ClauseEntryExtended, SqlWithParametersExtended}
import databases.requests.OrderingKeyPath
import databases.requests.model.overview.FormationChartModel
import databases.requests.overview.OverviewChartRequest
import sqlbuilder.Select

import scala.concurrent.Future

object FormationsChartRequest extends OverviewChartRequest[FormationChartModel] {
  override val rowParser: RowParser[FormationChartModel] = FormationChartModel.mapper

  def execute(orderingKeyPath: OrderingKeyPath, currentSeason: Int, currentRound: Int)
             (implicit restClickhouseDAO: RestClickhouseDAO): Future[List[FormationChartModel]] = {
    import sqlbuilder.SqlBuilder.implicits._
    val builder = Select(
      "formation",
      "season",
      "round",
      "count()" as "count"
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

    restClickhouseDAO.execute(builder.sqlWithParameters().build, rowParser)
  }
}
