package chpp.matchdetails

import org.apache.pekko.http.scaladsl.model.HttpRequest
import chpp.matchdetails.models.MatchDetails
import chpp.{AbstractRequest, OauthTokens, RequestCreator}

case class MatchDetailsRequest(matchId: Option[Long] = None) extends AbstractRequest[MatchDetails] {
  override def createRequest()(implicit oauthTokens: OauthTokens): HttpRequest = {
    val map = RequestCreator.params("matchdetails", "3.0",
    "matchID" -> matchId)

    RequestCreator.create(map)
  }
}
