package flows

import chpp.matchdetails.MatchDetailsRequest
import chpp.matchdetails.models.MatchDetails

object MatchDetailsHttpFlow extends AbstractHttpFlow[MatchDetailsRequest, MatchDetails] {
  override def preprocessBody(body: String): String =
    body.replace("<?xml version=\"1.0\" encoding=\"utf-8\"?>", "")
}
