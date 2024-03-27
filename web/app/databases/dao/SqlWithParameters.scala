package databases.dao

import anorm.{NamedParameter, ParameterValue, Row, SQL, SimpleSql, on}
import sqlbuilder.{DateParameter, IntParameter, LongParameter, Parameter, SqlBuilder, StringParameter, ValueParameter}


implicit class SqlBuilderParameters(sqlBuilder: SqlBuilder) {
  def sqlWithParameters(): SqlWithParameters = {
    val str = sqlBuilder.buildStringSql()
    SqlWithParameters(
      sql = str,
      parameters = sqlBuilder.parameters.toSeq
    )
  }
}



case class SqlWithParameters(sql: String, parameters: Seq[Parameter]) {
  def build: SimpleSql[Row] = {
    SQL(sql)
      .on(parameters
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