package databases.requests.overview

import common.StringExt.StringExt
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class TopSeasonScorersOverviewRequestTest extends AnyFunSuite with Matchers {
  test("TopSeasonScorersOverviewRequest generates for the world") {
    val builder = TopSeasonScorersOverviewRequest.builder(77, 12, None, None)
    val request = builder.sqlWithParameters().sql

    request.normalize() should be(
      """
        |SELECT any(league_id) as league_id, player_id, first_name, last_name, team_id, argMax(team_name, round) as team_name, league_unit_id, league_unit_name, sum(goals) as value, argMax(nationality, round) as nationality
        |FROM (
        |SELECT league_id, player_id, first_name, last_name, team_id, team_name, league_unit_id, league_unit_name, goals, round, nationality
        |FROM hattrick.player_stats
        | WHERE ((season = {nested_req_season_0}) AND (round <= {nested_req_round_3}) AND (cup_level = {nested_req_cup_level_4}))     )
        |   GROUP BY player_id, first_name, last_name, team_id, league_unit_id, league_unit_name   ORDER BY value DESC   LIMIT 0, 6
        |""".stripMargin.normalize()
    )
  }
}
