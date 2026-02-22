package com.blackmorse.hattid.web.databases.requests.playerstats.player

import zio.test.ZIOSpecDefault
import com.blackmorse.hattid.web.StringExt
import com.blackmorse.hattid.web.databases.requests.ClickhouseRequest

object PlayerHistoryRequestSpec extends ZIOSpecDefault:
  def spec = suite("PlayerHistoryRequest")(
    test("Query is correct") {
      PlayerHistoryRequest.builder(1L).sqlWithParameters()
        .sql
        .normalizeEqualTo(
          s"""
            |SELECT league_id,
            |league_unit_id,
            |league_unit_name,
            |player_id,
            |first_name,
            |last_name,
            |team_id,
            |team_name,
            |(age * 112) + days as age,
            |tsi,
            |rating,
            |rating_end_of_match,
            |cup_level,
            |${ClickhouseRequest.roleIdCase("role_id")} as role,
            |played_minutes,
            |injury_level,
            |salary,
            |yellow_cards,
            |red_cards,
            |goals,
            |season,
            |round,
            |nationality
            |FROM hattrick.player_stats
            | WHERE ((player_id = 1))    ORDER BY season DESC , round DESC , cup_level ASC    SETTINGS optimize_read_in_order = 0""".stripMargin)
    }
  )
