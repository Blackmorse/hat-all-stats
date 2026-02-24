package com.blackmorse.hattid.web.databases.requests.overview.charts

import com.blackmorse.hattid.web.databases.requests.OrderingKeyPath
import zio.test.ZIOSpecDefault
import com.blackmorse.hattid.web.StringExt

object NewTeamsNumberChartRequestSpec extends ZIOSpecDefault:
  def spec = suite("NewTeamsNumberChartRequestSpec")(
    test("Query is correct") {
      val orderingKeyPath = OrderingKeyPath(
        leagueId = Some(35),
        divisionLevel = Some(4),
        leagueUnitId = Some(12341)
      )

      val builder = NewTeamsNumberChartRequest.builder(orderingKeyPath, 75, 12)
      val sqlParameters = builder.sqlWithParameters()

      sqlParameters.sql.normalizeEqualTo(
        """
          |select season, round, count() as count
          |from (
          |  with(
          |    select max(dt) from hattrick.match_details
          |    where
          |    (
          |      (season = {with_season_0}) and (round = {with_round_1}) and
          |      (league_id = {with_league_id_2}) and (division_level = {with_division_level_3}) and
          |      (league_unit_id = {with_league_unit_id_4}) and (cup_level = {with_cup_level_6})
          |    )
          |  ) as dt
          |  select
          |   season,
          |   round,
          |   (dt - (((75 - season) * 16) * 7)) - ((12 - round) * 7) as league_match_day,
          |   league_match_day - founded_date as diff
          |   from hattrick.team_details
          |   where
          |   (
          |     (season >= {nested_season_0}) and (round <= {nested_round_1}) and
          |     (NOT (season = 75 and round > 12)) and
          |     (league_id = {nested_league_id_2}) and (division_level = {nested_division_level_3}) and
          |     (league_unit_id = {nested_league_unit_id_4}) and
          |     (diff <= multiIf(round = 1, 21, 7))
          |   )
          |) group by season, round
          |order by season ASC WITH FILL TO 75 + 1, round ASC with fill to 14 + 1
          |""".stripMargin)
    }
  )
