package models.web.analyzer

import models.clickhouse.NearestMatch
import models.web.analyzer.MatchOpponentCombinedInfo.Team
import models.web.matches.SingleMatch
import play.api.libs.json.{Json, OWrites}
import zio.json.{DeriveJsonEncoder, JsonEncoder}



case class MatchOpponentCombinedInfo(
                                      currentTeamPlayedMatches: Seq[NearestMatch],
                                      currentTeamNextOpponents: Seq[Team],
                                      opponentPlayedMatches: Seq[NearestMatch],
                                      simulatedMatch: Option[SingleMatch]) {

}

object MatchOpponentCombinedInfo {
  type Team = (Long, String)

  implicit val writes: OWrites[MatchOpponentCombinedInfo] = Json.writes[MatchOpponentCombinedInfo]
  implicit val jsonEncoder: JsonEncoder[MatchOpponentCombinedInfo] = DeriveJsonEncoder.gen[MatchOpponentCombinedInfo]
}


