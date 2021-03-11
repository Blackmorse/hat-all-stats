package databases.requests.model.overview

import anorm.SqlParser.get
import anorm.~

case class PlayerOverviewModel(season: Int, round: Int, leagueId: Int, leagueUnitId: Int, leagueUnitName: String,
           teamId: Long, teamName: String, playerId: Long, firstName: String, lastName: String,
                               value: Int)

object PlayerOverviewModel {
  val mapper = {
    get[Int]("season") ~
    get[Int]("round") ~
    get[Int]("league_id") ~
    get[Int]("league_unit_id") ~
    get[String]("league_unit_name") ~
    get[Long]("team_id") ~
    get[String]("team_name") ~
    get[Long]("player_id") ~
    get[String]("first_name") ~
    get[String]("last_name") ~
    get[Int]("value") map {
      case season ~ round ~ leagueId ~ leagueUnitId ~ leagueUnitName ~ teamId ~ teamName ~ playerId ~ firstName ~ lastName ~ value =>
        PlayerOverviewModel(season, round, leagueId, leagueUnitId, leagueUnitName, teamId, teamName, playerId, firstName, lastName, value)
    }
  }
}
