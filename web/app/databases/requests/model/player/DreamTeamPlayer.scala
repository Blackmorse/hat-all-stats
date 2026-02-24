package databases.requests.model.player

import anorm.SqlParser.get
import anorm.{RowParser, ~}
import databases.requests.model.Roles
import play.api.libs.json.{Json, OWrites}
import zio.json.{DeriveJsonEncoder, JsonEncoder}

case class DreamTeamPlayer(playerSortingKey: PlayerSortingKey,
                           round: Int,
                           role: String,
                           rating: Int,
                           ratingEndOfMatch: Int)

object DreamTeamPlayer {
  implicit val writes: OWrites[DreamTeamPlayer] = Json.writes[DreamTeamPlayer]
  implicit val jsonEncoder: JsonEncoder[DreamTeamPlayer] = DeriveJsonEncoder.gen[DreamTeamPlayer]

  val mapper: RowParser[DreamTeamPlayer] = {
    get[Int]("league_id") ~
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
      case leagueId ~ playerId ~ firstName ~ lastName ~ teamId ~ teamName ~
        leagueUnitId ~ leagueUnitName ~ round ~ role ~ rating ~ ratingEndOfMatch ~ nationality =>
        val playerSortingKey = PlayerSortingKey(playerId, firstName, lastName, teamId, teamName,
          leagueUnitId, leagueUnitName, nationality, leagueId)

        DreamTeamPlayer(playerSortingKey, round, Roles.mapping(role), rating, ratingEndOfMatch)
    }
  }
}
