package models.clickhouse

import anorm.SqlParser.get
import anorm.~
import play.api.libs.json.Json

case class PlayerStats(playerId: Long, firstName: String, lastName: String, teamId: Long, teamName: String, leagueUnitId: Long, leagueUnitName: String,
                  age: Int, games: Int, played: Int, scored: Int, yellowCards: Int, redCards: Int, totalInjuries: Int, goalRate: Double)

object PlayerStats {
  implicit val writes = Json.writes[PlayerStats]

  val playerStatsMapper = {
    get[Long]("player_id") ~
    get[String]("first_name") ~
    get[String]("last_name") ~
    get[Long]("team_id") ~
    get[String]("team_name") ~
    get[Long]("league_unit_id") ~
    get[String]("league_unit_name") ~
    get[Int]("age") ~
    get[Int]("games") ~
    get[Int]("played") ~
    get[Int]("scored") ~
    get[Int]("yellow_cards") ~
    get[Int]("red_cards") ~
    get[Int]("total_injuries") ~
    get[Double]("goal_rate") map {
      case playerId ~ firstName ~ lastName ~ teamId ~ teamName ~ leagueUnitId ~ leagueUnitName ~ age ~
        games ~ played ~ scored ~ yellowCards ~ redCards ~ totalInjuries ~ goalRate =>

        PlayerStats(playerId, firstName, lastName, teamId, teamName, leagueUnitId,
          leagueUnitName, age, games, played, scored, yellowCards, redCards, totalInjuries,
          if(goalRate.isNaN || goalRate.isInfinite) null.asInstanceOf[Double] else goalRate)
    }
  }
}
