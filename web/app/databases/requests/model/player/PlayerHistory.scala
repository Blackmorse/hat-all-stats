package databases.requests.model.player

import chpp.commonmodels.MatchType
import play.api.libs.json.{Json, OWrites}
import anorm.SqlParser.get
import anorm.{RowParser, ~}
import databases.requests.model.Roles

case class PlayerHistory(playerId: Long,
                         firstName: String,
                         lastName: String,
                         age: Int,
                         tsi: Int,
                         rating: Int,
                         ratingEndOfMatch: Int,
                         matchType: MatchType.Value,
                         role: String,
                         playedMinutes: Int,
                         injuryLevel: Int,
                         salary: Int,
                         yellowCards: Int,
                         redCards: Int
                        )

object PlayerHistory {
  implicit val writes: OWrites[PlayerHistory] = Json.writes[PlayerHistory]

  val mapper: RowParser[PlayerHistory] = {
    get[Long]("player_id") ~
    get[String]("first_name") ~
    get[String]("last_name") ~
    get[Int]("age") ~
    get[Int]("tsi") ~
    get[Int]("rating") ~
    get[Int]("rating_end_of_match") ~
    get[Int]("cup_level") ~
    get[Int]("role") ~
    get[Int]("played_minutes") ~
    get[Int]("injury_level") ~
    get[Int]("salary") ~
    get[Int]("yellow_cards") ~
    get[Int]("red_cards") map {
      case playerId ~ firstName ~ lastName ~ age ~ tsi ~ rating ~ ratingEndOfMatch ~
        cup_level ~ roleId ~ playedMinutes ~ injuryLevel ~ salary ~ yellowCards ~ redCArds =>
        PlayerHistory(
          playerId = playerId,
          firstName = firstName,
          lastName = lastName,
          age = age,
          tsi = tsi,
          rating = rating,
          ratingEndOfMatch = ratingEndOfMatch,
          matchType = if (cup_level == 0) MatchType.LEAGUE_MATCH else MatchType.CUP_MATCH,
          role = Roles.mapping(roleId),
          playedMinutes = playedMinutes,
          injuryLevel = injuryLevel,
          salary = salary,
          yellowCards = yellowCards,
          redCards = redCArds
        )
    }
  }
}