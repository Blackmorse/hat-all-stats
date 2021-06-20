package flows

import chpp.leaguedetails.LeagueDetailsRequest
import chpp.leaguedetails.models.LeagueDetails

object LeagueDetailsHttpFlow extends AbstractHttpFlow[LeagueDetailsRequest, LeagueDetails] {
  override def preprocessBody(body: String): String =
    body.replace("<?xml version=\"1.0\" encoding=\"utf-8\"?>", "")
}
