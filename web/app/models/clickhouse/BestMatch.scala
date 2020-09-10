package models.clickhouse

import anorm.SqlParser.get
import anorm.~

case class BestMatch(leagueUnitId: Long, leagueUnitName: String, teamId: Long, teamName: String,
                     oppositeTeamId: Long, oppositeTeamName: String, isHomeMatch: Boolean, matchId: Long,
                     goals: Int, enemyGoals: Int, soldTotal: Int, hatstats: Int, oppositeHatstats: Int)

object BestMatch {
  val bestMatchMapper = {
    get[Long]("league_unit_id") ~
    get[String]("league_unit_name") ~
    get[Long]("team_id") ~
    get[String]("team_name") ~
    get[Long]("opposite_team_id") ~
    get[String]("opposite_team_name") ~
    get[String]("is_home_match") ~
    get[Long]("match_id") ~
    get[Int]("goals") ~
    get[Int]("enemy_goals") ~
    get[Int]("sold_total") ~
    get[Int]("hatstats") ~
    get[Int]("opposite_hatstats") map {
      case leagueUnitId ~ leagueUnitName ~ teamId ~ teamName ~ oppositeTeamId ~ oppositeTeamName ~
        isHomeMatch ~ matchId ~ goals ~ enemyGoals ~ soldTotal ~ hatstats ~ oppositeHatstats =>
        BestMatch(leagueUnitId, leagueUnitName, teamId, teamName, oppositeTeamId, oppositeTeamName,
          if(isHomeMatch == "home") true else false, matchId, goals, enemyGoals, soldTotal, hatstats,
          oppositeHatstats)
    }
  }
}


