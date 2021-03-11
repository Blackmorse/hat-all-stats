package databases.requests.model.overview

import anorm.SqlParser.get
import anorm.~

final case class OverviewPlayerStats(count: Int, injuried: Int, goals: Int, yellowCards: Int, redCards: Int)

object OverviewPlayerStats {
  val mapper = {
      get[Int]("count") ~
      get[Int]("injuried") ~
      get[Int]("goals") ~
      get[Int]("yellow_cards") ~
      get[Int]("red_cards") map {
        case count ~ injuried ~ goals ~ yellowCards ~ redCards => OverviewPlayerStats(count, injuried, goals, yellowCards, redCards)
      }
  }
}