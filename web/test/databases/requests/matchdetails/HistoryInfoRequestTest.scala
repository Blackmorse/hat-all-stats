package databases.requests.matchdetails

import common.StringExt.StringExt
import org.scalatest.{FunSuite, Matchers}
import sqlbuilder.ValueParameter

class HistoryInfoRequestTest extends FunSuite with Matchers {
  test("No parameters results in no filters") {
    val builder = HistoryInfoRequest.builder(None, None, None)
    val sql = builder.sqlWithParameters().sql

    sql.normalize() should be ("""select season, league_id, division_level, round, count() as cnt
      | from hattrick.match_details
      | where ((cup_level = {main_cup_level_3}))
      | group by season, league_id, division_level, round
      | order by season asc, league_id asc, division_level asc, round asc
      |""".stripMargin.normalize())
  }

  test("league_id passes as parameter") {
    val builder = HistoryInfoRequest.builder(Some(11), None, None)
    val sqlParameters = builder.sqlWithParameters()
    val sql = sqlParameters.sql

    sql.normalize() should be ("""select season, league_id, division_level, round, count() as cnt
                                 | from hattrick.match_details
                                 | where ((league_id = {main_league_id_0}) and (cup_level = {main_cup_level_3}))
                                 | group by season, league_id, division_level, round
                                 | order by season asc, league_id asc, division_level asc, round asc
                                 |""".stripMargin.normalize())

    val parameters = sqlParameters.parameters.filter(_.asInstanceOf[ValueParameter[Int]].value.isDefined)
    parameters.size should be (2)
    parameters.head.asInstanceOf[ValueParameter[Int]].value should be (Some(11))
    parameters.head.asInstanceOf[ValueParameter[Int]].name should be ("league_id")
  }

  test("league_id, season, round passed as parameter") {
    val builder = HistoryInfoRequest.builder(Some(1), Some(11), Some(111))
    val sqlParameters = builder.sqlWithParameters()
    val sql = sqlParameters.sql

    sql.normalize() should be ("""select season, league_id, division_level, round, count() as cnt
                                 | from hattrick.match_details
                                 | where ((league_id = {main_league_id_0})
                                 | and (season = {main_season_1}) and (round = {main_round_2}) and (cup_level = {main_cup_level_3}))
                                 | group by season, league_id, division_level, round
                                 | order by season asc, league_id asc, division_level asc, round asc
                                 |""".stripMargin.normalize())

    val parameters = sqlParameters.parameters.filter(_.asInstanceOf[ValueParameter[Int]].value.isDefined)
    parameters.size should be (4)
  }
}
