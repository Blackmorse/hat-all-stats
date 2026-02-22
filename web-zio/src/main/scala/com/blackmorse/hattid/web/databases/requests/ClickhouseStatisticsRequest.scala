package com.blackmorse.hattid.web.databases.requests

import anorm.{Row, SimpleSql}
import com.blackmorse.hattid.web.databases.dao.RestClickhouseDAO
import com.blackmorse.hattid.web.databases.requests.ClickhouseRequest.*
import com.blackmorse.hattid.web.models.web.*
import sqlbuilder.{SqlBuilder, functions}
import zio.ZIO

trait ClickhouseStatisticsRequest[T] extends ClickhouseRequest[T] {
  val sortingColumns: Seq[String]

  def oneRoundBuilder(orderingKeyPath: OrderingKeyPath,
                      parameters: RestStatisticsParameters,
                      round: Int): SqlBuilder

  def aggregateBuilder(orderingKeyPath: OrderingKeyPath,
                       parameters: RestStatisticsParameters,
                       aggregateFunction: functions.func): SqlBuilder

  private def request(orderingKeyPath: OrderingKeyPath,
                       parameters: RestStatisticsParameters): SimpleSql[Row] = {
    (parameters.statsType match {
      case MultiplyRoundsType(_, func) => aggregateBuilder(orderingKeyPath, parameters, func)
      case Accumulate => aggregateBuilder(orderingKeyPath, parameters, functions.identity)
      case Round(round) => oneRoundBuilder(orderingKeyPath, parameters, round)
    }).sqlWithParameters().build
  }

  def execute(orderingKeyPath: OrderingKeyPath,
              parameters: RestStatisticsParameters): DBIO[List[T]] = wrapErrors {
    if (!sortingColumns.contains(parameters.sortBy)) {
      ZIO.fail(SqlInjectionError())
    } else {
      for {
        restClickhouseDAO <- ZIO.service[RestClickhouseDAO]
        result <- restClickhouseDAO.executeZIO(request(orderingKeyPath, parameters), rowParser)
      } yield result
    }
  }
}
