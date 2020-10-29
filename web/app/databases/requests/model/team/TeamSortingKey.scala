package databases.requests.model.team

import play.api.libs.json.Json

case class TeamSortingKey(teamId: Long, teamName: String, leagueUnitId: Long,
                          leagueUnitName: String)

object TeamSortingKey {
  implicit val writes = Json.writes[TeamSortingKey]
}