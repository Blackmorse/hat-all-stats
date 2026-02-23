package com.blackmorse.hattid.web.databases.requests

import com.blackmorse.hattid.web.databases.dao.RestClickhouseDAO
import com.blackmorse.hattid.web.databases.requests.ClickhouseRequest.DBIO
import sqlbuilder.SqlBuilder
import zio.ZIO

trait ClickhouseOverviewRequest[T] extends ClickhouseRequest[T] {
  val limit = 5

  protected def builder(season: Int, round: Int, leagueId: Option[Int], divisionLevel: Option[Int]): SqlBuilder
  
  def executeZio(season: Int, round: Int, leagueId: Option[Int], divisionLevel: Option[Int]): DBIO[List[T]] = wrapErrors {
      RestClickhouseDAO.executeZIO(builder(season, round, leagueId, divisionLevel).sqlWithParameters().build, rowParser)
  }
}
