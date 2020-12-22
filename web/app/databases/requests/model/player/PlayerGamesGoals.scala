package databases.requests.model.player

import anorm.SqlParser.get
import anorm.~
import play.api.libs.json.Json

case class PlayerGamesGoals(playerSortingKey: PlayerSortingKey,
                            games: Int,
                            playedMinutes: Int,
                            scored: Int,
                            goalRate: Double,
                            role: String,
                            age: Int)

object PlayerGamesGoals {
  implicit val writes = Json.writes[PlayerGamesGoals]

  val mapper = {
    get[Long]("player_id") ~
    get[String]("first_name") ~
    get[String]("last_name") ~
    get[Long]("team_id") ~
    get[String]("team_name") ~
    get[Long]("league_unit_id") ~
    get[String]("league_unit_name") ~
    get[Int]("games") ~
    get[Int]("played") ~
    get[Int]("scored") ~
    get[Double]("goal_rate") ~
    get[Int]("nationality") ~
    get[String]("role") ~
    get[Int]("age") map {
      case playerId ~ firstName ~ lastName ~ teamId ~ teamName ~
        leagueUnitId ~ leagueUnitName ~ games ~ playedMinutes ~
        scored ~ goalRate ~ nationality ~ role ~ age =>
        val playerSortingKey = PlayerSortingKey(playerId, firstName, lastName, teamId, teamName,
          leagueUnitId, leagueUnitName, nationality)
        PlayerGamesGoals(
          playerSortingKey,
          games,
          playedMinutes,
          scored,
          if(goalRate.isNaN || goalRate.isInfinite) null.asInstanceOf[Double] else goalRate,
          role,
          age)
    }
  }
}


