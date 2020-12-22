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
      .map(_.name)
    val builder = if(sql.contains("__having__")) {

      SqlBuilder(sql)
        .applyParameters(parameters)
        .where
          .applyParameters(orderingKeyPath)
        .having
          .role(role.map(Roles.reverseMapping))
          .nationality(playersParameters.nationality)
          .age.greaterEqual(playersParameters.minAge.map(_ * 112))
          .age.lessEqual(playersParameters.maxAge.map(_ * 112 + 111))
        .build
    } else {
      SqlBuilder(sql)
        .applyParameters(parameters)
        .where
          .applyParameters(orderingKeyPath)
          .role(role.map(Roles.reverseMapping))
          .nationality(playersParameters.nationality)
          .age.greaterEqual(playersParameters.minAge.map(_ * 112))
          .age.lessEqual(playersParameters.maxAge.map(_ * 112 + 111))
        .build
    }

    restClickhouseDAO.execute(builder, rowParser)
  }
}
