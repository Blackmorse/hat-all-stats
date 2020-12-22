package databases.requests.model.player

import anorm.SqlParser.get
import anorm.~
import databases.requests.model.Roles
import play.api.libs.json.Json

case class DreamTeamPlayer(playerSortingKey: PlayerSortingKey,
                           round: Int,
                           role: String,
                           rating: Int,
                           ratingEndOfMatch: Int)

object DreamTeamPlayer {
  implicit val writes = Json.writes[DreamTeamPlayer]

  val mapper = {
    get[Long]("player_id") ~
    get[String]("first_name") ~
    get[String]("last_name") ~
    get[Long]("team_id") ~
    get[String]("team_name") ~
    get[Long]("league_unit_id") ~
    get[String]("league_unit_name") ~
    get[Int]("round") ~
    get[Int]("role") ~
    get[Int]("rating") ~
    get[Int]("rating_end_of_match") ~
    get[Int]("nationality") map {
      case playerId ~ firstName ~ lastName ~ teamId ~ teamName ~
        leagueUnitId ~ leagueUnitName ~ round ~ role ~ rating ~ ratingEndOfMatch ~ nationality =>
        val playerSortingKey = PlayerSortingKey(playerId, firstName, lastName, teamId, teamName,
          leagueUnitId, leagueUnitName, nationality)

        DreamTeamPlayer(playerSortingKey, round, Roles.mapping(role), rating, ratingEndOfMatch)
    }
  }
}
