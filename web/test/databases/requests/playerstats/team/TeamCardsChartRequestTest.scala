package databases.requests.playerstats.team

import common.StringExt.StringExt
import databases.requests.OrderingKeyPath
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class TeamCardsChartRequestTest extends AnyFunSuite with Matchers {
  test("Test TeamCardsChartRequest toString") {
    val builder = TeamCardsChartRequest.builder(OrderingKeyPath(
      leagueId = Some(11),
      divisionLevel = Some(1),
      teamId = Some(111),
    ), 100)

    val sql = builder.sqlWithParameters().sql

    sql.normalize() should be (
      """
        |SELECT any(league_id) as league, argMax(team_name, round) as team_name, team_id, league_unit_id, league_unit_name, sum(yellow_cards) as yellow_cards_round, sum(red_cards) as red_cards_round, season, round, sum(yellow_cards_round) OVER (PARTITION BY team_id ORDER BY round) as yellow_cards_sum, sum(red_cards_round) OVER (PARTITION BY team_id ORDER BY round) as red_cards_sum
        |FROM hattrick.player_stats
        | WHERE ((season = {main_season_0}) AND (league_id = {main_league_id_1}) AND (division_level = {main_division_level_2}) AND (team_id = {main_team_id_4}))  GROUP BY team_id, league_unit_id, league_unit_name, round   ORDER BY team_id ASC , round ASC
        |""".stripMargin.normalize())
  }
}
