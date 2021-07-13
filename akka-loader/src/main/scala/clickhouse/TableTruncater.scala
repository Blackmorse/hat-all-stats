package clickhouse

import chpp.commonmodels.MatchType
import chpp.worlddetails.models.League

object TableTruncater {
  def sql(league: League, matchType: MatchType.Value, table: String, database: String): String = {
    val season = league.season - league.seasonOffset
    val round = if (matchType == MatchType.LEAGUE_MATCH) league.matchRound - 1 else if (matchType == MatchType.CUP_MATCH) league.matchRound
      else throw new IllegalArgumentException(matchType.toString)
    s"ALTER TABLE $database.$table DELETE WHERE (season = $season) AND (round = $round)"
  }
}
