package databases.requests.model.player

import chpp.commonmodels.MatchType
import play.api.libs.json.{Json, OWrites}
import anorm.SqlParser.get
import anorm.{RowParser, ~}
import databases.requests.model.Roles

case class PlayerHistory(playerSortingKey: PlayerSortingKey,
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
                         redCards: Int,
                         goals: Int,
                         season: Int,
                         round: Int
                        )

object PlayerHistory {
  implicit val writes: OWrites[PlayerHistory] = Json.writes[PlayerHistory]

  val mapper: RowParser[PlayerHistory] = {
    get[Int]("league_id") ~
    get[Int]("league_unit_id") ~
    get[String]("league_unit_name") ~
    get[Long]("player_id") ~
    get[String]("first_name") ~
    get[String]("last_name") ~
    get[Long]("team_id") ~
    get[String]("team_name") ~
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
    get[Int]("red_cards") ~
    get[Int]("goals") ~
    get[Int]("season") ~
    get[Int]("round") ~
    get[Int]("nationality") map {
      case leagueId ~ leagueUnitId ~ leagueUnitName ~ playerId ~ firstName ~ lastName ~ teamId ~ teamName ~
        age ~ tsi ~ rating ~ ratingEndOfMatch ~ cup_level ~ roleId ~ playedMinutes ~
        injuryLevel ~ salary ~ yellowCards ~ redCards ~ goals ~ season ~ round ~ nationality =>
        PlayerHistory(
          playerSortingKey = PlayerSortingKey(
            playerId = playerId,
            firstName = firstName,
            lastName = lastName,
            teamId = teamId,
            teamName = teamName,
            leagueUnitId = leagueUnitId,
            leagueUnitName = leagueUnitName,
            nationality = nationality,
            leagueId = leagueId
          ),
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
          redCards = redCards,
          goals = goals,
          season = season,
          round = round
        )
    }
  }
}