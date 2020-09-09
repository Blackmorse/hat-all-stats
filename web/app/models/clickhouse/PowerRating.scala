package models.clickhouse

import anorm.SqlParser.get
import anorm.~

case class PowerRating(teamId: Long, teamName: String, leagueUnitId: Long, leagueUnitName: String,
                       powerRating: Int)

object PowerRating {
  val powerRatingMapper = {
      get[Long]("team_id") ~
      get[String]("team_name") ~
      get[Long]("league_unit_id") ~
      get[String]("league_unit_name") ~
      get[Int]("power_rating") map {
        case teamId ~ teamName ~ leagueUnitId ~ leagueUnitName ~ powerRating =>
          PowerRating(teamId, teamName, leagueUnitId, leagueUnitName, powerRating)
      }
  }
}
