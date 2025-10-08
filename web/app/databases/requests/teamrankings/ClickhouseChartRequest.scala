package databases.requests.teamrankings

import databases.dao.RestClickhouseDAO
import databases.requests.{ClickhouseRequest, OrderingKeyPath}
import databases.requests.ClickhouseRequest.DBIO
import databases.requests.model.Chart
import models.web.BadRequestError
import sqlbuilder.SqlBuilder
import zio.ZIO

abstract class ClickhouseChartRequest[T <: Chart] extends ClickhouseRequest[T] {
  def sqlBuilder(orderingKeyPath: OrderingKeyPath, season: Int): SqlBuilder

  def execute(orderingKeyPath: OrderingKeyPath, season: Int): DBIO[List[T]] = wrapErrors {
    for {
      restClickhouseDAO <- ZIO.service[RestClickhouseDAO]
      _                 <- if (orderingKeyPath.isLeagueUnitLevel) ZIO.unit
                            else ZIO.fail(BadRequestError("Only league level is supported"))
      result <- restClickhouseDAO.executeZIO(sqlBuilder(orderingKeyPath, season).sqlWithParameters().build, rowParser)
    } yield result
  }
}
