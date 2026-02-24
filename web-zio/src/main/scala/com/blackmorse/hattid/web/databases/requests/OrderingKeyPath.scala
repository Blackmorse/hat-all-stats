package com.blackmorse.hattid.web.databases.requests

case class OrderingKeyPath(season: Option[Int] = None,
                           leagueId: Option[Int] = None,
                           divisionLevel: Option[Int] = None,
                           leagueUnitId: Option[Long] = None,
                           teamId: Option[Long] = None
                          ) {
  def isLeagueUnitLevel: Boolean = leagueId.isDefined && 
    divisionLevel.isDefined && 
    leagueUnitId.isDefined && 
    teamId.isEmpty
}
