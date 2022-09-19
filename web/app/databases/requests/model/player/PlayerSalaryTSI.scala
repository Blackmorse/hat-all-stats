package databases.requests.model.player

import play.api.libs.json.{Json, OWrites}
import anorm.SqlParser.get
import anorm.{RowParser, ~}
import databases.requests.model.Roles

case class PlayerSalaryTSI(sortingKey: PlayerSortingKey,
                           age: Int,
                           tsi: Int,
                           salary: Int,
                           role: String)

object PlayerSalaryTSI {
  implicit val writes: OWrites[PlayerSalaryTSI] = Json.writes[PlayerSalaryTSI]

  val mapper: RowParser[PlayerSalaryTSI] = {
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
        val playerSortingKey = PlayerSortingKey(playerId = playerId,
          firstName = firstName,
          lastName = lastName,
          teamId = teamId,
          teamName = teamName,
          leagueUnitId = leagueUnitId,
          leagueUnitName = leagueUnitName,
          nationality = nationality,
          leagueId = leagueId)

        PlayerSalaryTSI(sortingKey = playerSortingKey,
          age = age,
          tsi = tsi,
          salary = salary,
          role = Roles.mapping(role))
    }
  }
}
