package flows

import chpp.matchesarchive.MatchesArchiveRequest
import chpp.matchesarchive.models.MatchesArchive

object MatchesArchiveHttpFlow extends AbstractHttpFlow[MatchesArchiveRequest, MatchesArchive] {
  override def preprocessBody(body: String): String =
    body.replace("<?xml version=\"1.0\" encoding=\"utf-8\"?>", "")

}
