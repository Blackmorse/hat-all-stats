package chpp.worlddetails

import org.apache.pekko.http.scaladsl.model.HttpRequest
import chpp.worlddetails.models.WorldDetails
import chpp.{AbstractRequest, OauthTokens, RequestCreator}

case class WorldDetailsRequest(leagueId: Option[Int] = None,
                               countryId: Option[Int] = None,
                               includeRegions: Option[Boolean] = None) extends AbstractRequest[WorldDetails] {

  override def createRequest()(implicit oauthTokens: OauthTokens): HttpRequest = {
    val map = RequestCreator.params("worlddetails", "1.9",
      "leagueID" -> leagueId,
      "countryID" -> countryId,
      "includeRegions" -> includeRegions)

    RequestCreator.create(map)
  }

  override def preprocessResponseBody(body: String): String =
    body.replace("<Country Available=False />", "")
      .replace("Available=True", "Available=\"True\"")
      .replace("<?xml version=\"1.0\" encoding=\"utf-8\"?>", "")
}
