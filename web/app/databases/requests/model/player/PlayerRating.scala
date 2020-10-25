package databases.requests.model.player

import play.api.libs.json.Json
import anorm.SqlParser.get
import anorm.~

case class PlayerRating(playerSortingKey: PlayerSortingKey,
                        age: Int,
                        rating: Int,
                        ratingEndOfMatch: Int)

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
    get[Int]("rating_end_of_match") map {
      case playerId ~ firstName ~ lastName ~ teamId ~ teamName ~
        leagueUnitId ~ leagueUnitName ~ age ~ rating ~ ratingEndOfMatch =>
      val playerSortingKey = PlayerSortingKey(playerId, firstName, lastName, teamId, teamName,
        leagueUnitId, leagueUnitName)
      PlayerRating(playerSortingKey, age, rating, ratingEndOfMatch)
    }
  }
}
