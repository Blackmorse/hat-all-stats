package com.blackmorse.hattid.web.databases.requests.overview

import zio.test.ZIOSpecDefault
import com.blackmorse.hattid.web.StringExt

object OverviewMatchAveragesRequestSpec extends ZIOSpecDefault:
  def spec = suite("OverviewMatchAveragesRequestSpec")(
    test("Query is correct") {
      val builder = OverviewMatchAveragesRequest.builder(1, 2, Some(3), Some(4))
      val sql = builder.sqlWithParameters().sql

      sql.normalizeEqualTo(
        "select toInt32(avgIf(sold_total, is_home_match = 'home')) as avg_sold_total," +
          s"toUInt16(avg(rating_midfield * 3 + rating_left_att + rating_right_att + rating_mid_att + rating_left_def + rating_right_def + rating_mid_def)) as avg_hatstats, avg(goals) as avg_goals " +
          s"from hattrick.match_details where ((round = {main_round_0}) and (season = {main_season_1}) and (league_id = {main_league_id_2}) " +
          s"and (division_level = {main_division_level_3}) and (cup_level = {main_cup_level_4}))"
      )
    }
  )
