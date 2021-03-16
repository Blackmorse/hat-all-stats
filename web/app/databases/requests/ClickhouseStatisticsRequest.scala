package databases.requests

import databases.SqlBuilder
import databases.dao.RestClickhouseDAO
import models.web.{Accumulate, MultiplyRoundsType, RestStatisticsParameters, Round}

import scala.concurrent.Future

trait ClickhouseStatisticsRequest[T] extends ClickhouseRequest[T] {
  val sortingColumns: Seq[String]

  val aggregateSql: String

  val oneRoundSql: String

  def execute(orderingKeyPath: OrderingKeyPath,
              parameters: RestStatisticsParameters)
             (implicit restClickhouseDAO: RestClickhouseDAO): Future[List[T]] = {
    val sortBy = parameters.sortBy
    if(!sortingColumns.contains(sortBy))
      throw new Exception("Looks like SQL injection")

    val sql = parameters.statsType match {
      case MultiplyRoundsType(func) => aggregateSql.replace("__func__", func)
      case Accumulate  => aggregateSql
      case Round(_) => oneRoundSql
    }

    val round = parameters.statsType match {
      case Round(r) => Some(r)
      case _ => None
    }

    restClickhouseDAO.execute(SqlBuilder(sql)
      .where
        .applyParameters(parameters)
        .applyParameters(orderingKeyPath)
        .round(round)
      .sortBy(sortBy)
      .build, rowParser)
  }
}