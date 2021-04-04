package databases.sqlbuilder

import anorm.{NamedParameter, ParameterValue, Row, SQL, SimpleSql}
import databases.requests.OrderingKeyPath
import models.web.{Asc, Desc, RestStatisticsParameters, SortingDirection}

import scala.collection.mutable

object Clause {
  implicit def sqlBuilder(clause: Clause): SqlBuilder = clause.sqlBuilder
}

abstract class Clause(val sqlBuilder: SqlBuilder) {
  private[databases] val parameters: mutable.Buffer[Parameter] = mutable.Buffer()
  private[databases] val params: mutable.Buffer[(String, ParameterValue)] = mutable.Buffer()

  def applyParameters(orderingKeyPath: OrderingKeyPath): this.type = {
    leagueId(orderingKeyPath.leagueId)
    divisionLevel(orderingKeyPath.divisionLevel)
    leagueUnitId(orderingKeyPath.leagueUnitId)
    teamId(orderingKeyPath.teamId)

    this
  }

  def applyParameters(parameters: RestStatisticsParameters): this.type = {
    season(parameters.season)
    sqlBuilder.page(parameters.page)
    sqlBuilder.pageSize(parameters.pageSize)
    sqlBuilder.sortingDirection(parameters.sortingDirection)

    this
  }

  private def addParameter[T <: Parameter](parameter: T): T = {
    sqlBuilder.parametersNumber += 1
    parameters += parameter
    parameter
  }

  def season = addParameter(IntParameter(sqlBuilder.parametersNumber, "season", this))

  def leagueId = addParameter(IntParameter(sqlBuilder.parametersNumber, "league_id", this))

  def divisionLevel = addParameter(IntParameter(sqlBuilder.parametersNumber, "division_level", this))

  def leagueUnitId = addParameter(LongParameter(sqlBuilder.parametersNumber, "league_unit_id", this))

  def teamId = addParameter(LongParameter(sqlBuilder.parametersNumber, "team_id", this))

  def round = addParameter(IntParameter(sqlBuilder.parametersNumber, "round", this))

  def role = addParameter(IntParameter(sqlBuilder.parametersNumber, "role", this))

  def nationality = addParameter( IntParameter(sqlBuilder.parametersNumber, "nationality", this))

  def age = addParameter(IntParameter(sqlBuilder.parametersNumber, "age", this))

  def playedMinutes = addParameter(IntParameter(sqlBuilder.parametersNumber, "played_minutes", this))

  def foundedDate = addParameter(DateParameter(sqlBuilder.parametersNumber, "founded_date", this))

  def and: SqlBuilder = sqlBuilder

  def build: SimpleSql[Row] = sqlBuilder.build

  def where = sqlBuilder.where

  def having = sqlBuilder.having
}

class WhereClause(sqlBuilder: SqlBuilder) extends Clause(sqlBuilder)

class HavingClause(sqlBuilder: SqlBuilder) extends Clause(sqlBuilder)

case class SqlBuilder(baseSql: String) {
  var parametersNumber = 0
  private var page = 0
  private var pageSize = 16
  private var sortingDirection: String = "desc"
  private var sortBy: Option[String] = None
  private val whereClause = new WhereClause(this)
  private val havingClause = new HavingClause(this)

  def where = whereClause
  def having = havingClause


  def page(page: Int): SqlBuilder = {
    this.page = page
    this
  }

  def pageSize(pageSize: Int): SqlBuilder = {
    this.pageSize = pageSize
    this
  }

  def sortBy(sb: String): SqlBuilder = {
    this.sortBy = Some(sb)
    this
  }

  def sortingDirection(direction: SortingDirection): SqlBuilder = {
    this.sortingDirection = direction match {
      case Desc => "desc"
      case Asc => "asc"
    }
    this
  }


  def build: SimpleSql[Row] = {
    val whereParameters = where.parameters.filter(_.value != null)
    val sql =  if(whereParameters.nonEmpty) {
      val wheresql = " WHERE " + whereParameters.map(parameter => s"${parameter.name} ${parameter.oper} {${parameter.name}_${parameter.parameterNumber}}").mkString(" AND ")
      baseSql.replace("__where__", wheresql)
    } else {
      baseSql.replace("__where__", " ")
    }

    val havingParameters = having.parameters.filter(_.value != null)
    val hSql = if(havingParameters.nonEmpty) {
      val havingSql = " HAVING " + havingParameters.map(parameter => s"${parameter.name} ${parameter.oper} {${parameter.name}_${parameter.parameterNumber}}").mkString(" AND ")
      sql.replace("__having__", havingSql)
    } else {
      sql.replace("__having__", " ")
    }

    val result = hSql
      .replace("__limit__", s" limit ${page * pageSize}, ${pageSize + 1}")
      .replace("__sortingDirection__", sortingDirection)

    val finalResult = this.sortBy.map(sb => result.replace("__sortBy__", sb)).getOrElse(result)

    SQL(finalResult)
      .on((where.parameters ++ having.parameters).map(parameter => NamedParameter.namedWithString((s"${parameter.name}_${parameter.parameterNumber}", parameter.value))): _*)
  }
}