package databases.requests.overview

import anorm.RowParser
import databases.requests.matchdetails.MatchSurprisingRequest
import databases.requests.ClickhouseOverviewRequest
import databases.requests.model.`match`.MatchTopHatstats

object SurprisingMatchesOverviewRequest extends ClickhouseOverviewRequest[MatchTopHatstats] {
  override val sql: String  = MatchSurprisingRequest.oneRoundSql

  override val rowParser: RowParser[MatchTopHatstats] = MatchTopHatstats.mapper

  override def sortBy: String = "abs_hatstats_difference"
}
