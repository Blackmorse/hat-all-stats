package databases.requests.playerstats.player.stats

import anorm.{Row, SimpleSql}
import databases.dao.RestClickhouseDAO
import databases.requests.model.Roles
import databases.requests.{ClickhouseRequest, OrderingKeyPath}
import models.web.{HattidError, PlayersParameters, RestStatisticsParameters, Round, SqlInjectionError}
import sqlbuilder.SqlBuilder
import zio.{IO, ZIO}
import ClickhouseRequest.*

import scala.concurrent.Future

trait ClickhousePlayerStatsRequest[T] extends ClickhouseRequest[T] {
  val sortingColumns: Seq[String]

  private def simpleSql(orderingKeyPath: OrderingKeyPath,
                parameters: RestStatisticsParameters,
                playersParameters: PlayersParameters): IO[SqlInjectionError, SimpleSql[Row]] = {
    if (!sortingColumns.contains(parameters.sortBy)) {
      ZIO.fail(SqlInjectionError())
    } else {
      val round = parameters.statsType match {
        case Round(r) => r
      }

      // A bit complicated
      // But that's:
      // in case the role is not provided => return None role
      // in case role is provided but invalid => return SqlInjectionError
      for {
        roleCandidate <- ZIO.succeed(playersParameters.role)
        role <- roleCandidate match {
          case Some(roleString) =>
            ZIO.fromOption(Roles.of(roleString))
              .mapError(_ => SqlInjectionError())
              .map(r => Some(r.name))
          case None => ZIO.succeed(None)
        }
        } yield buildSql(orderingKeyPath = orderingKeyPath,
            parameters = parameters,
            playersParameters = playersParameters,
            role = role,
            round = round).sqlWithParameters().build
    }
  }

  def executeZIO(orderingKeyPath: OrderingKeyPath,
                 parameters: RestStatisticsParameters,
                 playersParameters: PlayersParameters)(implicit restClickhouseDAO: RestClickhouseDAO): IO[HattidError, List[T]] = {
    (for {
      simpleSql <- simpleSql(orderingKeyPath, parameters, playersParameters)
      result <- restClickhouseDAO.executeZIO(simpleSql, rowParser)
    } yield result).hattidErrors
  }
  
  // TODO remove in favor of ZIO
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
