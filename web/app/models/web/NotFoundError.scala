package models.web

import play.api.libs.json.{Json, OWrites}

case class NotFoundError(entityType: String,
                         entityId: String,
                         description: String)

object NotFoundError {
  implicit val writes: OWrites[NotFoundError] = Json.writes[NotFoundError]

  val PLAYER: String = "PLAYER"
  val TEAM: String = "TEAM"
  val LEAGUE: String = "COUNTRY"
  val DIVISION_LEVEL: String = "DIVISION_LEVEL"
  val LEAGUE_UNIT: String = "LEAGUE"
}
