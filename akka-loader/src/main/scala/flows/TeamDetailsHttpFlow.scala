package flows

import chpp.teamdetails.TeamDetailsRequest
import chpp.teamdetails.models.TeamDetails

object TeamDetailsHttpFlow extends AbstractHttpFlow[TeamDetailsRequest, TeamDetails] {
  override def preprocessBody(body: String): String =
    body.replace("<?xml version=\"1.0\" encoding=\"utf-8\"?>", "")
}
