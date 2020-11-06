package databases.requests.overview.model

import play.api.libs.json.Json
import anorm.SqlParser.get
import anorm.~

case class NumberOverview(numberOfTeams:Int,
                          numberOfPlayers: Int,
                          injuried: Int,
                          goals: Int,
                          yellowCards: Int,
                          redCards: Int)

object NumberOverview {
  implicit val writes = Json.writes[NumberOverview]

  val mapper = {
    get[Int]("numberOfTeams") ~
    get[Int]("numberOfPlayers") ~
    get[Int]("injuried") ~
    get[Int]("goals") ~
    get[Int]("yellow_cards") ~
    get[Int]("red_cards") map {
      case numberOfTeams ~ numberOfPlayers ~ injuried ~ goals ~ yellowCards ~ redCards =>
        NumberOverview(numberOfTeams, numberOfPlayers, injuried, goals, yellowCards, redCards)
    }
  }
}
