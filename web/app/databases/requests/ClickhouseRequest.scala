package databases.requests

import anorm.{Row, RowParser, SimpleSql}
import databases.SqlBuilder
import models.web.{MultiplyRoundsType, RestStatisticsParameters}

trait ClickhouseRequest[T] {
  val rowParser: RowParser[T]
}

trait ClickhouseStatisticsRequest[T] extends ClickhouseRequest[T] {
  val sortingColumns: Seq[String]

  protected def formRequest(baseSql: String,
                            formSql: String => String,
                            orderingKeyPath: OrderingKeyPath,
                            parameters: RestStatisticsParameters): SimpleSql[Row] = {
    if(!sortingColumns.contains(parameters.sortBy))
      throw new Exception("Looks like SQL injection")

    val sql = formSql(baseSql
      .replace("__sortBy__", parameters.sortBy))

    SqlBuilder(sql)
      .applyParameters(orderingKeyPath, parameters)
      .build
  }
}

trait AvgMaxRequest[T] extends ClickhouseStatisticsRequest[T] {
  val requestForAvgMax: String

  def avgMaxRequest(orderingKeyPath: OrderingKeyPath,
                    avgMaxType: MultiplyRoundsType,
                    parameters: RestStatisticsParameters): SimpleSql[Row] = {
    formRequest(requestForAvgMax,
      sql => sql.replace("__func__", avgMaxType.function),
      orderingKeyPath,
      parameters)
  }
}

trait AllRequest[T] extends ClickhouseStatisticsRequest[T] {
  val requestForAll: String

  def allRequest(orderingKeyPath: OrderingKeyPath,
                 parameters: RestStatisticsParameters): SimpleSql[Row] = {
    formRequest(requestForAll,
      identity,
      orderingKeyPath,
      parameters)
  }
}

trait RoundRequest[T] extends ClickhouseStatisticsRequest[T] {
  val requestForRound: String

  def roundRequest(orderingKeyPath: OrderingKeyPath,
                   parameters: RestStatisticsParameters): SimpleSql[Row] = {
    formRequest(requestForRound,
      identity,
      orderingKeyPath,
      parameters)
  }
}