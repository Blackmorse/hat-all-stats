package databases.requests.playerstats.player.stats

import common.StringExt.StringExt
import databases.requests.{ClickhouseRequest, OrderingKeyPath}
import models.web.{Accumulate, Asc, PlayersParameters, RestStatisticsParameters}
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class PlayerGamesGoalsRequestTest extends AnyFunSuite with Matchers {
  test("Test having clause") {
    val orderingKeyPath = OrderingKeyPath(
      season = Some(1),
      leagueId = Some(2),
      divisionLevel = Some(3),
      leagueUnitId = Some(4),
      teamId = Some(5)
    )
    val restStatisticsParameters = RestStatisticsParameters(
      page = 1,
      pageSize = 16,
      sortBy = "games",
      sortingDirection = Asc,
      season = 75,
      statsType = Accumulate
    )

    val playersParameters = PlayersParameters(
      role = Some("midfielder"),
      nationality = Some(1),
      minAge = Some(18),
      maxAge = Some(22)
    )

    val builder = PlayerGamesGoalsRequest.buildSql(orderingKeyPath,
      restStatisticsParameters, playersParameters, Some("midfielder"), round = 14)

    val sqlParameters = builder.sqlWithParameters()

    sqlParameters.sql.normalize() should be (
      s"""
        |select any(league_id) as league,
        |player_id,
        |first_name,
        |last_name,
        |team_id,
        |argMax(team_name, round) as team_name,
        |league_unit_id,
        |league_unit_name,
        |countIf(played_minutes > 0) as games,
        |sum(played_minutes) as played,
        |sum(goals) as scored,
        |floor(played / scored, 2) as goal_rate,
        |argMax(nationality, round) as nationality,
        |arrayFirst(x -> x != 0, topK(2)(${ClickhouseRequest.roleIdCase("role_id")})) as role,
        |((argMax(age, round) * 112) + argMax(days, round)) as age
        |from hattrick.player_stats
        |where
        |(
        | (season = {main_season_0}) and (league_id = {main_league_id_1}) and
        | (division_level = {main_division_level_2}) and (league_unit_id = {main_league_unit_id_3}) and
        | (team_id = {main_team_id_4}) and (cup_level = {main_cup_level_5})
        |)
        |group by player_id, first_name, last_name, team_id, league_unit_id, league_unit_name
        |having
        |(
        | (round <= {main_round_6}) and (role = {main_role_7}) and
        | (nationality = {main_nationality_8}) and (age >= {main_age_9}) and
        | (age <= {main_age_10})
        |)
        |order by games asc, player_id asc
        |limit 16, 17
        |SETTINGS max_bytes_before_external_group_by = 700000000
        |""".stripMargin.normalize())
  }
}
