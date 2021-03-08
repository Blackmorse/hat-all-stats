package databases.requests.model.team

import play.api.libs.json.Json
import anorm.SqlParser.get
import anorm.~

case class TeamSalaryTSI(teamSortingKey: TeamSortingKey,
                         tsi: Long,
                         salary: Long,
                         playersCount: Int,
                         avgSalary: Long,
                         avgTsi: Long)

object TeamSalaryTSI {
  implicit val writes = Json.writes[TeamSalaryTSI]

  val mapper = {
    get[Int]("league") ~
    get[Long]("team_id") ~
    get[String]("team_name") ~
    get[Long]("league_unit_id") ~
    get[String]("league_unit_name") ~
    get[Long]("tsi") ~
    get[Long]("salary") ~
    get[Int]("players_count") ~
    get[Long]("avg_salary") ~
    get[Long]("avg_tsi") map {
      case leagueId ~ teamId ~ teamName ~ leagueUnitId ~ leagueUnitName ~
        tsi ~ salary ~ playersCount ~ avgSalary ~ avgTsi =>
        val teamSortingKey = TeamSortingKey(teamId, teamName, leagueUnitId, leagueUnitName, leagueId)

        TeamSalaryTSI(teamSortingKey,
          tsi,
          salary,
          playersCount,
          avgSalary,
          avgTsi)
    }
  }
}