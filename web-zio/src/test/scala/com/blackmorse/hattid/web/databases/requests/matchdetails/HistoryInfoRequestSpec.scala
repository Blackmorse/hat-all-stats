package com.blackmorse.hattid.web.databases.requests.matchdetails

import zio.*
import zio.test.{test, *}
import com.blackmorse.hattid.web.StringExt
import sqlbuilder.ValueParameter
import zio.test.Assertion.equalTo

object HistoryInfoRequestSpec extends ZIOSpecDefault:
  def spec = suite("HistoryInfoRequestSpec")(
    test("No parameters results in no filters") {
      val request = HistoryInfoRequest
      val builder = HistoryInfoRequest.builder(None, None, None)
      val sql = builder.sqlWithParameters().sql

      sql.normalizeEqualTo(
        """
          |select season, league_id, division_level, round, count() as cnt
          | from hattrick.match_details
          | where ((cup_level = {main_cup_level_3}))
          | group by season, league_id, division_level, round
          | order by season asc, league_id asc, division_level asc, round asc""".stripMargin)
    } +
      test("league_id passes as parameter") {
        val builder = HistoryInfoRequest.builder(Some(11), None, None)
        val sqlParameters = builder.sqlWithParameters()
        val sql = sqlParameters.sql

        val parameters = sqlParameters.parameters.filter(_.asInstanceOf[ValueParameter[Int]].value.isDefined)

        sql.normalizeEqualTo(
          """
            |select season, league_id, division_level, round, count() as cnt
            | from hattrick.match_details
            | where ((league_id = {main_league_id_0}) and (cup_level = {main_cup_level_3}))
            | group by season, league_id, division_level, round
            | order by season asc, league_id asc, division_level asc, round asc
            |""".stripMargin.normalize()) *>
          ZIO.succeed(parameters.size).map(size => assert(size)(equalTo(2))) *>
          ZIO.succeed(parameters.head.asInstanceOf[ValueParameter[Int]].value).map(value => assert(value)(equalTo(Some(11)))) *>
          ZIO.succeed(parameters.head.asInstanceOf[ValueParameter[Int]].name).map(name => assert(name)(equalTo("league_id")))
      } +
      test("league_id, season, round passed as parameter") {
        val builder = HistoryInfoRequest.builder(Some(1), Some(11), Some(111))
        val sqlParameters = builder.sqlWithParameters()
        val sql = sqlParameters.sql
        val parameters = sqlParameters.parameters.filter(_.asInstanceOf[ValueParameter[Int]].value.isDefined)
        
        sql.normalizeEqualTo(
          """
            |select season, league_id, division_level, round, count() as cnt
            | from hattrick.match_details
            | where ((league_id = {main_league_id_0})
            | and (season = {main_season_1}) and (round = {main_round_2}) and (cup_level = {main_cup_level_3}))
            | group by season, league_id, division_level, round
            | order by season asc, league_id asc, division_level asc, round asc""".stripMargin) *>
          ZIO.succeed(parameters.size).map(size => assert(size)(equalTo(4)))
      }
  )
