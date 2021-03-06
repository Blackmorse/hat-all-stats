package databases.requests.overview.model

import databases.requests.model.`match`.MatchTopHatstats
import play.api.libs.json.Json

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
  implicit val writes = Json.writes[TotalOverview]
}
