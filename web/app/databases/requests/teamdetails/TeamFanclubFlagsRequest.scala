package databases.requests.teamdetails

import anorm.RowParser
import databases.requests.ClickhouseStatisticsRequest
import databases.requests.model.team.TeamFanclubFlags

object TeamFanclubFlagsRequest extends ClickhouseStatisticsRequest[TeamFanclubFlags] {
  override val sortingColumns: Seq[String] = Seq("fanclub_size", "home_flags", "away_flags", "all_flags")
  override val aggregateSql: String = ""

  override val oneRoundSql: String = """
       |SELECT
       |    league_id,
       |    team_id,
       |    team_name,
       |    league_unit_id,
       |    league_unit_name,
       |    fanclub_size,
       |    home_flags,
       |    away_flags,
       |    home_flags + away_flags AS all_flags
       |FROM hattrick.team_details
       | __where__
       |ORDER BY
       |   __sortBy__ __sortingDirection__,
       |   team_id __sortingDirection__
       |__limit__
       |""".stripMargin

  override val rowParser: RowParser[TeamFanclubFlags] = TeamFanclubFlags.mapper
}
