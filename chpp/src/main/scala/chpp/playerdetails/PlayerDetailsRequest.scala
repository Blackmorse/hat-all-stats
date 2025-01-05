package chpp.playerdetails

import org.apache.pekko.http.scaladsl.model.HttpRequest
import chpp.{AbstractRequest, OauthTokens, RequestCreator}
import chpp.playerdetails.models.PlayerDetails

case class PlayerDetailsRequest(actionType: String = "view",
                                playerId: Long,
                                includeMatchInfo: Option[Boolean] = None,
                               ) extends AbstractRequest[PlayerDetails] {
  override def createRequest()(implicit oauthTokens: OauthTokens): HttpRequest = {
    val map = RequestCreator.params("playerdetails", "2.9",
      "actionType" -> Some(actionType),
      "playerID" -> Some(playerId),
      "includeMatchInfo" -> includeMatchInfo
    )

    RequestCreator.create(map)
  }
}
