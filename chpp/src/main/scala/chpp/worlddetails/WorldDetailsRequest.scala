package chpp.worlddetails

import chpp.AbstractRequest
import chpp.worlddetails.models.WorldDetails

case class WorldDetailsRequest(leagueId: Option[Int] = None,
                               countryId: Option[Int] = None,
                               includeRegions: Option[Boolean] = None) extends AbstractRequest[WorldDetails]("worlddetails", "1.9",
  "leagueID" -> leagueId,
  "countryID" -> countryId,
  "includeRegions" -> includeRegions) {

  override def preprocessResponseBody(body: String): String =
    body.replace("<Country Available=False />", "")
      .replace("Available=True", "Available=\"True\"")
      .replace("<?xml version=\"1.0\" encoding=\"utf-8\"?>", "")
}
