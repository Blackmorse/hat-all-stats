package sqlbuilder

import scala.collection.mutable

object NestedSelect {
  def apply(fields: Field*): From = {
    new SqlBuilder("nested_req").select(fields: _*)
  }
}

object WSelect {
  def apply(fields: Field*): From = {
    new SqlBuilder("with").select(fields: _*)
  }
}

object Select {
  def apply(fields: Field*): From = {
    new SqlBuilder("main").select(fields: _*)
  }
}

class Select(sqlBuild: SqlBuilder) {
  val from = From(sqlBuild)
  private var fields: Seq[Field] = _
  def apply(fields: Field*): From = {
    this.fields = fields
    from
  }

  def parameters: Seq[Parameter] =
    if (this.from._innerSqlBuilder != null) {
      (this.from._innerSqlBuilder.whereClause.parameters ++ this.from._innerSqlBuilder.havingClause.parameters ++
        this.from._innerSqlBuilder.withSelect.map(ws => ws.sqlBuilder.parameters).getOrElse(mutable.Buffer())).toSeq
    } else {
      Seq()
    }

  override def toString: String = {
    val fromString = if (from._innerSqlBuilder == null) {
      from._name
    } else {
      "(" + from._innerSqlBuilder.buildStringSql() + ")"
    }
    s"""
       |SELECT ${fields.map(_.toString).mkString(", ")}
       |FROM $fromString
       |""".stripMargin
  }
}

class Field (val name: String) {
  private var alias: Option[String] = None

  def as(alias: String): Field = {
    this.alias = Some(alias)
    this
  }

  def *(n: Int): Field = {
    val field = new Field(s"$name * $n")
    field.alias = alias
    field
  }

  def toInt32: Field = function("toInt32")
  def toInt64: Field = function("toInt64")
  def toUInt16: Field = function("toUInt16")

  private def function(functionName: String): Field = {
    val res = new Field(s"$functionName($name)")
    res.alias = alias
    res
  }

  override def toString: String = {
    s"$name${alias.map(a => s" as $a").getOrElse("")}"
  }
}

case class From(sqlBuilder: SqlBuilder) {
  var _name: String = _
  var _innerSqlBuilder: SqlBuilder = _
  def from(name: String): SqlBuilder = {
    this._name = name
    sqlBuilder
  }

  def from(innerSqlBuilder: SqlBuilder): SqlBuilder = {
    this._innerSqlBuilder = innerSqlBuilder
    sqlBuilder
  }
}

