package chpp.leaguefixtures

import chpp.AbstractRequest
import chpp.leaguefixtures.models.LeagueFixtures

case class LeagueFixturesRequest(leagueLevelUnitId: Option[Int] = None,
                                 season: Option[Int] = None) extends AbstractRequest[LeagueFixtures]("leaguefixtures", "1.2",
  "leagueLevelUnitID" -> leagueLevelUnitId,
  "season" -> season)
