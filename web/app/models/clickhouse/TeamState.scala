package models.clickhouse

import anorm.SqlParser.get
import anorm.~

case class TeamState(teamName: String, teamId: Long, leagueUnitId: Long, leagueUnitName: String, tsi: Int, salary: Int, rating: Int, ratingEndOfMatch: Int,
                     age: Int, injury: Int, injuryCount: Int)

object TeamState {
  val teamStateMapper = {
    get[String]("team_name") ~
    get[Long]("team_id") ~
    get[Long]("league_unit_id") ~
    get[String]("league_unit_name") ~
    get[Int]("tsi") ~
    get[Int]("salary") ~
    get[Int]("rating") ~
    get[Int]("rating_end_of_match") ~
    get[Int]("age") ~
    get[Int]("injury") ~
    get[Int]("injury_count") map {
      case teamName ~ teamId ~ leagueUnitId ~ leagueUnitName ~ tsi ~ salary ~ rating ~ ratingEndOfMatch ~ age ~ injury ~ injuryLevel =>
        TeamState(teamName, teamId, leagueUnitId, leagueUnitName, tsi, salary, rating, ratingEndOfMatch, age, injury, injuryLevel)
    }
  }
}