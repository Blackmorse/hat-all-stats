package databases.requests.overview.charts

import databases.requests.OrderingKeyPath
import common.StringExt.StringExt
import databases.sql.Fields.hatstats
import org.scalatest.funsuite.AnyFunSuite
import databases.dao.SqlBuilderParameters
import org.scalatest.matchers.should.Matchers

class AverageHatstatsChartRequestTest extends AnyFunSuite with Matchers {
  test("Test AverageHatstatsChartRequest") {
    val orderingKeyPath = OrderingKeyPath(
      leagueId = Some(11),
      divisionLevel = Some(4)
    )

    val builder = AverageHatstatsChartRequest.builder(orderingKeyPath, 76, 12)
    val sqlParameters = builder.sqlWithParameters()

    sqlParameters.sql.normalize() should be (
      s"""
        |select season,round, toInt32(avg($hatstats)) as count
        |from hattrick.match_details
        |where
        |(
        | (league_id = {main_league_id_0}) and (division_level = {main_division_level_1}) and
        | (cup_level = {main_cup_level_4}) and
        | (season >= {main_season_5}) and (round <= {main_round_6}) and
        | (not (season = 76 and round > 12))
        |) group by season, round
        |order by season asc, round asc
        |""".stripMargin.normalize())
  }
}
