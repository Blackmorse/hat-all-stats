package databases.requests.overview

import anorm.RowParser
import databases.requests.ClickhouseOverviewRequest
import databases.requests.overview.model.MatchAttendanceOverview

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
}
