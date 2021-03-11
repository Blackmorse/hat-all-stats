package databases.requests.model.overview

import anorm.SqlParser.get
import anorm.~

case class AvgMatchDetailsModel(avgHatstats: Int, avgSoldTickets: Int, avgScoredByTeam: Double)

object AvgMatchDetailsModel {
  val mapper = {
    get[Int]("avg_hatstats") ~
      get[Int]("avg_sold_total") ~
      get[Double]("avg_goals") map {
      case avgHatstats ~ avgSoldTickets ~ avgScoredByTeam => AvgMatchDetailsModel(avgHatstats, avgSoldTickets, avgScoredByTeam)
    }
  }
}
