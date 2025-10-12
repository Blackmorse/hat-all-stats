package chpp.leaguedetails

import chpp.AbstractRequest
import chpp.leaguedetails.models.LeagueDetails

case class LeagueDetailsRequest(leagueUnitId: Option[Int]) extends AbstractRequest[LeagueDetails]("leaguedetails", "1.6",
  "leagueLevelUnitID" -> leagueUnitId)
