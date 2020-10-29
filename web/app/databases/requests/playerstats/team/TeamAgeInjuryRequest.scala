package databases.requests.playerstats.team

import anorm.RowParser
import databases.requests.ClickhouseStatisticsRequest
import databases.requests.model.team.TeamAgeInjury

object TeamAgeInjuryRequest extends ClickhouseStatisticsRequest[TeamAgeInjury] {
  override val sortingColumns: Seq[String] = Seq("age", "injury", "injury_count")

  override val aggregateSql: String = ""

  override val oneRoundSql: String = """
       |SELECT
       |    argMax(team_name, round) as team_name,
       |    team_id,
       |    league_unit_id,
       |    league_unit_name,
       |    toUInt32(avg((age * 112) + days)) AS age,
       |    sumIf(injury_level, (played_minutes > 0) AND (injury_level > 0)) AS injury,
       |    countIf(injury_level, (played_minutes > 0) AND (injury_level > 0)) AS injury_count
       |FROM hattrick.player_stats
       |__where__ AND (round = __round__)
       |GROUP BY
       |    team_id,
       |    league_unit_id,
       |    league_unit_name
       |ORDER BY __sortBy__ __sortingDirection__, team_id asc
       |__limit__""".stripMargin

  override val rowParser: RowParser[TeamAgeInjury] = TeamAgeInjury.mapper
}
