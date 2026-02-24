package databases.requests.model.overview

import databases.requests.model.`match`.MatchTopHatstats
import play.api.libs.json.{Json, OWrites}
import zio.json.{DeriveJsonEncoder, JsonEncoder}

case class TotalOverview(numberOverview: NumberOverview,
                         formations: List[FormationsOverview],
                         averageOverview: AveragesOverview,
                         surprisingMatches: List[MatchTopHatstats],
                         topHatstatsTeams: List[TeamStatOverview],
                         topSalaryTeams: List[TeamStatOverview],
                         topMatches: List[MatchTopHatstats],
                         topSalaryPlayers: List[PlayerStatOverview],
                         topRatingPlayers: List[PlayerStatOverview],
                         topMatchAttendance: List[MatchAttendanceOverview],
                         topTeamVictories: List[TeamStatOverview],
                         topSeasonScorers: List[PlayerStatOverview])
object TotalOverview {
  implicit val writes: OWrites[TotalOverview] = Json.writes[TotalOverview]
  implicit val jsonEncoder: JsonEncoder[TotalOverview] = DeriveJsonEncoder.gen[TotalOverview]

  def empty(): TotalOverview =
    TotalOverview(
      numberOverview = NumberOverview(
        numberOfTeams = 0,
        numberOfPlayers = 0,
        injuried = 0,
        goals = 0,
        yellowCards = 0,
        redCards = 0,
        numberOfNewTeams = 0
      ),
      formations = List(),
      averageOverview = AveragesOverview(
        matchAverages = OverviewMatchAverages(
          hatstats = 0,
          spectators = 0,
          goals = 0
        ),
        teamPlayerAverages = OverviewTeamPlayerAverages(
          age = 0,
          salary = 0,
          rating = 0
        )
      ),
      surprisingMatches = List(),
      topHatstatsTeams = List(),
      topSalaryTeams = List(),
      topMatches = List(),
      topSalaryPlayers = List(),
      topRatingPlayers = List(),
      topMatchAttendance = List(),
      topTeamVictories = List(),
      topSeasonScorers = List()
    )
}
