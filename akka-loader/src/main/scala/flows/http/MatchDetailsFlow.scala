package flows.http

import flows.AbstractHttpFlow
import models.chpp.matchdetails.MatchDetails
import requests.MatchDetailsRequest

object MatchDetailsFlow extends AbstractHttpFlow[MatchDetailsRequest, MatchDetails] {
  override def preprocessBody(body: String): String =
    body.replace("<?xml version=\"1.0\" encoding=\"utf-8\"?>", "")
}
