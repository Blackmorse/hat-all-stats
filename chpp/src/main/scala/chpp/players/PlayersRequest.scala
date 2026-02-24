package chpp.players

import chpp.AbstractRequest
import chpp.players.models.Players

case class PlayersRequest(actionType: Option[String] = None,
                          orderBy: Option[String] = None,
                          teamId: Option[Long] = None,
                          includeMatchInfo: Option[Boolean] = None) extends AbstractRequest[Players]("players", "2.4",
  "actionType" -> actionType,
  "orderBy" -> orderBy,
  "teamID" -> teamId,
  "includeMatchInfo" -> includeMatchInfo)
