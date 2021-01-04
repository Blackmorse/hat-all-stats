package requests
import akka.http.scaladsl.model.HttpRequest
import models.{OauthTokens, RequestCreator}

case class MatchDetailsRequest(matchId: Option[Long] = None) extends AbstractRequest {
  override def createRequest()(implicit oauthTokens: OauthTokens): HttpRequest = {
    val map = RequestCreator.params("matchdetails", "3.0",
    "matchID" -> matchId)

    RequestCreator.create(map)
  }
}
