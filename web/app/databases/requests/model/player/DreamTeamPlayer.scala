package databases.requests.model.player

import anorm.SqlParser.get
import anorm.~
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
    get[String]("role") ~
    get[Int]("rating") ~
    get[Int]("rating_end_of_match") map {
      case playerId ~ firstName ~ lastName ~ teamId ~ teamName ~
        leagueUnitId ~ leagueUnitName ~ round ~ role ~ rating ~ ratingEndOfMatch =>
        val playerSortingKey = PlayerSortingKey(playerId, firstName, lastName, teamId, teamName,
          leagueUnitId, leagueUnitName)

        DreamTeamPlayer(playerSortingKey, round, role, rating, ratingEndOfMatch)
    }
  }
}
