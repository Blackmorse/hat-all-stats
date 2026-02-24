package com.blackmorse.hattid.web.models.web.analyzer

import com.blackmorse.hattid.web.models.clickhouse.NearestMatch
import com.blackmorse.hattid.web.models.web.analyzer.MatchOpponentCombinedInfo.Team
import com.blackmorse.hattid.web.models.web.matches.SingleMatch
import zio.json.{DeriveJsonEncoder, JsonEncoder}



case class MatchOpponentCombinedInfo(
                                      currentTeamPlayedMatches: Seq[NearestMatch],
                                      currentTeamNextOpponents: Seq[Team],
                                      opponentPlayedMatches: Seq[NearestMatch],
                                      simulatedMatch: Option[SingleMatch]) {

}

object MatchOpponentCombinedInfo {
  type Team = (Long, String)

  implicit val jsonEncoder: JsonEncoder[MatchOpponentCombinedInfo] = DeriveJsonEncoder.gen[MatchOpponentCombinedInfo]
}


