package models.clickhouse.overview

import anorm.SqlParser.get
import anorm.~

case class TeamOverviewModel(season: Int, round: Int, leagueId: Int, leagueUnitId: Int, leagueUnitName: String,
                             teamId: Long, teamName: String, value: Int)

object TeamOverviewModel {
  val mapper = {
    get[Int]("team_season") ~
    get[Int]("team_round") ~
    get[Int]("league_id") ~
    get[Int]("league_unit_id") ~
    get[String]("league_unit_name") ~
    get[Long]("team_id") ~
    get[String]("team_name") ~
    get[Int]("value") map {
      case season ~ round ~ leagueId ~ leagueUnitId ~ leagueUnitName ~ teamId ~ teamName ~ value =>
        TeamOverviewModel(season, round, leagueId, leagueUnitId, leagueUnitName, teamId, teamName, value)
    }
  }
}
