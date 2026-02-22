package com.blackmorse.hattid.web.models.clickhouse

import anorm.SqlParser.get
import anorm.{RowParser, ~}

case class HistoryInfo(season: Int, leagueId: Int, round: Int, divisionLevel: Int, count: Int)

object HistoryInfo {
  val mapper: RowParser[HistoryInfo] = {
    get[Int]("season") ~
    get[Int]("league_id") ~
    get[Int]("division_level") ~
    get[Int]("round") ~
    get[Int]("cnt") map {
      case season ~ leagueId ~ divisionLevel ~ round ~ cnt =>
        HistoryInfo(season, leagueId, round, divisionLevel, cnt)
    }
  }
}
