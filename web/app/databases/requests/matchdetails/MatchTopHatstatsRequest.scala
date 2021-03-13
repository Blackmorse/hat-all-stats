package databases.requests.matchdetails

import anorm.RowParser
import databases.requests.ClickhouseRequestFunctions.Away
import databases.requests.{ClickhouseRequestFunctions, ClickhouseStatisticsRequest}
import databases.requests.model.`match`.MatchTopHatstats

object MatchTopHatstatsRequest extends ClickhouseStatisticsRequest[MatchTopHatstats] {
  override val sortingColumns: Seq[String] = Seq("sum_hatstats", "sum_loddar_stats")

  override val aggregateSql: String =
    s"""SELECT
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
      |    hatstats + opposite_hatstats AS sum_hatstats,
      |    ${ClickhouseRequestFunctions.loddarStats()} as loddar_stats,
      |    ${ClickhouseRequestFunctions.loddarStats(Away)} as opposite_loddar_stats,
      |    loddar_stats + opposite_loddar_stats as sum_loddar_stats
      |FROM hattrick.match_details
      |__where__
      |ORDER BY
      |   __sortBy__ __sortingDirection__,
      |   team_id __sortingDirection__
      |LIMIT 1 BY match_id
      |__limit__
      |""".stripMargin

  override val oneRoundSql: String = s"""
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
       |    hatstats + opposite_hatstats AS sum_hatstats,
       |    ${ClickhouseRequestFunctions.loddarStats()} as loddar_stats,
       |    ${ClickhouseRequestFunctions.loddarStats(Away)} as opposite_loddar_stats,
       |    loddar_stats + opposite_loddar_stats as sum_loddar_stats
       |FROM hattrick.match_details
       |__where__
       |ORDER BY
       |   __sortBy__ __sortingDirection__,
       |   team_id __sortingDirection__
       |LIMIT 1 BY match_id
       |__limit__
       |""".stripMargin

  override val rowParser: RowParser[MatchTopHatstats] = MatchTopHatstats.mapper
}
