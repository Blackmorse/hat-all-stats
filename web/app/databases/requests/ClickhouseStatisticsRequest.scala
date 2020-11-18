package databases.requests

import databases.{RestClickhouseDAO, SqlBuilder}
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
      case MultiplyRoundsType(func) => aggregateSql.replace("__func__", func).replace("__sortBy__", sortBy)
      case Accumulate => aggregateSql.replace("__sortBy__", sortBy)
      case Round(round) => oneRoundSql.replace("__round__", round.toString).replace("__sortBy__", sortBy)
    }

    restClickhouseDAO.execute(SqlBuilder(sql)
      .applyParameters(orderingKeyPath)
      .applyParameters(parameters)
      .build, rowParser)
  }
}