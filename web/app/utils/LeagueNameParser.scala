package utils

object LeagueNameParser {
  def getLeagueUnitNumberByName(leagueUnitName: String) = {
    if(!leagueUnitName.contains('.')) ("I", 1)
    else {
      (leagueUnitName.split('.')(0), leagueUnitName.split('.')(1).toInt)
    }
  }
}
