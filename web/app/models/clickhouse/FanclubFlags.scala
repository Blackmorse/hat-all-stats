package models.clickhouse

import anorm.SqlParser.get
import anorm.~

case class FanclubFlags(teamId: Long, teamName: String, leagueUnitId: Long, leagueUnitName: String,
                        fanclubSize: Int, homeFlags: Int, awayFlags: Int, allFlags: Int)

object FanclubFlags {
  val fanclubFlagsMapper = {
    get[Long]("team_id") ~
    get[String]("team_name") ~
    get[Long]("league_unit_id") ~
    get[String]("league_unit_name") ~
    get[Int]("fanclub_size") ~
    get[Int]("home_flags") ~
    get[Int]("away_flags") ~
    get[Int]("all_flags") map {
      case teamId ~ teamName ~ leagueUnitId ~ leagueUnitName ~ fanclubSize ~ homeFlags ~ awayFlags ~ allFlags =>
        FanclubFlags(teamId, teamName, leagueUnitId, leagueUnitName, fanclubSize, homeFlags, awayFlags, allFlags)
    }
  }
}
