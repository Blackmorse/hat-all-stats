package databases.requests.overview

import anorm.RowParser
import databases.requests.ClickhouseOverviewRequest
import databases.requests.matchdetails.MatchTopHatstatsRequest
import databases.requests.model.`match`.MatchTopHatstats

object TopMatchesOverviewRequest extends ClickhouseOverviewRequest[MatchTopHatstats] {
  override def sortBy: String = "sum_hatstats"

  override val sql: String = MatchTopHatstatsRequest.oneRoundSql

  override val rowParser: RowParser[MatchTopHatstats] = MatchTopHatstats.mapper
}
