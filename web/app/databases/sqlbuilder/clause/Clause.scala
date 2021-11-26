package databases.sqlbuilder.clause

import databases.requests.OrderingKeyPath
import databases.sqlbuilder.{ConditionParameter, DateParameter, IntParameter, LongParameter, Parameter, SqlBuilder, StringParameter, ValueParameter}
import models.web.RestStatisticsParameters

import scala.collection.mutable

object Clause {
  implicit def sqlBuilder(clause: Clause): SqlBuilder = clause.sqlBuilder
}

object ClauseEntry {
  implicit def sqlBuilder(clauseEntry: ClauseEntry): SqlBuilder = clauseEntry.sqlBuilder
  implicit def clause(clauseEntry: ClauseEntry): Clause = clauseEntry.sqlBuilder.whereClause
}

case class ClauseEntry(sqlBuilder: SqlBuilder) {
  private[sqlbuilder] val parameters: mutable.Buffer[Parameter] = mutable.Buffer()
//rename
  def orderingKeyPath(orderingKeyPath: OrderingKeyPath): this.type = {
    leagueId(orderingKeyPath.leagueId)
    divisionLevel(orderingKeyPath.divisionLevel)
    leagueUnitId(orderingKeyPath.leagueUnitId)
    teamId(orderingKeyPath.teamId)

    this
  }

  private def addParameter[T <: Parameter](parameter: T): T = {
    sqlBuilder.parametersNumber += 1
    parameters += parameter
    parameter
  }

  def season = addParameter(IntParameter(sqlBuilder.parametersNumber, "season", this, sqlBuilder.name))

  def leagueId = addParameter(IntParameter(sqlBuilder.parametersNumber, "league_id", this, sqlBuilder.name))

  def divisionLevel = addParameter(IntParameter(sqlBuilder.parametersNumber, "division_level", this, sqlBuilder.name))

  def leagueUnitId = addParameter(LongParameter(sqlBuilder.parametersNumber, "league_unit_id", this, sqlBuilder.name))

  def teamId = addParameter(LongParameter(sqlBuilder.parametersNumber, "team_id", this, sqlBuilder.name))

  def round = addParameter(IntParameter(sqlBuilder.parametersNumber, "round", this, sqlBuilder.name))

  def role = addParameter(IntParameter(sqlBuilder.parametersNumber, "role", this, sqlBuilder.name))

  def nationality = addParameter( IntParameter(sqlBuilder.parametersNumber, "nationality", this, sqlBuilder.name))

  def age = addParameter(IntParameter(sqlBuilder.parametersNumber, "age", this, sqlBuilder.name))

  def playedMinutes = addParameter(IntParameter(sqlBuilder.parametersNumber, "played_minutes", this, sqlBuilder.name))

  def foundedDate = addParameter(DateParameter(sqlBuilder.parametersNumber, "founded_date", this, sqlBuilder.name))

  def rankType = addParameter(StringParameter(sqlBuilder.parametersNumber, "rank_type", this, sqlBuilder.name))

  def and(condition: String): ClauseEntry = {
    parameters += ConditionParameter(condition)
    this
  }

  def and(conditionOpt: Option[String]): ClauseEntry = {
    conditionOpt.foreach(condition => parameters += ConditionParameter(condition))
    this
  }

  def isLeagueMatch: ClauseEntry = {
    val cupLevelParameter = IntParameter(sqlBuilder.parametersNumber, "cup_level", this, sqlBuilder.name)
    cupLevelParameter(0)
    addParameter(cupLevelParameter)
    this
  }

  def createSql: Option[String] = {
    val whereParameters = parameters.filter(parameter => {
      parameter match {
        case ConditionParameter(_) => true
        case _ => parameter.asInstanceOf[ValueParameter].value != null
      }
    })
    if(whereParameters.nonEmpty) {
      Some(
        whereParameters.map {
          case ConditionParameter(condition) => s"($condition)"
          case parameter =>
            val valueParameter = parameter.asInstanceOf[ValueParameter]
            s"(${valueParameter.name} ${valueParameter.oper} {${sqlBuilder.name}_${valueParameter.name}_${valueParameter.parameterNumber}})"
        }.mkString(" AND ")
      )
    } else {
      None
    }
  }
}

abstract class Clause(val sqlBuilder: SqlBuilder, val clauseType: String) {
  val entries = mutable.Buffer[ClauseEntry]()

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

  private[sqlbuilder] def initialized: Boolean = entries.nonEmpty

  override def toString: String = {
    val definedEntries = entries.map(_.createSql).filter(_.isDefined)
    if (definedEntries.isEmpty) return ""
    val ors = definedEntries.map(_.get).mkString("(", ") OR (", ")")
    s"$clauseType $ors"
  }
}

class WhereClause(sqlBuilder: SqlBuilder) extends Clause(sqlBuilder, "WHERE")

class HavingClause(sqlBuilder: SqlBuilder) extends Clause(sqlBuilder, "HAVING")

