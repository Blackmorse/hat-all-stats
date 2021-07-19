package databases.sqlbuilder


class Select(sqlBuild: SqlBuilder) {
  val from = From(sqlBuild)
  private var fields: Seq[Field] = _
  def apply(fields: Field*): From = {
    this.fields = fields
    from
  }

  override def toString: String = {
    s"""
       |SELECT ${fields.map(_.toString).mkString(", ")}
       |FROM ${from._name}
       |""".stripMargin
  }
}

class Field (val name: String) {
  private var alias: Option[String] = None

  def as(alias: String): Field = {
    this.alias = Some(alias)
    this
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

