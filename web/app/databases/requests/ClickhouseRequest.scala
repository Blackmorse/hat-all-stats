package databases.requests

import anorm.{NamedParameter, ParameterValue, Row, RowParser, SQL, SimpleSql, ToParameterValue}
import databases.ClickhousePool.ClickhousePool
import databases.dao.RestClickhouseDAO
import databases.requests.ClickhouseRequest.DBIO
import databases.requests.model.Roles
import io.github.gaelrenoux.tranzactio.DbException
import models.web.{DbError, HattidError, SqlInjectionError}
import sqlbuilder.{DateParameter, IntParameter, LongParameter, SqlWithParameters, StringParameter, ValueParameter}
import sqlbuilder.clause.ClauseEntry
import zio.{IO, ZIO, ZLayer}


trait ClickhouseRequest[T] {
  val rowParser: RowParser[T]

  def wrapErrorsOpt(zio: ZIO[ClickhousePool & RestClickhouseDAO, Throwable | SqlInjectionError, Option[T]]): DBIO[Option[T]] = {
    zio mapError {
      case sqlInjectionError: SqlInjectionError => sqlInjectionError
      case ex: DbException => DbError(ex)
      case t: Throwable => DbError(t)
    }
  }
  
  def wrapErrors(zio: ZIO[ClickhousePool & RestClickhouseDAO, Throwable | HattidError, List[T]]): DBIO[List[T]] = {
    zio mapError {
      case hattidError: HattidError => hattidError
      case ex: DbException => DbError(ex)
      case t: Throwable => DbError(t)
    }
  }
}

object ClickhouseRequest {
  type DBIO[T] = ZIO[ClickhousePool & RestClickhouseDAO, HattidError, T]

  implicit class HattidDBZIO[Result](zio: ZIO[RestClickhouseDAO, Throwable | SqlInjectionError, Result]) {
    def hattidErrors: DBIO[Result] = zio.mapError {
      case sqlInjectionError: SqlInjectionError => sqlInjectionError
      case ex: io.github.gaelrenoux.tranzactio.DbException => DbError(ex)
      case t: Throwable => DbError(t)
    }
  }

  def roleIdCase(fieldName: String): String = {
    val rolesList = (for(role <- Roles.all;
        id <- role.htIds) yield s"$id, ${role.internalId},")
      .mkString("\n")

    s"""
       |caseWithExpression($fieldName,
       |$rolesList
       |0)""".stripMargin
  }

  object implicits {
    implicit class ClauseEntryExtended(clauseEntry: ClauseEntry) {
      def orderingKeyPath(orderingKeyPath: OrderingKeyPath): ClauseEntry = {
        clauseEntry.leagueId(orderingKeyPath.leagueId)
        clauseEntry.divisionLevel(orderingKeyPath.divisionLevel)
        clauseEntry.leagueUnitId(orderingKeyPath.leagueUnitId)
        clauseEntry.teamId(orderingKeyPath.teamId)
      }
    }

//    implicit class SqlWithParametersExtended(sqlWithParameters: SqlWithParameters) {
//      def build: SimpleSql[Row] = {
//        SQL(sqlWithParameters.sql)
//          .on(sqlWithParameters.parameters.toSeq
//            //TODO match with subtypes, (use .collect but not .filter)
//            .filter(parameter => parameter.isInstanceOf[ValueParameter[Any]])
//            .map(_.asInstanceOf[ValueParameter[Any]])
//            .map(parameter => {
//              val parameterValue = parameter match {
//                case i @ IntParameter(_, _, _, _) => i.value: ParameterValue
//                case l @ LongParameter(_, _, _, _) => l.value: ParameterValue
//                case s @ StringParameter(_, _, _, _) => s.value: ParameterValue
//                case d @ DateParameter(_, _, _, _) => d.value: ParameterValue
//              }
//
//              NamedParameter.namedWithString((s"${parameter.sqlBuilderName}_${parameter.name}_${parameter.parameterNumber}", parameterValue))
//            }): _*
//          )
//      }
//    }
  }
}