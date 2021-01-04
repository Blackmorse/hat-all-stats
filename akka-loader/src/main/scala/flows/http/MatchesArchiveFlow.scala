package flows.http

import flows.AbstractHttpFlow
import models.chpp.matchesarchive.MatchesArchive
import requests.MatchesArchiveRequest

object MatchesArchiveFlow extends AbstractHttpFlow[MatchesArchiveRequest, MatchesArchive] {
  override def preprocessBody(body: String): String =
    body.replace("<?xml version=\"1.0\" encoding=\"utf-8\"?>", "")

}
