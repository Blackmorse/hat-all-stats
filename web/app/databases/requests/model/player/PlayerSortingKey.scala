package databases.requests.model.player

import play.api.libs.json.Json

case class PlayerSortingKey(playerId: Long,
                            firstName: String,
                            lastName:String,
                            teamId: Long,
                            teamName: String,
                            leagueUnitId: Long,
                            leagueUnitName: String,
                            nationality: Int)

object PlayerSortingKey {
  implicit val writes = Json.writes[PlayerSortingKey]
}
