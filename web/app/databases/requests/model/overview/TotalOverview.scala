package databases.requests.model.overview

import databases.requests.model.`match`.MatchTopHatstats
import play.api.libs.json.{Json, OWrites}

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
}
