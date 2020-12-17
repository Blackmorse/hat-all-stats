package databases.requests.playerstats.player

import databases.requests.model.Roles
import databases.{RestClickhouseDAO, SqlBuilder}
import databases.requests.{ClickhouseRequest, OrderingKeyPath}
import models.web.{RestStatisticsParameters, Round}

import scala.concurrent.Future

trait ClickhousePlayerRequest[T] extends ClickhouseRequest[T] {
  val sortingColumns: Seq[String]

  val oneRoundSql: String

  def execute(orderingKeyPath: OrderingKeyPath,
              parameters: RestStatisticsParameters,
              role: String)(implicit restClickhouseDAO: RestClickhouseDAO): Future[List[T]] = {
    val sortBy = parameters.sortBy
    if(!sortingColumns.contains(sortBy))
      throw new Exception("Looks like SQL injection")

    val sql = parameters.statsType match {
      case Round(round) => oneRoundSql.replace("__round__", round.toString).replace("__sortBy__", sortBy)
    }

    val roleOpt = Roles.of(role)
    val actualRole = roleOpt.getOrElse(throw new RuntimeException("Looks like SQL injection"))
    val builder = if(sql.contains("__having__")) {
      val actualSql = if(actualRole.name == "none") {
          sql.replace("__having__", "")
        } else {
          sql.replace("__having__", s"HAVING role = '${actualRole.name}'")
        }
      SqlBuilder(actualSql)
        .applyParameters(orderingKeyPath)
        .applyParameters(parameters)
        .build
    } else {
      SqlBuilder(sql)
        .applyParameters(orderingKeyPath)
        .applyParameters(parameters)
        .role(actualRole)
        .build
    }

    restClickhouseDAO.execute(builder, rowParser)
  }
}
