package models.clickhouse.league

import anorm.SqlParser.get
import anorm.~

case class TeamRating(teamId: Long, teamName: String, league_unit_id: Long, leagueUnitName: String, hatStats: Int)

object TeamRating {
  val teamRatingMapper = {
    get[Long]("team_id") ~
      get[String]("team_name") ~
      get[Long]("league_unit_id") ~
      get[String]("league_unit_name") ~
      get[Int]("hatstats") map {
      case teamId ~ teamName ~ leagueUnitId ~ leagueUnitName ~ hatstats =>
        TeamRating(teamId, teamName, leagueUnitId, leagueUnitName, hatstats)
    }
  }
}