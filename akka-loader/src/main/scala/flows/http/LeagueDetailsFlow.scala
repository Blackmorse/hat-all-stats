package flows.http

import flows.AbstractHttpFlow
import models.chpp.leaguedetails.LeagueDetails
import requests.LeagueDetailsRequest

object LeagueDetailsFlow extends AbstractHttpFlow[LeagueDetailsRequest, LeagueDetails]{
  override def preprocessBody(body: String): String =
    body.replace("<?xml version=\"1.0\" encoding=\"utf-8\"?>", "")
}
