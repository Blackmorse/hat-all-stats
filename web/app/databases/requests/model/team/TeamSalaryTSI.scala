package databases.requests.model.team

import play.api.libs.json.Json
import anorm.SqlParser.get
import anorm.~

case class TeamSalaryTSI(teamSortingKey: TeamSortingKey, tsi: Long, salary: Long)

object TeamSalaryTSI {
  implicit val writes = Json.writes[TeamSalaryTSI]

  val mapper = {
    get[Long]("team_id") ~
    get[String]("team_name") ~
    get[Long]("league_unit_id") ~
    get[String]("league_unit_name") ~
    get[Long]("tsi") ~
    get[Long]("salary") map {
      case teamId ~ teamName ~ leagueUnitId ~ leagueUnitName ~
        tsi ~ salary =>
        val teamSortingKey = TeamSortingKey(teamId, teamName, leagueUnitId, leagueUnitName)
        TeamSalaryTSI(teamSortingKey, tsi, salary)
    }
  }
}