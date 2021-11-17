package databases.requests.overview

import anorm.RowParser
import databases.requests.ClickhouseOverviewRequest
import databases.requests.model.overview.OverviewMatchAverages
import databases.sqlbuilder.{Select, SqlBuilder}

object OverviewMatchAveragesRequest extends ClickhouseOverviewRequest[OverviewMatchAverages] {
  override val rowParser: RowParser[OverviewMatchAverages] = OverviewMatchAverages.mapper

  override def builder(season: Int,
                       round: Int,
                       leagueId: Option[Int],
                       divisionLevel: Option[Int]): SqlBuilder = {
    import SqlBuilder.fields._
    import SqlBuilder.implicits._
    Select(
        "avgIf(sold_total, is_home_match = 'home')".toInt32 as "avg_sold_total",
        s"toUInt16(avg($hatstats))" as "avg_hatstats",
        "avg(goals)" as "avg_goals"
      )
      .from("hattrick.match_details")
      .where
        .round(round)
        .season(season)
        .leagueId(leagueId)
        .divisionLevel(divisionLevel)
        .isLeagueMatch
  }
}
