package chpp.players

import akka.http.scaladsl.model.HttpRequest
import chpp.{AbstractRequest, OauthTokens, RequestCreator}

case class PlayersRequest(actionType: Option[String] = None,
                          orderBy: Option[String] = None,
                          teamId: Option[Long] = None,
                          includeMatchInfo: Option[Boolean] = None) extends AbstractRequest {
  override def createRequest()(implicit oauthTokens: OauthTokens): HttpRequest = {
    val map = RequestCreator.params("players", "2.4",
    "actionType" -> actionType,
      "orderBy" -> orderBy,
      "teamID" -> teamId,
      "includeMatchInfo" -> includeMatchInfo)

    RequestCreator.create(map)
  }
}
