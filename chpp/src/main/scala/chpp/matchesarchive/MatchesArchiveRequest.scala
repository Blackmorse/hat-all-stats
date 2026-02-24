package chpp.matchesarchive

import chpp.AbstractRequest
import chpp.matchesarchive.models.MatchesArchive

import java.util.Date

case class MatchesArchiveRequest(teamId: Option[Long] = None,
                                 isYouth: Option[Boolean] = None,
                                 firstMatchDate: Option[Date] = None,
                                 lastMatchDate: Option[Date] = None,
                                 season: Option[Int] = None,
                                 includeHto: Option[Boolean] = None) extends AbstractRequest[MatchesArchive]("matchesarchive", "1.4",
  "teamID" -> teamId,
  "isYouth" -> isYouth,
  "FirstMatchDate" -> firstMatchDate,
  "LastMatchDate" -> lastMatchDate,
  "season" -> season,
  "includeHTO" -> includeHto
)
