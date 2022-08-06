package models.web.teams

import play.api.libs.json.{Json, OWrites}

case class TeamSearchResult(teamId: Long, teamName: String)

object TeamSearchResult {
  implicit val writes: OWrites[TeamSearchResult] = Json.writes[TeamSearchResult]
}
