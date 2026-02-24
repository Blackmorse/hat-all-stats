package com.blackmorse.hattid.web.databases.requests.overview

import zio.test.ZIOSpecDefault
import com.blackmorse.hattid.web.StringExt

object TopSeasonScorersOverviewRequestSpec extends ZIOSpecDefault:
  def spec = suite("TopSeasonScorersOverviewRequest")(
    test("Query is correct") {
      val builder = TopSeasonScorersOverviewRequest.builder(77, 12, None, None)
      val request = builder.sqlWithParameters().sql
      
      request.normalizeEqualTo(
        """
          |SELECT any(league_id) as league_id, player_id, first_name, last_name, team_id, argMax(team_name, round) as team_name, league_unit_id, league_unit_name, sum(goals) as value, argMax(nationality, round) as nationality
          |FROM (
          |SELECT league_id, player_id, first_name, last_name, team_id, team_name, league_unit_id, league_unit_name, goals, round, nationality
          |FROM hattrick.player_stats
          | WHERE ((season = {nested_req_season_0}) AND (round <= {nested_req_round_3}) AND (cup_level = {nested_req_cup_level_4}))     )
          |   GROUP BY player_id, first_name, last_name, team_id, league_unit_id, league_unit_name   ORDER BY value DESC   LIMIT 0, 6
          |   SETTINGS max_bytes_before_external_group_by = 700000000
          |""".stripMargin
      )
    }
  )
