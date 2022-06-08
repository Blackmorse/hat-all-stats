package sqlbuilder

import sqlbuilder.clause.ClauseEntry

import java.text.SimpleDateFormat
import java.util.Date

trait Parameter

trait ValueParameter[+T] extends Parameter {
  val parameterNumber: Int
  val name: String
  def oper: String
  def value: Option[T]
  def sqlBuilderName: String
}

case class ConditionParameter(condition: String) extends Parameter

case class IntParameter(parameterNumber: Int, name: String, clauseEntry: ClauseEntry, sqlBuilderName: String) extends ValueParameter[Int] {
  private[sqlbuilder] var _oper: String = "="
  private[sqlbuilder] var _value: Option[Int] = None

  def apply(value: Int): ClauseEntry = {
    this._value = Some(value)
    clauseEntry
  }

  def apply(valueOpt: Option[Int]): ClauseEntry = {
    this._value = valueOpt
    clauseEntry
  }

  def greaterEqual(value: Int): ClauseEntry = {
    this._value = Some(value)
    this._oper = ">="
    clauseEntry
  }

  def greaterEqual(valueOpt: Option[Int]): ClauseEntry = {
    this._value = valueOpt
    this._oper = ">="
    clauseEntry
  }

  def lessEqual(value: Int): ClauseEntry = {
    this._value = Some(value)
    this._oper = "<="
    clauseEntry
  }

  def lessEqual(valueOpt: Option[Int]): ClauseEntry = {
    this._value = valueOpt
    this._oper = "<="
    clauseEntry
  }

  override def oper: String = _oper
  override def value: Option[Int] = _value
}

case class LongParameter(parameterNumber: Int, name: String, clauseEntry: ClauseEntry, sqlBuilderName: String) extends ValueParameter[Long] {
  var _oper: String = "="
  var _value: Option[String] = None

  def apply(value: Long): ClauseEntry = {
    this._value = Some(value.toString)
    clauseEntry
  }

  def apply(valueOpt: Option[Long]): ClauseEntry = {
    this._value = valueOpt.map(_.toString)
    clauseEntry
  }

  def greaterEqual(value: Long): ClauseEntry = {
    this._value = Some(value.toString)
    this._oper = ">"
    clauseEntry
  }

  override def oper: String = _oper

  override def value: Option[Long] = _value.map(_.toLong)
}

case class StringParameter(parameterNumber: Int, name: String, clauseEntry: ClauseEntry, sqlBuilderName: String) extends ValueParameter[String] {
  var _oper = "="
  var _value: Option[String] = None

  def apply(value: String): ClauseEntry = {
    this._value = Some(value)
    clauseEntry
  }

  def apply(valueOpt: Option[String]): ClauseEntry = {
    this._value = valueOpt
    clauseEntry
  }

  override def oper: String = _oper
  override def value: Option[String] = _value
}

case class DateParameter(parameterNumber: Int, name: String, clauseEntry: ClauseEntry, sqlBuilderName: String) extends ValueParameter[String] {
  var _oper = "="
  var _value: Option[String] = None
  val format = new SimpleDateFormat("yyyy-MM-dd")

  def apply(value: Date): ClauseEntry = {
    this._value = Some(format.format(value))
    clauseEntry
  }

  def greaterOrEqual(date: Date): ClauseEntry = {
    this._value = Some(format.format(date))
    this._oper = ">="
    clauseEntry
  }

  def less(date: Date): ClauseEntry = {
    this._value = Some(format.format(date))
    this._oper = "<"
    clauseEntry
  }

  override def oper: String = _oper

  override def value: Option[String] = _value
}