package databases.requests.overview

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import common.StringExt.StringExt

class OverviewTeamPlayerAveragesRequestTest extends AnyFunSuite with Matchers {
  test("Nested select should be nested") {
    val builder = OverviewTeamPlayerAveragesRequest.builder(75, 14, Some(10), Some(4))
    val sqlParameters = builder.sqlWithParameters()

    sqlParameters.sql.normalize() should be(
      """
        |select touint16(avg(avg_age)) as avg_age,
        |touint32(avg(sum_salary)) as avg_salary,
        |avg(sum_rating) as avg_rating
        |from (
        |  select avg((age * 112) + days) as avg_age,
        |  sum(rating) as sum_rating,
        |  sum(salary) as sum_salary
        |  from hattrick.player_stats where
        |  (
        |  (round = {nested_req_round_0}) and (season = {nested_req_season_1}) and
        |  (league_id = {nested_req_league_id_2}) and (division_level = {nested_req_division_level_3}) and
        |  (cup_level = {nested_req_cup_level_4})
        |  )
        |  group by team_id
        |)
        |""".stripMargin.normalize())
  }
}
