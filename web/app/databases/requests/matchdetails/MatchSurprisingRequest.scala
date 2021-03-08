package databases.requests.matchdetails

import anorm.RowParser
import databases.requests.ClickhouseStatisticsRequest
import databases.requests.model.`match`.MatchTopHatstats

object MatchSurprisingRequest extends ClickhouseStatisticsRequest[MatchTopHatstats] {
  override val sortingColumns: Seq[String] = Seq("abs_goals_difference", "abs_hatstats_difference")
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
      |    goals,
      |    enemy_goals,
      |    abs(goals - enemy_goals) as abs_goals_difference,
      |    ((((((rating_midfield * 3) + rating_left_att) + rating_mid_att) + rating_right_att) + rating_left_def) + rating_right_def) + rating_mid_def AS hatstats,
      |    ((((((opposite_rating_midfield * 3) + opposite_rating_left_att) + opposite_rating_right_att) + opposite_rating_mid_att) + opposite_rating_left_def) + opposite_rating_right_def) + opposite_rating_mid_def AS opposite_hatstats,
      |    hatstats - opposite_hatstats as hatstats_difference,
      |    abs(hatstats_difference) as abs_hatstats_difference
      |FROM hattrick.match_details
      |__where__ AND (((goals - enemy_goals) * hatstats_difference) < 0) AND (opposite_team_id != 0)
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
      |    goals,
      |    enemy_goals,
      |    abs(goals - enemy_goals) as abs_goals_difference,
      |    ((((((rating_midfield * 3) + rating_left_att) + rating_mid_att) + rating_right_att) + rating_left_def) + rating_right_def) + rating_mid_def AS hatstats,
      |    ((((((opposite_rating_midfield * 3) + opposite_rating_left_att) + opposite_rating_right_att) + opposite_rating_mid_att) + opposite_rating_left_def) + opposite_rating_right_def) + opposite_rating_mid_def AS opposite_hatstats,
      |    hatstats - opposite_hatstats as hatstats_difference,
      |    abs(hatstats_difference) as abs_hatstats_difference
      |FROM hattrick.match_details
      |__where__ AND (((goals - enemy_goals) * hatstats_difference) < 0) AND (round = __round__) AND (opposite_team_id != 0)
      |ORDER BY
      |   __sortBy__ __sortingDirection__,
      |   team_id __sortingDirection__
      |LIMIT 1 BY match_id
      |__limit__
      |""".stripMargin

  override val rowParser: RowParser[MatchTopHatstats] = MatchTopHatstats.mapper
}
