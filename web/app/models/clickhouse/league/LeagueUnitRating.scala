package models.clickhouse.league

import anorm.SqlParser.get
import anorm.~

case class LeagueUnitRating(leagueUnitId: Long, leagueUnitName: String, hatStats: Int)

object LeagueUnitRating {
  val leagueUnitRatingMapper = {
    get[Long]("league_unit_id") ~
    get[String]("league_unit_name") ~
    get[Int]("hatstats") map {
      case leagueUnitId ~ leagueUnitName ~ hatstats => LeagueUnitRating(leagueUnitId, leagueUnitName, hatstats)
    }
  }
}


