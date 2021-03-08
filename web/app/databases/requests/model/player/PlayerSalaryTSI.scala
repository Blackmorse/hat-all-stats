package databases.requests.model.player

import play.api.libs.json.Json
import anorm.SqlParser.get
import anorm.~
import databases.requests.model.Roles

case class PlayerSalaryTSI(playerSortingKey: PlayerSortingKey,
                           age: Int,
                           tsi: Int,
                           salary: Int,
                           role: String)

object PlayerSalaryTSI {
  implicit val writes = Json.writes[PlayerSalaryTSI]

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
    get[Int]("tsi") ~
    get[Int]("salary") ~
    get[Int]("nationality") ~
    get[Int]("role") map {
      case leagueId ~ playerId ~ firstName ~ lastName ~ teamId ~ teamName ~
        leagueUnitId ~ leagueUnitName ~ age ~ tsi ~ salary ~ nationality ~ role =>
        val playerSortingKey = PlayerSortingKey(playerId, firstName, lastName, teamId, teamName,
          leagueUnitId, leagueUnitName, nationality, leagueId)
        PlayerSalaryTSI(playerSortingKey, age, tsi, salary, Roles.mapping(role))
    }
  }
}
