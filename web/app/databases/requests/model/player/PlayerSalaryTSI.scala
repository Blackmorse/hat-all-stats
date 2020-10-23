package databases.requests.model.player

import play.api.libs.json.Json
import anorm.SqlParser.get
import anorm.~

case class PlayerSalaryTSI(playerSortingKey: PlayerSortingKey,
                           age: Int,
                           tsi: Int,
                           salary: Int)

object PlayerSalaryTSI {
  implicit val writes = Json.writes[PlayerSalaryTSI]

  val mapper = {
    get[Long]("player_id") ~
    get[String]("first_name") ~
    get[String]("last_name") ~
    get[Long]("team_id") ~
    get[String]("team_name") ~
    get[Long]("league_unit_id") ~
    get[String]("league_unit_name") ~
    get[Int]("age") ~
    get[Int]("tsi") ~
    get[Int]("salary") map {
      case playerId ~ firstName ~ lastName ~ teamId ~ teamName ~
        leagueUnitId ~ leagueUnitName ~ age ~ tsi ~ salary =>
        val playerSortingKey = PlayerSortingKey(playerId, firstName, lastName, teamId, teamName,
          leagueUnitId, leagueUnitName)
        PlayerSalaryTSI(playerSortingKey, age, tsi, salary)
    }
  }
}
