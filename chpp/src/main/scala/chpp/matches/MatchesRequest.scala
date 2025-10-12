package chpp.matches

import chpp.AbstractRequest
import chpp.matches.models.Matches

import java.util.Date

case class MatchesRequest(teamId: Option[Long] = None,
                          isYouth: Option[Boolean] = None,
                          lastMatchDate: Option[Date] = None) extends AbstractRequest[Matches]("matches", "2.8",
  "TeamID" -> teamId,
  "isYouth" -> isYouth,
  "LastMatchDate" -> lastMatchDate)
