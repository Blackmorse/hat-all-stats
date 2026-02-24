package com.blackmorse.hattid.web.utils

object LeagueNameParser {
  def getLeagueUnitNumberByName(leagueUnitName: String) = {
    if(!leagueUnitName.contains('.') || !(leagueUnitName.split("\\.")(1).forall(_.isDigit))) ("I", 1)
    else {
      (leagueUnitName.split('.')(0), leagueUnitName.split('.')(1).toInt)
    }
  }
}
