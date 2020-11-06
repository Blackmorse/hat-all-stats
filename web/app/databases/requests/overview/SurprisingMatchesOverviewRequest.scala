package databases.requests.overview

import anorm.RowParser
import databases.requests.ClickhouseOverviewRequest
import databases.requests.overview.model.MatchTopHatstatsOverview

object SurprisingMatchesOverviewRequest extends ClickhouseOverviewRequest[MatchTopHatstatsOverview] {
  override val sql: String = """
       |SELECT
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
       |    ((((((rating_midfield * 3) + rating_left_att) + rating_mid_att) + rating_right_att) + rating_left_def) + rating_right_def) + rating_mid_def AS hatstats,
       |    ((((((opposite_rating_midfield * 3) + opposite_rating_left_att) + opposite_rating_right_att) + opposite_rating_mid_att) + opposite_rating_left_def) + opposite_rating_right_def) + opposite_rating_mid_def AS opposite_hatstats,
       |    hatstats - opposite_hatstats as hatstats_difference,
       |    abs(hatstats_difference) as abs_hatstats_difference
       |FROM hattrick.match_details
       |__where__ AND (((goals - enemy_goals) * hatstats_difference) < 0) AND (opposite_team_id != 0)
       |ORDER BY
       |   abs_hatstats_difference desc
       |LIMIT 1 BY match_id
       |__limit__
       |""".stripMargin

  override val rowParser: RowParser[MatchTopHatstatsOverview] = MatchTopHatstatsOverview.mapper
}
