package databases.requests.model.team

import anorm.SqlParser.get
import anorm.~
import play.api.libs.json.{Json, OWrites}

case class TeamCards(teamSortingKey: TeamSortingKey,
                     yellowCards: Int,
                     redCards: Int)

object TeamCards {
  implicit val writes: OWrites[TeamCards] = Json.writes[TeamCards]

  val mapper = {
      get[Int]("league") ~
      get[Long]("team_id") ~
      get[String]("team_name") ~
      get[Long]("league_unit_id") ~
      get[String]("league_unit_name") ~
      get[Int]("yellow_cards") ~
      get[Int]("red_cards") map {
        case leagueId ~ teamId ~ teamName ~ leagueUnitId ~ leagueUnitName ~
          yellowCards ~ redCards =>
          val teamSortingKey = TeamSortingKey(teamId, teamName, leagueUnitId, leagueUnitName, leagueId)
          TeamCards(teamSortingKey, yellowCards, redCards)
    }
  }
}
