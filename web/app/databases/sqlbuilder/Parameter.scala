package databases.sqlbuilder

import anorm.ParameterValue

import java.text.SimpleDateFormat
import java.util.Date

trait Parameter {
  val parameterNumber: Int
  val name: String
  def oper: String
  def value: ParameterValue
}

case class IntParameter(parameterNumber: Int, name: String, clause: Clause) extends Parameter {
  var _oper: String = "="
  var _value: ParameterValue = _

  def apply(value: Int): Clause = {
    this._value = value
    clause
  }

  def apply(valueOpt: Option[Int]): Clause = {
    valueOpt.foreach(value => this._value = value)
    clause
  }

  def greaterEqual(value: Int): Clause = {
    this._value = value
    this._oper = ">="
    clause
  }

  def greaterEqual(valueOpt: Option[Int]): Clause = {
    valueOpt.foreach(value => this._value = value)
    this._oper = ">="
    clause
  }

  def lessEqual(value: Int): Clause = {
    this._value = value
    this._oper = "<="
    clause
  }

  def lessEqual(valueOpt: Option[Int]): Clause = {
    valueOpt.foreach(value => this._value = value)
    this._oper = "<="
    clause
  }

  override def oper: String = _oper
  override def value: ParameterValue = _value
}

case class LongParameter(parameterNumber: Int, name: String, clause: Clause) extends Parameter {
  var _oper: String = "="
  var _value: ParameterValue = _

  def apply(value: Long): Clause = {
    this._value = value.toString
    clause
  }

  def apply(valueOpt: Option[Long]): Clause = {
    valueOpt.foreach(value => this._value = value.toString)
    clause
  }

  def greaterEqual(value: Long): Clause = {
    this._value = value.toString
    this._oper = ">"
    clause
  }

  override def oper: String = _oper

  override def value: ParameterValue = _value
}

case class StringParameter(parameterNumber: Int, name: String, clause: Clause) extends Parameter {
  var _oper = "="
  var _value: ParameterValue = _

  def apply(value: String): Clause = {
    this._value = value
    clause
  }

  def apply(valueOpt: Option[String]): Clause = {
    valueOpt.foreach(value => this._value = value)
    clause
  }

  override def oper: String = _oper
  override def value: ParameterValue = _value
}

case class DateParameter(parameterNumber: Int, name: String, clause: Clause) extends Parameter {
  var _oper = "="
  var _value: ParameterValue = _
  val format = new SimpleDateFormat("yyyy-MM-dd")

  def apply(value: Date): Clause = {
    this._value = format.format(value)
    clause
  }

  def greaterOrEqual(date: Date): Clause = {
    this._value = format.format(date)
    this._oper = ">="
    clause
  }

  def less(date: Date): Clause = {
    this._value = format.format(date)
    this._oper = "<"
    clause
  }

  override def oper: String = _oper

  override def value: ParameterValue = _value
}
