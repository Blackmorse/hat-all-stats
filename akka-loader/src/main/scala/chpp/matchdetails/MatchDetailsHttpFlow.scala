package chpp.matchdetails

import chpp.matchdetails.models.MatchDetails
import flows.AbstractHttpFlow

object MatchDetailsHttpFlow extends AbstractHttpFlow[MatchDetailsRequest, MatchDetails] {
  override def preprocessBody(body: String): String =
    body.replace("<?xml version=\"1.0\" encoding=\"utf-8\"?>", "")
}
