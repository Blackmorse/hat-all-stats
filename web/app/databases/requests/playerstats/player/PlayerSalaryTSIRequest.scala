package databases.requests.playerstats.player

import anorm.RowParser
import databases.requests.ClickhouseRequest
import databases.requests.model.player.PlayerSalaryTSI

object PlayerSalaryTSIRequest extends ClickhousePlayerSingleRoundRequest[PlayerSalaryTSI] {
  override val sortingColumns: Seq[String] = Seq("age", "tsi", "salary")

  override val oneRoundSql: String =
    s"""
      |SELECT
      |    league_id,
      |    team_name,
      |    team_id,
      |    league_unit_name,
      |    league_unit_id,
      |    player_id,
      |    first_name,
      |    last_name,
      |    ((age * 112) + days)  AS age,
      |    tsi,
      |    salary,
      |    nationality,
      |    ${ClickhouseRequest.roleIdCase("role_id")} as role
      |FROM hattrick.player_stats
      |__where__
      |ORDER BY
      |    __sortBy__ __sortingDirection__,
      |    player_id __sortingDirection__
      |__limit__""".stripMargin
  override val rowParser: RowParser[PlayerSalaryTSI] = PlayerSalaryTSI.mapper
}
