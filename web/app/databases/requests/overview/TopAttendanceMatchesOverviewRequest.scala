package databases.requests.overview

import anorm.RowParser
import databases.requests.ClickhouseOverviewRequest
import databases.requests.overview.model.MatchAttendanceOverview
import databases.sqlbuilder.{Select, SqlBuilder}

object TopAttendanceMatchesOverviewRequest extends ClickhouseOverviewRequest[MatchAttendanceOverview] {
  override val rowParser: RowParser[MatchAttendanceOverview] = MatchAttendanceOverview.mapper

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
        "opposite_team_id",
        "opposite_team_name",
        "match_id",
        "is_home_match",
        "goals",
        "enemy_goals",
        "sold_total" as "spectators"
      )
      .from("hattrick.match_details")
      .where
        .round(round)
        .season(season)
        .leagueId(leagueId)
        .divisionLevel(divisionLevel)
        .isLeagueMatch
      .orderBy("spectators".desc)
      .limitBy(1, "match_id")
      .limit(limit)
  }
}
