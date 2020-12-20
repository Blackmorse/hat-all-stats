package databases.requests.playerstats.player

import databases.requests.model.Roles
import databases.{RestClickhouseDAO, SqlBuilder}
import databases.requests.{ClickhouseRequest, OrderingKeyPath}
import models.web.{PlayersParameters, RestStatisticsParameters, Round}

import scala.concurrent.Future

trait ClickhousePlayerRequest[T] extends ClickhouseRequest[T] {
  val sortingColumns: Seq[String]

  val oneRoundSql: String

  def execute(orderingKeyPath: OrderingKeyPath,
              parameters: RestStatisticsParameters,
              playersParameters: PlayersParameters)(implicit restClickhouseDAO: RestClickhouseDAO): Future[List[T]] = {
    val sortBy = parameters.sortBy
    if(!sortingColumns.contains(sortBy))
      throw new Exception("Looks like SQL injection")

    val sql = parameters.statsType match {
      case Round(round) => oneRoundSql.replace("__round__", round.toString).replace("__sortBy__", sortBy)
    }

    val role = playersParameters.role.map(roleString => Roles.of(roleString).getOrElse(throw new RuntimeException("Looks like SQL injection")))
    val builder = if(sql.contains("__having__")) {

      SqlBuilder(sql)
        .applyParameters(orderingKeyPath)
        .applyParameters(parameters)
        .having
          .role(role)
          .nationality(playersParameters.nationality)
        .build
    } else {
      SqlBuilder(sql)
        .applyParameters(orderingKeyPath)
        .applyParameters(parameters)
        .where
          .role(role)
          .nationality(playersParameters.nationality)
        .build
    }

    restClickhouseDAO.execute(builder, rowParser)
  }
}
