package databases.requests.playerstats.player.stats

import anorm.{Row, SimpleSql}
import databases.dao.RestClickhouseDAO
import databases.requests.ClickhouseRequest.*
import databases.requests.model.Roles
import databases.requests.{ClickhouseRequest, OrderingKeyPath}
import models.web.{PlayersParameters, RestStatisticsParameters, Round, SqlInjectionError}
import sqlbuilder.SqlBuilder
import zio.{IO, ZIO}

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

  def execute(orderingKeyPath: OrderingKeyPath,
              parameters: RestStatisticsParameters,
              playersParameters: PlayersParameters): DBIO[List[T]] = wrapErrors {
    for {
      restClickhouseDAO <- ZIO.service[RestClickhouseDAO]
      simpleSql <- simpleSql(orderingKeyPath, parameters, playersParameters)
      result <- restClickhouseDAO.executeZIO(simpleSql, rowParser)
    } yield result
  }

  def buildSql(orderingKeyPath: OrderingKeyPath,
               parameters: RestStatisticsParameters,
               playersParameters: PlayersParameters,
               role: Option[String],
               round: Int): SqlBuilder
}
