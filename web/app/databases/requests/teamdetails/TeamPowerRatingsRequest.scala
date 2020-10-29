package databases.requests.teamdetails

import anorm.RowParser
import databases.requests.ClickhouseStatisticsRequest
import databases.requests.model.team.TeamPowerRating

object TeamPowerRatingsRequest extends ClickhouseStatisticsRequest[TeamPowerRating] {
  override val sortingColumns: Seq[String] = Seq("power_rating")
  override val aggregateSql: String = ""

  override val oneRoundSql: String = """
       |SELECT
       |    team_id,
       |    team_name,
       |    league_unit_id,
       |    league_unit_name,
       |    power_rating
       |FROM hattrick.team_details
       | __where__ AND (round = __round__)
       |ORDER BY
       |   __sortBy__ __sortingDirection__,
       |   team_id __sortingDirection__
       |__limit__""".stripMargin

  override val rowParser: RowParser[TeamPowerRating] = TeamPowerRating.mapper
}
