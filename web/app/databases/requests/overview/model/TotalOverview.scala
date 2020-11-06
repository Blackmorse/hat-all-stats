package databases.requests.overview.model

import play.api.libs.json.Json

case class TotalOverview(numberOverview: NumberOverview,
                         formations: List[FormationsOverview],
                         averageOverview: AveragesOverview,
                         surprisingMatches: List[MatchTopHatstatsOverview],
                         topHatstatsTeams: List[TeamStatOverview],
                         topSalaryTeams: List[TeamStatOverview],
                         topMatches: List[MatchTopHatstatsOverview],
                         topSalaryPlayers: List[PlayerStatOverview],
                         topRatingPlayers: List[PlayerStatOverview])
object TotalOverview {
  implicit val writes = Json.writes[TotalOverview]
}
