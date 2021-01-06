package chpp.teamdetails

import chpp.teamdetails.models.TeamDetails
import flows.AbstractHttpFlow

object TeamDetailsHttpFlow extends AbstractHttpFlow[TeamDetailsRequest, TeamDetails] {
  override def preprocessBody(body: String): String =
    body.replace("<?xml version=\"1.0\" encoding=\"utf-8\"?>", "")
}
