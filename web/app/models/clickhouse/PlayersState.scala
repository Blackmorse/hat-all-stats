package models.clickhouse

import anorm.SqlParser.get
import anorm.~

case class PlayersState(teamName: String, teamId: Long, leagueUnitName: String, leagueUnitId: Long, playerId: Long,
                        firstName: String, lastName: String, age: Double, tsi: Int, salary: Int,
                        rating: Int, ratingEndOfMatch: Int, injuryLevel: Int, redCards: Int, yellowCards: Int)

object PlayersState {
  val playersStateMapper = {
    get[String]("team_name") ~
    get[Long]("team_id") ~
    get[String]("league_unit_name") ~
    get[Long]("league_unit_id") ~
    get[Long]("player_id") ~
    get[String]("first_name") ~
    get[String]("last_name") ~
    get[Double]("age") ~
    get[Int]("tsi") ~
    get[Int]("salary") ~
    get[Int]("rating") ~
    get[Int]("rating_end_of_match") ~
    get[Int]("injury_level") ~
    get[Int]("red_cards") ~
    get[Int]("yellow_cards") map {
      case teamName ~ teamId ~ leagueUnitName ~ leagueUnitId ~ playerId ~ firstName ~ lastName ~
        age ~ tsi ~ salary ~ rating ~ ratingEndOfMatch ~ injuryLevel ~ redCards ~ yellowCards =>
       PlayersState(teamName, teamId, leagueUnitName, leagueUnitId, playerId, firstName, lastName,
         age, tsi, salary, rating, ratingEndOfMatch, injuryLevel, redCards, yellowCards)
    }

  }
}
