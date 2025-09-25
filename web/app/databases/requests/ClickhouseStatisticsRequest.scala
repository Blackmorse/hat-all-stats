package databases.requests

import anorm.{Row, SimpleSql}
import databases.dao.RestClickhouseDAO
import io.github.gaelrenoux.tranzactio.DbException
import models.web.{Accumulate, DbError, HattidError, MultiplyRoundsType, RestStatisticsParameters, Round, SqlInjectionError}
import sqlbuilder.{SqlBuilder, functions}
import zio.{IO, ZIO}
import ClickhouseRequest._

import scala.concurrent.Future

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

  //TODO remove in favor of ZIO
  def execute(orderingKeyPath: OrderingKeyPath,
              parameters: RestStatisticsParameters)
             (implicit restClickhouseDAO: RestClickhouseDAO): Future[List[T]] = {
    if(!sortingColumns.contains(parameters.sortBy))
      throw new Exception("Looks like SQL injection")

    restClickhouseDAO.execute(request(orderingKeyPath, parameters), rowParser)
  }

  def executeZIO(orderingKeyPath: OrderingKeyPath,
              parameters: RestStatisticsParameters)
             (implicit restClickhouseDAO: RestClickhouseDAO): IO[HattidError, List[T]] = {
    if (!sortingColumns.contains(parameters.sortBy)) {
      ZIO.fail(SqlInjectionError())
    } else {
      restClickhouseDAO.executeZIO(request(orderingKeyPath, parameters), rowParser)
    }.hattidErrors
  }
}
