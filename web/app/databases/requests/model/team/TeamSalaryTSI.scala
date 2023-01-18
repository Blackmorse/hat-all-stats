package databases.requests.model.team

import play.api.libs.json.{Json, OWrites}
import anorm.SqlParser.get
import anorm.{RowParser, ~}

case class TeamSalaryTSI(teamSortingKey: TeamSortingKey,
                         tsi: Long,
                         salary: Long,
                         playersCount: Int,
                         avgSalary: Long,
                         avgTsi: Long,
                         salaryPerTsi: Double)

object TeamSalaryTSI {
  implicit val writes: OWrites[TeamSalaryTSI] = Json.writes[TeamSalaryTSI]

  val mapper: RowParser[TeamSalaryTSI] = {
    get[Int]("league") ~
    get[Long]("team_id") ~
    get[String]("team_name") ~
    get[Long]("league_unit_id") ~
    get[String]("league_unit_name") ~
    get[Long]("team_tsi") ~
    get[Long]("sum_salary") ~
    get[Int]("players_count") ~
    get[Long]("avg_salary") ~
    get[Long]("avg_tsi") ~
    get[Double]("salary_per_tsi") map {
      case leagueId ~ teamId ~ teamName ~ leagueUnitId ~ leagueUnitName ~
        tsi ~ salary ~ playersCount ~ avgSalary ~ avgTsi ~ salaryPerTsi =>
        val teamSortingKey = TeamSortingKey(
          teamId = teamId,
          teamName = teamName,
          leagueUnitId = leagueUnitId,
          leagueUnitName = leagueUnitName,
          leagueId = leagueId)

        TeamSalaryTSI(teamSortingKey = teamSortingKey,
          tsi = tsi,
          salary = salary,
          playersCount = playersCount,
          avgSalary = avgSalary,
          avgTsi = avgTsi,
          salaryPerTsi = salaryPerTsi)
    }
  }
}