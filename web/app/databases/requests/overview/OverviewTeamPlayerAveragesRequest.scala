package databases.requests.overview

import anorm.RowParser
import databases.requests.ClickhouseOverviewRequest
import databases.requests.overview.model.OverviewTeamPlayerAverages

object OverviewTeamPlayerAveragesRequest extends ClickhouseOverviewRequest[OverviewTeamPlayerAverages] {
  override val sql: String = """
     |SELECT
     |    toUInt16(avg(avg_age)) AS avg_age,
     |    toUInt32(avg(sum_salary)) AS avg_salary,
     |    avg(sum_rating) AS avg_rating
     |FROM
     |(
     |    SELECT
     |        avg((age * 112) + days) AS avg_age,
     |        sum(rating) AS sum_rating,
     |        sum(salary) AS sum_salary
     |    FROM hattrick.player_stats
     |    __where__
     |    GROUP BY team_id
     |)
     |""".stripMargin

  override val rowParser: RowParser[OverviewTeamPlayerAverages] = OverviewTeamPlayerAverages.mapper
}
