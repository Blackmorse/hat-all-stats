package databases.requests.model.team

import play.api.libs.json.{Json, OWrites}
import anorm.SqlParser.get
import anorm.{RowParser, ~}
import databases.requests.model.Chart

case class TeamSalaryTSIChart(teamSortingKey: TeamSortingKey,
                          season: Int,
                          round: Int,
                          tsi: Long,
                          salary: Long,
                          playersCount: Int,
                          avgSalary: Long,
                          avgTsi: Long,
                          salaryPerTsi: Double) extends Chart

object TeamSalaryTSIChart {
  implicit val writes: OWrites[TeamSalaryTSIChart] = Json.writes[TeamSalaryTSIChart]

  val mapper: RowParser[TeamSalaryTSIChart] = {
    get[Int]("league") ~
      get[Long]("team_id") ~
      get[String]("team_name") ~
      get[Long]("league_unit_id") ~
      get[String]("league_unit_name") ~
      get[Long]("team_tsi") ~
      get[Int]("season") ~
      get[Int]("round") ~
      get[Long]("sum_salary") ~
      get[Int]("players_count") ~
      get[Long]("avg_salary") ~
      get[Long]("avg_tsi") ~
      get[Double]("salary_per_tsi") map {
      case leagueId ~ teamId ~ teamName ~ leagueUnitId ~ leagueUnitName ~
        tsi ~ season ~ round ~ salary ~ playersCount ~ avgSalary ~ avgTsi ~ salaryPerTsi =>
        val teamSortingKey = TeamSortingKey(
          teamId = teamId,
          teamName = teamName,
          leagueUnitId = leagueUnitId,
          leagueUnitName = leagueUnitName,
          leagueId = leagueId)

        TeamSalaryTSIChart(teamSortingKey = teamSortingKey,
          season = season,
          round = round,
          tsi = tsi,
          salary = salary,
          playersCount = playersCount,
          avgSalary = avgSalary,
          avgTsi = avgTsi,
          salaryPerTsi = salaryPerTsi)
    }
  }
}