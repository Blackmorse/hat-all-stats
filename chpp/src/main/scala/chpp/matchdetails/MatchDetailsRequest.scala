package chpp.matchdetails

import chpp.AbstractRequest
import chpp.matchdetails.models.MatchDetails

case class MatchDetailsRequest(matchId: Option[Long] = None) extends AbstractRequest[MatchDetails]("matchdetails", "3.0",
  "matchID" -> matchId)
