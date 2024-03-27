package chpp.teamdetails

import org.apache.pekko.http.scaladsl.model.HttpRequest
import chpp.teamdetails.models.TeamDetails
import chpp.{AbstractRequest, OauthTokens, RequestCreator}

case class TeamDetailsRequest(teamId: Option[Long] = None,
                              userId: Option[Int] = None,
                              includeDomesticFlags: Option[Boolean] = None,
                              includeFlags: Option[Boolean] = None,
                              includeSupporters: Option[Boolean] = None) extends AbstractRequest[TeamDetails] {
  override def createRequest()(implicit oauthTokens: OauthTokens): HttpRequest = {
    val map = RequestCreator.params("teamdetails", "3.4",
    "teamID" -> teamId,
    "userID" -> userId,
    "includeDomesticFlags" -> includeDomesticFlags,
    "includeFlags" -> includeFlags,
    "includeSupporters" -> includeSupporters,
    )

    RequestCreator.create(map)
  }
}
