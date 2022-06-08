package databases.requests.overview

import anorm.RowParser
import databases.requests.ClickhouseOverviewRequest
import databases.requests.model.overview.TeamStatOverview
import databases.sql.Fields.hatstats
import sqlbuilder.{Select, SqlBuilder}

object TopHatstatsTeamOverviewRequest extends ClickhouseOverviewRequest[TeamStatOverview] {
  override val rowParser: RowParser[TeamStatOverview] = TeamStatOverview.mapper

  override def builder(season: Int,
                       round: Int,
                       leagueId: Option[Int],
                       divisionLevel: Option[Int]): SqlBuilder = {
    import SqlBuilder.implicits._
    Select(
        "league_id",
        "league_unit_id",
        "league_unit_name",
        "team_id",
        "team_name",
        hatstats as "value"
      )
      .from("hattrick.match_details")
      .where
        .round(round)
        .season(season)
        .leagueId(leagueId)
        .divisionLevel(divisionLevel)
        .isLeagueMatch
      .orderBy("value".desc)
      .limit(limit)
  }
}
