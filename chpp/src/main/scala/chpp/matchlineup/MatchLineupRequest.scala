package chpp.matchlineup

import chpp.AbstractRequest
import chpp.matchlineup.models.MatchLineup

case class MatchLineupRequest(matchId: Option[Long] = None,
                              teamId: Option[Long] = None,
                              sourceSystem: Option[String] = None) extends AbstractRequest[MatchLineup]("matchlineup", "2.1",
  "matchID" -> matchId,
  "teamID" -> teamId,
  "sourceSystem" -> sourceSystem) 
