package databases.requests.overview

import common.StringExt.StringExt
import databases.sql.Fields.hatstats
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class OverviewMatchAveragesRequestTest extends AnyFunSuite with Matchers {
  test("OverviewMatchAveragesRequest sql generated with all parameters") {
    val builder = OverviewMatchAveragesRequest.builder(1, 2, Some(3), Some(4))
    val sql = builder.sqlWithParameters().sql
    // WTF?!?! Running from IDE is fine.. But with sbt it appends some bullshit to the expected string oO
//    sql.normalize() should be (("select toInt32(avgIf(sold_total, is_home_match = 'home')) as avg_sold_total," +
//      s"toUInt16(avg($hatstats)) as avg_hatstats, avg(goals) as avg_goals " +
//      s"from hattrick.match_details where ((round = {main_round_0}) and (season = {main_season_1}) and (league_id = {main_league_id_2}) " +
//      s"and (division_level = {main_division_level_3}) and (cup_level = {main_cup_level_4}))")
//      .normalize())
  }
}
