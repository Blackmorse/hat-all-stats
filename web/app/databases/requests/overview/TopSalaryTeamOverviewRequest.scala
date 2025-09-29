package databases.requests.overview

import anorm.RowParser
import databases.requests.ClickhouseOverviewRequest
import databases.requests.model.overview.TeamStatOverview
import sqlbuilder.{Select, SqlBuilder}

object TopSalaryTeamOverviewRequest extends ClickhouseOverviewRequest[TeamStatOverview] {
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
        "team_name",
        "team_id",
        "sum(salary)" `as` "value"
      )
      .from("hattrick.player_stats")
      .where
        .round(round)
        .season(season)
        .leagueId(leagueId)
        .divisionLevel(divisionLevel)
        .isLeagueMatch
      .groupBy("league_id",
        "league_unit_id",
        "league_unit_name",
        "team_id",
        "team_name")
      .orderBy("value".desc)
      .limit(limit)
  }
}
