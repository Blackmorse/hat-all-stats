package chpp.matchesarchive

import chpp.matchesarchive.models.MatchesArchive
import flows.AbstractHttpFlow

object MatchesArchiveHttpFlow extends AbstractHttpFlow[MatchesArchiveRequest, MatchesArchive] {
  override def preprocessBody(body: String): String =
    body.replace("<?xml version=\"1.0\" encoding=\"utf-8\"?>", "")

}
