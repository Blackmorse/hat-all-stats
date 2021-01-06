package chpp.leaguedetails

import chpp.leaguedetails.models.LeagueDetails
import flows.AbstractHttpFlow

object LeagueDetailsHttpFlow extends AbstractHttpFlow[LeagueDetailsRequest, LeagueDetails]{
  override def preprocessBody(body: String): String =
    body.replace("<?xml version=\"1.0\" encoding=\"utf-8\"?>", "")
}
