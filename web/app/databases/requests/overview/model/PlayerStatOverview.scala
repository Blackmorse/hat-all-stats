package databases.requests.overview.model

import anorm.SqlParser.get
import anorm.~
import databases.requests.model.player.PlayerSortingKey
import play.api.libs.json.Json

case class PlayerStatOverview(leagueId: Int,
                              playerSortingKey: PlayerSortingKey,
                              value: Int)

object PlayerStatOverview {
  implicit val writes = Json.writes[PlayerStatOverview]

  val mapper = {
    get[Int]("league_id") ~
    get[Long]("player_id") ~
    get[String]("first_name") ~
    get[String]("last_name") ~
    get[Long]("team_id") ~
    get[String]("team_name") ~
    get[Long]("league_unit_id") ~
    get[String]("league_unit_name") ~
    get[Int]("nationality") ~
    get[Int]("value") map {
      case leagueId ~ playerId ~ firstName ~ lastName ~ teamId ~ teamName ~
        leagueUnitId ~ leagueUnitName ~ nationality ~ value =>

        val playerSortingKey = PlayerSortingKey(playerId, firstName, lastName, teamId, teamName,
          leagueUnitId, leagueUnitName, nationality)
        PlayerStatOverview(leagueId, playerSortingKey, value)
    }
  }
}
