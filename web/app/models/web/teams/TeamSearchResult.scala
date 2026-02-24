package models.web.teams

import play.api.libs.json.{Json, OWrites}
import zio.json._

case class TeamSearchResult(teamId: Long, teamName: String)

object TeamSearchResult {
  implicit val writes: OWrites[TeamSearchResult] = Json.writes[TeamSearchResult]
  implicit val encoder: JsonEncoder[TeamSearchResult] = DeriveJsonEncoder.gen[TeamSearchResult]
}
