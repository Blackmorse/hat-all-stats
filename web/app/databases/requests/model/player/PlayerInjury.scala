package databases.requests.model.player

import anorm.SqlParser.get
import anorm.~
import play.api.libs.json.Json

case class PlayerInjury(playerSortingKey: PlayerSortingKey,
                        age: Int,
                        injury: Int)

object PlayerInjury {
  implicit val writes = Json.writes[PlayerInjury]

  val mapper = {
    get[Int]("league_id") ~
    get[Long]("player_id") ~
    get[String]("first_name") ~
    get[String]("last_name") ~
    get[Long]("team_id") ~
    get[String]("team_name") ~
    get[Long]("league_unit_id") ~
    get[String]("league_unit_name") ~
    get[Int]("age") ~
    get[Int]("injury") ~
    get[Int]("nationality") map {
      case leagueId ~ playerId ~ firstName ~ lastName ~ teamId ~ teamName ~
        leagueUnitId ~ leagueUnitName ~ age ~ injury ~ nationality =>
        val playerSortingKey = PlayerSortingKey(playerId, firstName, lastName, teamId, teamName,
          leagueUnitId, leagueUnitName, nationality, leagueId)
        PlayerInjury(playerSortingKey, age, injury)
    }
  }
}
