package databases.requests.overview

import databases.dao.RestClickhouseDAO
import databases.requests.{ClickhouseRequest, OrderingKeyPath}

import scala.concurrent.Future

trait OverviewChartRequest[T] extends ClickhouseRequest[T] {
  protected val START_SEASON = 75 // only Russia for 74 season. Excluding this stats
  protected val MAX_ROUND = 14 //15 round exists somewhere :(

  def execute(orderingKeyPath: OrderingKeyPath, currentSeason: Int, currentRound: Int)
             (implicit restClickhouseDAO: RestClickhouseDAO): Future[List[T]]
}
