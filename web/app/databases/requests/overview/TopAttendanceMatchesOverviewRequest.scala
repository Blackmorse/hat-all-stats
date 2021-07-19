package databases.requests.overview

import anorm.RowParser
import databases.dao.RestClickhouseDAO
import databases.requests.ClickhouseOverviewRequest
import databases.requests.overview.NumberOverviewRequest.rowParser
import databases.requests.overview.model.MatchAttendanceOverview
import databases.sqlbuilder.SqlBuilder

import scala.concurrent.Future

object TopAttendanceMatchesOverviewRequest extends ClickhouseOverviewRequest[MatchAttendanceOverview] {
  override val sql: String =
    """
      SELECT
          league_id,
          league_unit_id,
          league_unit_name,
          team_id,
          team_name,
          opposite_team_id,
          opposite_team_name,
          match_id,
          is_home_match,
          goals,
          enemy_goals,
          sold_total as spectators
      FROM hattrick.match_details
      __where__
      ORDER BY
         spectators desc
      LIMIT 1 BY match_id
      __limit__
    """
  override val rowParser: RowParser[MatchAttendanceOverview] = MatchAttendanceOverview.mapper

  override def execute(season: Int, round: Int, leagueId: Option[Int], divisionLevel: Option[Int])
                      (implicit restClickhouseDAO: RestClickhouseDAO): Future[List[MatchAttendanceOverview]] = {
    import SqlBuilder.implicits._
    val builder = new SqlBuilder("", newApi = true)
      .select(
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

    restClickhouseDAO.execute(builder.build, rowParser)
  }
}
