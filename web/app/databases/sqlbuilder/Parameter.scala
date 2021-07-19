package databases.sqlbuilder

import anorm.ParameterValue
import databases.sqlbuilder.clause.ClauseEntry

import java.text.SimpleDateFormat
import java.util.Date

trait Parameter

trait ValueParameter extends Parameter {
  val parameterNumber: Int
  val name: String
  def oper: String
  def value: ParameterValue
}

case class ConditionParameter(condition: String) extends Parameter

case class IntParameter(parameterNumber: Int, name: String, clauseEntry: ClauseEntry) extends ValueParameter {
  private[sqlbuilder] var _oper: String = "="
  private[sqlbuilder] var _value: ParameterValue = _

  def apply(value: Int): ClauseEntry = {
    this._value = value
    clauseEntry
  }

  def apply(valueOpt: Option[Int]): ClauseEntry = {
    valueOpt.foreach(value => this._value = value)
    clauseEntry
  }

  def greaterEqual(value: Int): ClauseEntry = {
    this._value = value
    this._oper = ">="
    clauseEntry
  }

  def greaterEqual(valueOpt: Option[Int]): ClauseEntry = {
    valueOpt.foreach(value => this._value = value)
    this._oper = ">="
    clauseEntry
  }

  def lessEqual(value: Int): ClauseEntry = {
    this._value = value
    this._oper = "<="
    clauseEntry
  }

  def lessEqual(valueOpt: Option[Int]): ClauseEntry = {
    valueOpt.foreach(value => this._value = value)
    this._oper = "<="
    clauseEntry
  }

  override def oper: String = _oper
  override def value: ParameterValue = _value
}

case class LongParameter(parameterNumber: Int, name: String, clauseEntry: ClauseEntry) extends ValueParameter {
  var _oper: String = "="
  var _value: ParameterValue = _

  def apply(value: Long): ClauseEntry = {
    this._value = value.toString
    clauseEntry
  }

  def apply(valueOpt: Option[Long]): ClauseEntry = {
    valueOpt.foreach(value => this._value = value.toString)
    clauseEntry
  }

  def greaterEqual(value: Long): ClauseEntry = {
    this._value = value.toString
    this._oper = ">"
    clauseEntry
  }

  override def oper: String = _oper

  override def value: ParameterValue = _value
}

case class StringParameter(parameterNumber: Int, name: String, clauseEntry: ClauseEntry) extends ValueParameter {
  var _oper = "="
  var _value: ParameterValue = _

  def apply(value: String): ClauseEntry = {
    this._value = value
    clauseEntry
  }

  def apply(valueOpt: Option[String]): ClauseEntry = {
    valueOpt.foreach(value => this._value = value)
    clauseEntry
  }

  override def oper: String = _oper
  override def value: ParameterValue = _value
}

case class DateParameter(parameterNumber: Int, name: String, clauseEntry: ClauseEntry) extends ValueParameter {
  var _oper = "="
  var _value: ParameterValue = _
  val format = new SimpleDateFormat("yyyy-MM-dd")

  def apply(value: Date): ClauseEntry = {
    this._value = format.format(value)
    clauseEntry
  }

  def greaterOrEqual(date: Date): ClauseEntry = {
    this._value = format.format(date)
    this._oper = ">="
    clauseEntry
  }

  def less(date: Date): ClauseEntry = {
    this._value = format.format(date)
    this._oper = "<"
    clauseEntry
  }

  override def oper: String = _oper

  override def value: ParameterValue = _value
}