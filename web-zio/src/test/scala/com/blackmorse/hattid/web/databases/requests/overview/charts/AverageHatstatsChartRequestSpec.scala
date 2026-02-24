package com.blackmorse.hattid.web.databases.requests.overview.charts

import com.blackmorse.hattid.web.databases.requests.OrderingKeyPath
import zio.test.ZIOSpecDefault
import com.blackmorse.hattid.web.StringExt
import com.blackmorse.hattid.web.databases.sql.Fields.hatstats

object AverageHatstatsChartRequestSpec extends ZIOSpecDefault:
  def spec = suite("AverageHatstatsChartRequest")(
    test("Query is correct") {
      val orderingKeyPath = OrderingKeyPath(
        leagueId = Some(11),
        divisionLevel = Some(4)
      )

      val builder = AverageHatstatsChartRequest.builder(orderingKeyPath, 76, 12)
      val sqlParameters = builder.sqlWithParameters()

      sqlParameters.sql.normalizeEqualTo(
        s"""
          |select season,round, toInt32(avg(rating_midfield * 3 + rating_left_att + rating_right_att + rating_mid_att + rating_left_def + rating_right_def + rating_mid_def)) as count
          |from hattrick.match_details
          |where
          |(
          | (league_id = {main_league_id_0}) and (division_level = {main_division_level_1}) and
          | (cup_level = {main_cup_level_4}) and
          | (season >= {main_season_5}) and (round <= {main_round_6}) and
          | (not (season = 76 and round > 12))
          |) group by season, round
          |order by season asc, round asc
          |""")
    }
  )

