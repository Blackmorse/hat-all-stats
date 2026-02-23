package com.blackmorse.hattid.web.databases.requests.teamrankings

import com.blackmorse.hattid.web.databases.dao.RestClickhouseDAO
import com.blackmorse.hattid.web.databases.requests.{ClickhouseRequest, OrderingKeyPath}
import com.blackmorse.hattid.web.databases.requests.ClickhouseRequest.DBIO
import com.blackmorse.hattid.web.databases.requests.model.Chart
import com.blackmorse.hattid.web.models.web.BadRequestError
import sqlbuilder.SqlBuilder
import zio.ZIO

abstract class ClickhouseChartRequest[T <: Chart] extends ClickhouseRequest[T] {
  def sqlBuilder(orderingKeyPath: OrderingKeyPath, season: Int): SqlBuilder

  def execute(orderingKeyPath: OrderingKeyPath, season: Int): DBIO[List[T]] = wrapErrors {
    for {
      _                 <- ZIO.unless(orderingKeyPath.isLeagueUnitLevel)(ZIO.fail(BadRequestError("Only league level is supported")))
      result            <- RestClickhouseDAO.executeZIO(sqlBuilder(orderingKeyPath, season).sqlWithParameters().build, rowParser)
    } yield result
  }
}
