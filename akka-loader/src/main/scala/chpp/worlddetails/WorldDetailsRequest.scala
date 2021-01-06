package chpp.worlddetails

import akka.http.scaladsl.model.HttpRequest
import chpp.{AbstractRequest, OauthTokens, RequestCreator}

case class WorldDetailsRequest(leagueId: Option[Int] = None,
                               countryId: Option[Int] = None,
                               includeRegions: Option[Boolean] = None) extends AbstractRequest {

  override def createRequest()(implicit oauthTokens: OauthTokens): HttpRequest = {
    val map = RequestCreator.params("worlddetails", "1.8",
      "leagueID" -> leagueId,
      "countryID" -> countryId,
      "includeRegions" -> includeRegions)

    RequestCreator.create(map)
  }
}
