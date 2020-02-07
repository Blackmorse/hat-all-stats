package models.clickhouse

import anorm.SqlParser.get
import anorm.~

case class FormalTeamStats(teamId: Long, teamName: String, leagueUnitId: Long, leagueUnitName: String,
                           scored: Int, missed: Int, wins: Int, draws: Int, lost: Int, points: Int)

object FormalTeamStats {
  val formalTeamStatsMapper = {
    get[Long]("team_id") ~
    get[String]("team_name") ~
    get[Long]("league_unit_id") ~
    get[String]("league_unit_name") ~
    get[Int]("scored") ~
    get[Int]("missed") ~
    get[Int]("wins") ~
    get[Int]("draws") ~
    get[Int]("loses") ~
    get[Int]("points") map {
      case teamId ~ teamName ~ leagueUnitId ~ leagueUnitName ~ scored ~ missed ~ wins ~ draws ~ lost ~ points =>
        FormalTeamStats(teamId, teamName, leagueUnitId, leagueUnitName, scored, missed, wins, draws, lost, points)
    }
  }
}
