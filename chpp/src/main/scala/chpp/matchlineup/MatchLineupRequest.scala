package chpp.matchlineup

import akka.http.scaladsl.model.HttpRequest
import chpp.matchlineup.models.{MatchLineup, Team}
import chpp.{AbstractRequest, OauthTokens, RequestCreator}

case class MatchLineupRequest(matchId: Option[Long] = None,
                              teamId: Option[Long] = None,
                              sourceSystem: Option[String] = None) extends AbstractRequest[MatchLineup] {
  override def createRequest()(implicit oauthTokens: OauthTokens): HttpRequest = {
    val map = RequestCreator.params("matchlineup", "2.1",
      "matchID" -> matchId,
    "teamID" -> teamId,
    "sourceSystem" -> sourceSystem)

    RequestCreator.create(map)
  }
}
