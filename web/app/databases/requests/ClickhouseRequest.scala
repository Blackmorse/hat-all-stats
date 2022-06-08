package databases.requests

import anorm.{NamedParameter, ParameterValue, Row, RowParser, SQL, SimpleSql, ToParameterValue}
import databases.requests.model.Roles
import sqlbuilder.{DateParameter, IntParameter, LongParameter, SqlWithParameters, StringParameter, ValueParameter}
import sqlbuilder.clause.ClauseEntry


trait ClickhouseRequest[T] {
  val rowParser: RowParser[T]
}

object ClickhouseRequest {
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

    implicit class SqlWithParametersExtended(sqlWithParameters: SqlWithParameters) {
      def build: SimpleSql[Row] = {
        SQL(sqlWithParameters.sql)
          .on(sqlWithParameters.parameters.toSeq
            //TODO match with subtypes, (use .collect but not .filter)
            .filter(parameter => parameter.isInstanceOf[ValueParameter[Any]])
            .map(_.asInstanceOf[ValueParameter[Any]])
            .map(parameter => {
              val parameterValue = parameter match {
                case i @ IntParameter(_, _, _, _) => i.value: ParameterValue
                case l @ LongParameter(_, _, _, _) => l.value: ParameterValue
                case s @ StringParameter(_, _, _, _) => s.value: ParameterValue
                case d @ DateParameter(_, _, _, _) => d.value: ParameterValue
              }

              NamedParameter.namedWithString((s"${parameter.sqlBuilderName}_${parameter.name}_${parameter.parameterNumber}", parameterValue))
            }): _*
          )
      }
    }
  }
}