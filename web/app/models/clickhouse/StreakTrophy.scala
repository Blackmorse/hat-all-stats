package models.clickhouse

import anorm.SqlParser.get
import anorm.~

case class StreakTrophy(teamId: Long, teamName: String, leagueUnitId: Long, leagueUnitName: String,
                        trophies: Int, victories: Int, undefeated: Int)

object StreakTrophy {
  val streakTrophyMapper = {
      get[Long]("team_id") ~
      get[String]("team_name") ~
      get[Long]("league_unit_id") ~
      get[String]("league_unit_name") ~
      get[Int]("trophies_number") ~
      get[Int]("number_of_victories") ~
      get[Int]("number_of_undefeated") map {
        case teamId ~ teamName ~ leagueUnitId ~ leagueUnitName ~ trophies ~ victories ~ undefeated =>
          StreakTrophy(teamId, teamName, leagueUnitId, leagueUnitName, trophies, victories, undefeated)
      }
  }
}
