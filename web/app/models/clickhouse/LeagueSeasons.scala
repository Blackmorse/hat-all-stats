package models.clickhouse

import anorm.SqlParser.get
import anorm.~

case class LeagueSeasons(leagueId: Int, season: Int)

object LeagueSeasons {
  val mapper = {
    get[Int]("league_id") ~
    get[Int]("season") map {
      case leagueId ~ season => LeagueSeasons(leagueId, season)
    }
  }
}