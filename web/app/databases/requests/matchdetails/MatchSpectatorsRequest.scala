package databases.requests.matchdetails

import anorm.RowParser
import databases.requests.ClickhouseStatisticsRequest
import databases.requests.model.`match`.MatchSpectators

object MatchSpectatorsRequest extends ClickhouseStatisticsRequest[MatchSpectators] {
  override val sortingColumns: Seq[String] = Seq("sold_total")
  override val aggregateSql: String =
    """SELECT
      |    league_id,
      |    league_unit_id,
      |    league_unit_name,
      |    team_id,
      |    team_name,
      |    opposite_team_id,
      |    opposite_team_name,
      |    match_id,
      |    is_home_match,
      |    sold_total,
      |    goals,
      |    enemy_goals
      |FROM hattrick.match_details
      |__where__
      |ORDER BY
      |   __sortBy__ __sortingDirection__,
      |   team_id __sortingDirection__
      |LIMIT 1 BY match_id
      |__limit__
      |""".stripMargin

  override val oneRoundSql: String =
    """SELECT
      |    league_id,
      |    league_unit_id,
      |    league_unit_name,
      |    team_id,
      |    team_name,
      |    opposite_team_id,
      |    opposite_team_name,
      |    match_id,
      |    is_home_match,
      |    sold_total,
      |    goals,
      |    enemy_goals
      |FROM hattrick.match_details
      |__where__
      |ORDER BY
      |   __sortBy__ __sortingDirection__,
      |   team_id __sortingDirection__
      |LIMIT 1 BY match_id
      |__limit__
      |""".stripMargin

  override val rowParser: RowParser[MatchSpectators] = MatchSpectators.mapper
}
