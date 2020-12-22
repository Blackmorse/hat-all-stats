package databases.requests.model.player

import play.api.libs.json.Json
import anorm.SqlParser.get
import anorm.~
import databases.requests.model.Roles

case class PlayerRating(playerSortingKey: PlayerSortingKey,
                        age: Int,
                        rating: Int,
                        ratingEndOfMatch: Int,
                        role: String)

object PlayerRating {
  implicit val writes = Json.writes[PlayerRating]

  val mapper = {
    get[Long]("player_id") ~
    get[String]("first_name") ~
    get[String]("last_name") ~
    get[Long]("team_id") ~
    get[String]("team_name") ~
    get[Long]("league_unit_id") ~
    get[String]("league_unit_name") ~
    get[Int]("age") ~
    get[Int]("rating") ~
    get[Int]("rating_end_of_match") ~
    get[Int]("nationality") ~
    get[Int]("role") map {
      case playerId ~ firstName ~ lastName ~ teamId ~ teamName ~
        leagueUnitId ~ leagueUnitName ~ age ~ rating ~ ratingEndOfMatch ~ nationality ~ role =>
      val playerSortingKey = PlayerSortingKey(playerId, firstName, lastName, teamId, teamName,
        leagueUnitId, leagueUnitName, nationality)
      PlayerRating(playerSortingKey, age, rating, ratingEndOfMatch, Roles.mapping(role))
    }
  }
}
