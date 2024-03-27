package databases.requests.playerstats.player.stats

import databases.dao.RestClickhouseDAO
import databases.requests.model.Roles
import databases.requests.{ClickhouseRequest, OrderingKeyPath}
import models.web.{PlayersParameters, RestStatisticsParameters, Round}
import sqlbuilder.SqlBuilder
import databases.dao.SqlBuilderParameters

import scala.concurrent.Future

trait ClickhousePlayerStatsRequest[T] extends ClickhouseRequest[T] {
  val sortingColumns: Seq[String]

  def execute(orderingKeyPath: OrderingKeyPath,
              parameters: RestStatisticsParameters,
              playersParameters: PlayersParameters)(implicit restClickhouseDAO: RestClickhouseDAO): Future[List[T]] = {
    if (!sortingColumns.contains(parameters.sortBy))
      throw new Exception("Looks like SQL injection")

    val round = parameters.statsType match {
      case Round(r) => r
    }
    val role = playersParameters.role.map(roleString => Roles.of(roleString).getOrElse(throw new RuntimeException("Looks like SQL injection")))
      .map(_.name)

    val builder = buildSql(orderingKeyPath = orderingKeyPath,
      parameters = parameters,
      playersParameters = playersParameters,
      role = role,
      round = round)
    restClickhouseDAO.execute(builder.sqlWithParameters().build, rowParser)
  }

  def buildSql(orderingKeyPath: OrderingKeyPath,
               parameters: RestStatisticsParameters,
               playersParameters: PlayersParameters,
               role: Option[String],
               round: Int): SqlBuilder
}
