package databases.requests.overview

import databases.requests.ClickhouseRequest.DBIO
import databases.requests.{ClickhouseRequest, OrderingKeyPath}

trait OverviewChartRequest[T] extends ClickhouseRequest[T] {
  protected val START_SEASON = 75 // only Russia for 74 season. Excluding this stats
  protected val MAX_ROUND = 14 //15 round exists somewhere :(
  
  def execute(orderingKeyPath: OrderingKeyPath, currentSeason: Int, currentRound: Int): DBIO[List[T]]
}
