package databases.requests.matchdetails

import common.StringExt.StringExt
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import sqlbuilder.ValueParameter
import zio.{Unsafe, ZIO, ZLayer}
import zio.Console.*
import zio.*

import java.io.IOException

class Service() {
  def echo = printLine("job")
}

object Service {
  def make: ZIO[Any, IOException, Service] = {
    for {
      _ <- printLine("Init!")
      service <- ZIO.succeed(new Service())
    } yield service
  }
}

class HistoryInfoRequestTest extends AnyFunSuite with Matchers {
  test("ZIO") {
    val layer = ZLayer{ Service.make }

    
    

    

    val env = ZIO.scoped {
     layer.build
    }

    val program = (for {
      service <- ZIO.service[Service]
      _ <- service.echo
    } yield ())

//    val env = Unsafe.unsafe { implicit unsafe =>
////      Runtime.default.unsafe.run(
//        
////      ).getOrThrowFiberFailure()
//    }
    
//    val env = Unsafe.unsafe { implicit unsafe =>
//      Runtime.default.unsafe.run(layer.build()).getOrThrowFiberFailure()
//    }

    Unsafe.unsafe(implicit unsafe =>
      val builtEnv = Runtime.default.unsafe.run(
        ZIO.scoped(layer.build)
      ).getOrThrowFiberFailure()
      
      
      Runtime.default.unsafe.run(program.provideEnvironment(builtEnv))
      Runtime.default.unsafe.run(program.provideEnvironment(builtEnv))
    )
  }

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
