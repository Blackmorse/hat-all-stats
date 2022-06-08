package databases.requests

import databases.dao.RestClickhouseDAO
import databases.requests.ClickhouseRequest.implicits.SqlWithParametersExtended
import models.web.{Accumulate, MultiplyRoundsType, RestStatisticsParameters, Round}
import sqlbuilder.{SqlBuilder, functions}

import scala.concurrent.Future

trait ClickhouseStatisticsRequest[T] extends ClickhouseRequest[T] {
  val sortingColumns: Seq[String]

  def oneRoundBuilder(orderingKeyPath: OrderingKeyPath,
                      parameters: RestStatisticsParameters,
                      round: Int): SqlBuilder

  def aggregateBuilder(orderingKeyPath: OrderingKeyPath,
                       parameters: RestStatisticsParameters,
                       aggregateFuntion: functions.func): SqlBuilder

  def execute(orderingKeyPath: OrderingKeyPath,
              parameters: RestStatisticsParameters)
             (implicit restClickhouseDAO: RestClickhouseDAO): Future[List[T]] = {
    val sortBy = parameters.sortBy
    if(!sortingColumns.contains(sortBy))
      throw new Exception("Looks like SQL injection")

    val sqlBuilder = parameters.statsType match {
      case MultiplyRoundsType(_, func) => aggregateBuilder(orderingKeyPath, parameters, func)
      case Accumulate => aggregateBuilder(orderingKeyPath, parameters, functions.identity)
      case Round(round) => oneRoundBuilder(orderingKeyPath, parameters, round)
    }

    restClickhouseDAO.execute(sqlBuilder.sqlWithParameters().build, rowParser)
  }
}
