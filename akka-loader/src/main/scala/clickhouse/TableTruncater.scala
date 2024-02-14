package clickhouse

import chpp.commonmodels.MatchType
import chpp.worlddetails.models.League
import utils.realRound

object TableTruncater {
  def sql(league: League, matchType: MatchType.Value, table: String, database: String): String = {
    val season = league.season - league.seasonOffset
    val round = realRound(matchType, league)
    s"ALTER TABLE $database.$table DELETE WHERE (season = $season) AND (round = $round)"
  }
}
