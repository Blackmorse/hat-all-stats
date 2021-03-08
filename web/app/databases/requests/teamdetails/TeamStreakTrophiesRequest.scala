package databases.requests.teamdetails

import anorm.RowParser
import databases.requests.ClickhouseStatisticsRequest
import databases.requests.model.team.TeamStreakTrophies

object TeamStreakTrophiesRequest extends ClickhouseStatisticsRequest[TeamStreakTrophies] {
  override val sortingColumns: Seq[String] = Seq("trophies_number", "number_of_victories", "number_of_undefeated")

  override val aggregateSql: String = ""

  override val oneRoundSql: String = """
     |SELECT
     |    league_id,
     |    team_id,
     |    team_name,
     |    league_unit_id,
     |    league_unit_name,
     |    trophies_number,
     |    number_of_victories,
     |    number_of_undefeated
     |FROM hattrick.team_details
     | __where__ AND (round = __round__)
     |ORDER BY
     |   __sortBy__ __sortingDirection__,
     |   team_id __sortingDirection__
     |__limit__""".stripMargin

  override val rowParser: RowParser[TeamStreakTrophies] = TeamStreakTrophies.mapper
}
