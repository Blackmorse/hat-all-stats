package com.blackmorse.hattid.web.databases.requests.playerstats.team

import com.blackmorse.hattid.web.databases.requests.OrderingKeyPath
import com.blackmorse.hattid.web.databases.requests.playerstats.team.chart.TeamCardsChartRequest
import zio.test.ZIOSpecDefault
import com.blackmorse.hattid.web.StringExt

object TeamCardsChartRequestSpec extends ZIOSpecDefault:
  def spec = suite("TeamCardsChartRequest")(
    test("Query is correct") {
      val builder = TeamCardsChartRequest.sqlBuilder(OrderingKeyPath(
        leagueId = Some(11),
        divisionLevel = Some(1),
        teamId = Some(111),
      ), 100)

      val sql = builder.sqlWithParameters().sql

      sql.normalizeEqualTo(
        """
          |SELECT any(league_id) as league, argMax(team_name, round) as team_name, team_id, league_unit_id, league_unit_name, sum(yellow_cards) as yellow_cards_round, sum(red_cards) as red_cards_round, season, round, sum(yellow_cards_round) OVER (PARTITION BY team_id ORDER BY round) as yellow_cards_sum, sum(red_cards_round) OVER (PARTITION BY team_id ORDER BY round) as red_cards_sum
          |FROM hattrick.player_stats
          | WHERE ((season = {main_season_0}) AND (league_id = {main_league_id_1}) AND (division_level = {main_division_level_2}) AND (team_id = {main_team_id_4}))  GROUP BY team_id, league_unit_id, league_unit_name, season, round   ORDER BY team_id ASC , round ASC
          |""".stripMargin)
    }
  )
