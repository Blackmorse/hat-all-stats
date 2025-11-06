package databases.requests.model.team

import play.api.libs.json.{Json, OWrites}

case class TeamSortingKey(teamId: Long, teamName: String, leagueUnitId: Long,
                          leagueUnitName: String, leagueId: Int)

object TeamSortingKey {
  implicit val writes: OWrites[TeamSortingKey] = Json.writes[TeamSortingKey]
  implicit val jsonEncoder: zio.json.JsonEncoder[TeamSortingKey] = zio.json.DeriveJsonEncoder.gen[TeamSortingKey]
}