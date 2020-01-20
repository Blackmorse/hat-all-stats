package utils

object LeagueNameParser {
  def getLeagueUnitNumberByName(leagueUnitName: String) = {
    if(!leagueUnitName.contains('.')) 1
    else {
      leagueUnitName.split('.')(1).toInt
    }
  }
}
