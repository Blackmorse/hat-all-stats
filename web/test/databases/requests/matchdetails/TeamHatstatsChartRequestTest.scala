package databases.requests.matchdetails

import common.StringExt.StringExt
import databases.requests.OrderingKeyPath
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class TeamHatstatsChartRequestTest extends AnyFunSuite with Matchers {
  test("Test TeamHatstatsChartRequest") {
    val builder = TeamHatstatsChartRequest.builder(OrderingKeyPath(
      leagueId = Some(11),
      divisionLevel = Some(1),
      teamId = Some(111),
    ), 100)

    val sql = builder.sqlWithParameters().sql

    sql.normalize() should be (
      """
        |SELECT team_id, league_id as league, team_name, league_unit_id, league_unit_name, season, round, rating_midfield * 3 + rating_left_att + rating_right_att + rating_mid_att + rating_left_def + rating_right_def + rating_mid_def as hatstats,  rating_midfield as midfield, toInt32(rating_right_def + rating_left_def + rating_mid_def) as defense, toInt32(rating_right_att + rating_mid_att + rating_left_att) as attack, loddar_stats(rating_midfield, rating_mid_def, rating_left_def, rating_right_def, rating_mid_att, rating_left_att, rating_right_att, tactic_skill) as loddar_stats
        |FROM hattrick.match_details
        | WHERE ((season = {main_season_0}) AND (league_id = {main_league_id_1}) AND (division_level = {main_division_level_2}) AND (team_id = {main_team_id_4}) AND (cup_level = {main_cup_level_5}))
        |""".stripMargin.normalize())
  }
}
