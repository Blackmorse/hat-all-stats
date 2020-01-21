package models.clickhouse

import anorm.SqlParser.get
import anorm.~

case class LeagueUnitRating(leagueUnitId: Long, leagueUnitName: String, hatStats: Int, midfield: Int, defense: Int, attack: Int)

object LeagueUnitRating {
  val leagueUnitRatingMapper = {
    get[Long]("league_unit_id") ~
    get[String]("league_unit_name") ~
    get[Int]("hatstats") ~
    get[Int]("midfield") ~
    get[Int]("defense") ~
    get[Int]("attack") map {
      case leagueUnitId ~ leagueUnitName ~ hatstats ~ midfield ~ defense ~ attack =>
        LeagueUnitRating(leagueUnitId, leagueUnitName, hatstats, midfield, defense, attack)
    }
  }
}


