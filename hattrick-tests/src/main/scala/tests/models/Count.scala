package tests.models

import anorm.SqlParser.get
import anorm.{RowParser, ~}

case class Count(leagueId: Int, divisionLevel: Int, round: Int, cnt: Long)

object Count {
  val mapper: RowParser[Count] = {
    get[Int]("league_id") ~
    get[Int]("division_level") ~
    get[Int]("round") ~
    get[Long]("cnt") map { case leagueId ~ divisionLevel ~ round ~ cnt  =>
      Count(leagueId, divisionLevel, round, cnt)
    }
  }
}