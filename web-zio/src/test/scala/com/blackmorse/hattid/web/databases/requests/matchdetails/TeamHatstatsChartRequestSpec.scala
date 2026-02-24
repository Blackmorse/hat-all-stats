package com.blackmorse.hattid.web.databases.requests.matchdetails

import com.blackmorse.hattid.web.databases.requests.OrderingKeyPath
import com.blackmorse.hattid.web.databases.requests.matchdetails.chart.TeamHatstatsChartRequest
import zio.test.ZIOSpecDefault
import zio.test.{test, *}
import com.blackmorse.hattid.web.StringExt

object TeamHatstatsChartRequestSpec extends ZIOSpecDefault:
  def spec = suite("TeamHatstatsChartRequest")(
    test("Query is correct") {
      val builder = TeamHatstatsChartRequest.sqlBuilder(OrderingKeyPath(
        leagueId = Some(11),
        divisionLevel = Some(1),
        teamId = Some(111),
      ), 100)

      val sql = builder.sqlWithParameters().sql
      
      sql.normalizeEqualTo(
        """
          |SELECT team_id, league_id as league, team_name, league_unit_id, league_unit_name, season, round, rating_midfield * 3 + rating_left_att + rating_right_att + rating_mid_att + rating_left_def + rating_right_def + rating_mid_def as hatstats,  rating_midfield as midfield, toInt32(rating_right_def + rating_left_def + rating_mid_def) as defense, toInt32(rating_right_att + rating_mid_att + rating_left_att) as attack, loddar_stats(rating_midfield, rating_mid_def, rating_left_def, rating_right_def, rating_mid_att, rating_left_att, rating_right_att, tactic_skill) as loddar_stats
          |FROM hattrick.match_details
          | WHERE ((season = {main_season_0}) AND (league_id = {main_league_id_1}) AND (division_level = {main_division_level_2}) AND (team_id = {main_team_id_4}) AND (cup_level = {main_cup_level_5}))
          |""".stripMargin
      )
    }
  )
