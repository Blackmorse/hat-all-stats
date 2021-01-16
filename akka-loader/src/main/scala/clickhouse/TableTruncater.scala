package clickhouse

import chpp.worlddetails.models.League

object TableTruncater {
  def sql(league: League, table: String, database: String): String = {
    val season = league.season - league.seasonOffset
    val round = league.matchRound - 1
    s"ALTER TABLE $database.$table DELETE WHERE (season = $season) AND (round = $round)"
  }
}
