package models.clickhouse

import anorm.SqlParser.get
import anorm.~

case class TeamRating(teamId: Long, teamName: String, leagueUnitId: Long, leagueUnitName: String,
                      hatStats: Int, midfield: Int, defense: Int, attack: Int)

object TeamRating {
  val teamRatingMapper = {
    get[Long]("team_id") ~
      get[String]("team_name") ~
      get[Long]("league_unit_id") ~
      get[String]("league_unit_name") ~
      get[Int]("hatstats") ~
      get[Int]("midfield") ~
      get[Int]("defense") ~
      get[Int]("attack") map {
      case teamId ~ teamName ~ leagueUnitId ~ leagueUnitName ~ hatstats ~ midfield ~ defense ~ attack =>
        TeamRating(teamId, teamName, leagueUnitId, leagueUnitName, hatstats, midfield, defense, attack)
    }
  }
}