package databases.sqlbuilder.clause

import anorm.{ParameterValue, Row, SimpleSql}
import databases.requests.OrderingKeyPath
import databases.sqlbuilder.{DateParameter, IntParameter, LongParameter, Parameter, SqlBuilder, StringParameter}
import models.web.RestStatisticsParameters

import scala.collection.mutable

object Clause {
  implicit def sqlBuilder(clause: Clause): SqlBuilder = clause.sqlBuilder
}

object ClauseEntry {
  implicit def sqlBuilder(clauseEntry: ClauseEntry): SqlBuilder = clauseEntry.sqlBuilder
  implicit def clause(clauseEntry: ClauseEntry): Clause = clauseEntry.sqlBuilder.whereClause
}

class ClauseEntry(val sqlBuilder: SqlBuilder) {
  private[sqlbuilder] val parameters: mutable.Buffer[Parameter] = mutable.Buffer()
  private[sqlbuilder] val params: mutable.Buffer[(String, ParameterValue)] = mutable.Buffer()

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

  def rankType = addParameter(StringParameter(sqlBuilder.parametersNumber, "rank_type", this))

  def createSql: Option[String] = {
    val whereParameters = parameters.filter(_.value != null)
    if(whereParameters.nonEmpty) {
      Some(
        whereParameters.map(parameter => s"${parameter.name} ${parameter.oper} {${parameter.name}_${parameter.parameterNumber}}").mkString(" AND ")
      )
    } else {
      None
    }
  }
}

abstract class Clause(val sqlBuilder: SqlBuilder, val clauseType: String) {
  private val entries = mutable.Buffer[ClauseEntry]()

  def create(): ClauseEntry = {
    if (entries.nonEmpty) throw new RuntimeException("Unable to Initialize Clause twice!")
    or
  }

  def or: ClauseEntry = {
    val entry = new ClauseEntry(sqlBuilder)
    entries += entry
    entry
  }

  private[databases] def parameters = entries.flatMap(_.parameters)

  override def toString: String = {
    val definedEntries = entries.map(_.createSql).filter(_.isDefined)
    if (definedEntries.isEmpty) return ""
    val ors = definedEntries.map(_.get).mkString("(", ") OR (", ")")
    s"$clauseType $ors"
  }
}

class WhereClause(sqlBuilder: SqlBuilder) extends Clause(sqlBuilder, "WHERE")

class HavingClause(sqlBuilder: SqlBuilder) extends Clause(sqlBuilder, "HAVING")

