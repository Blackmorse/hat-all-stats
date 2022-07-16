package chpp.avatars

import akka.http.scaladsl.model.HttpRequest
import chpp.{AbstractRequest, OauthTokens, RequestCreator}
import chpp.avatars.models.AvatarContainer

case class AvatarRequest(actionType: String = "players",
                         teamId: Option[Int] = None) extends AbstractRequest[AvatarContainer] {

  override def createRequest()(implicit oauthTokens: OauthTokens): HttpRequest = {
    val map = RequestCreator.params("avatars", "1.1",
    "actionType" -> Some(actionType),
      "teamId" -> teamId
    )

    RequestCreator.create(map)
  }
}
