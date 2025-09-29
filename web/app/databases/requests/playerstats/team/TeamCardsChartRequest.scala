package databases.requests.playerstats.team

import anorm.RowParser
import databases.requests.{ClickhouseRequest, OrderingKeyPath}
import databases.requests.model.team.TeamCards
import models.web.{HattidError, RestStatisticsParameters}

class TeamCardsChartRequest extends ClickhouseRequest[TeamCards] {
  override val rowParser: RowParser[TeamCards] = TeamCards.mapper
  
//  def execute(orderingKeyPath: OrderingKeyPath, 
//              parameters: RestStatisticsParameters): zio.IO[HattidError, List[TeamCards]] = {
//    
//  }
}
