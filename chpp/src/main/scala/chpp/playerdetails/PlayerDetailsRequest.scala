package chpp.playerdetails

import chpp.AbstractRequest
import chpp.playerdetails.models.PlayerDetails

case class PlayerDetailsRequest(actionType: String = "view",
                                playerId: Long,
                                includeMatchInfo: Option[Boolean] = None,
                               ) extends AbstractRequest[PlayerDetails]("playerdetails", "2.9",
  "actionType" -> Some(actionType),
  "playerID" -> Some(playerId),
  "includeMatchInfo" -> includeMatchInfo
)
