package databases.requests.model.team

import anorm.SqlParser.get
import anorm.~
import play.api.libs.json.Json

case class TeamCards(teamSortingKey: TeamSortingKey,
                     yellowCards: Int,
                     redCards: Int)

object TeamCards {
  implicit val writes = Json.writes[TeamCards]

  val mapper = {
      get[Long]("team_id") ~
      get[String]("team_name") ~
      get[Long]("league_unit_id") ~
      get[String]("league_unit_name") ~
      get[Int]("yellow_cards") ~
      get[Int]("red_cards") map {
        case teamId ~ teamName ~ leagueUnitId ~ leagueUnitName ~
          yellowCards ~ redCards =>
          val teamSortingKey = TeamSortingKey(teamId, teamName, leagueUnitId, leagueUnitName)
        TeamCards(teamSortingKey, yellowCards, redCards)
    }
  }
}
