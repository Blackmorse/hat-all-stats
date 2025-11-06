package databases.requests.model.player

import play.api.libs.json.{Json, OWrites}
import zio.json.{DeriveJsonEncoder, JsonEncoder}

case class PlayerSortingKey(playerId: Long,
                            firstName: String,
                            lastName:String,
                            teamId: Long,
                            teamName: String,
                            leagueUnitId: Long,
                            leagueUnitName: String,
                            nationality: Int,
                            leagueId: Int)

object PlayerSortingKey {
  implicit val writes: OWrites[PlayerSortingKey] = Json.writes[PlayerSortingKey]
  implicit val jsonEncoder: JsonEncoder[PlayerSortingKey] = DeriveJsonEncoder.gen[PlayerSortingKey]
}
