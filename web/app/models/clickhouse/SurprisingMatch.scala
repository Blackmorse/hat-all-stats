package models.clickhouse

import anorm.SqlParser.get
import anorm.~

case class SurprisingMatch(leagueUnitId: Long, leagueUnitName: String, teamId: Long, teamName: String,
               oppositeTeamId: Long, oppositeTeamName: String, matchId: Long, isHomeMatch: Boolean,
               goals: Int, enemyGoals: Int, hatstats: Int, oppositeHatstats: Int)

object SurprisingMatch {
  val surprisingMatchMapper = {
    get[Long]("league_unit_id") ~
    get[String]("league_unit_name") ~
    get[Long]("team_id") ~
    get[String]("team_name") ~
    get[Long]("opposite_team_id") ~
    get[String]("opposite_team_name") ~
    get[Long]("match_id") ~
    get[String]("is_home_match") ~
    get[Int]("goals") ~
    get[Int]("enemy_goals") ~
    get[Int]("hatstats") ~
    get[Int]("opposite_hatstats") map {
      case leagueUnitId ~ leagueUnitName ~ teamId ~ teamName ~ oppositeTeamId ~ oppositeTeamName
        ~ matchId ~ isHomeMatch ~ goals ~ enemyGoals ~ hatstats ~ oppositeHatstats =>
        SurprisingMatch(leagueUnitId, leagueUnitName, teamId, teamName, oppositeTeamId, oppositeTeamName,
          matchId, if(isHomeMatch == "home") true else false, goals, enemyGoals, hatstats, oppositeHatstats)
    }
  }
}