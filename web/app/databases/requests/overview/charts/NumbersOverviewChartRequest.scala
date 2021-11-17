package databases.requests.overview.charts

import anorm.RowParser
import databases.dao.RestClickhouseDAO
import databases.requests.{ClickhouseRequest, OrderingKeyPath}
import databases.requests.model.overview.NumbersChartModel
import databases.requests.overview.OverviewChartRequest
import databases.sqlbuilder.Select

import scala.concurrent.Future

trait NumbersOverviewChartRequest extends OverviewChartRequest[NumbersChartModel] {
  override val rowParser: RowParser[NumbersChartModel] = NumbersChartModel.mapper

  protected val table: String
  protected val aggregateFunction: String
  protected val condition: Option[String]

  def execute(orderingKeyPath: OrderingKeyPath, currentSeason: Int, currentRound: Int)
             (implicit restClickhouseDAO: RestClickhouseDAO): Future[List[NumbersChartModel]] = {
    import databases.sqlbuilder.SqlBuilder.implicits._
    val builder = Select(
      "season",
      "round",
      aggregateFunction as "count")
      .from(table)
      .where
        .orderingKeyPath(orderingKeyPath)
        .isLeagueMatch
        .season.greaterEqual(START_SEASON)
        .round.lessEqual(MAX_ROUND)
        .and(s" NOT (season = $currentSeason and round > $currentRound)")
        .and(condition)
      .groupBy("season", "round")
      .orderBy("season".asc, "round".asc)

    restClickhouseDAO.execute(builder.build, rowParser)
  }
}
