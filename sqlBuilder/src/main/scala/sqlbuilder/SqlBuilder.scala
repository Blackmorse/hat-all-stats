package sqlbuilder

import sqlbuilder.clause.{ClauseEntry, HavingClause, WhereClause}

import scala.collection.mutable

case class GroupBy(fields: Seq[String]) {
  override def toString: String = {
    fields.mkString(", ")
  }
}

object SqlBuilder {
  object implicits {
    implicit def stringToField(string: String): Field =
      new Field(string)

    implicit def stringToOrderByField(field: String): OrderByField =
      new OrderByField(field)
  }
}

case class With(sqlBuilder: SqlBuilder) {
  def as(alias: String, builderName: String = "main"): WithSelect = {
    WithSelect(sqlBuilder, alias, builderName)
  }
}

case class WithSelect(sqlBuilder: SqlBuilder, alias: String, builderName: String) {
  def select(fields: Field*): From = {
    val selectSqlBuilder = new SqlBuilder(builderName)
    selectSqlBuilder.withSelect = Some(this)
    selectSqlBuilder.select(fields: _*)
  }
}

case class SqlWithParameters(sql: String, parameters: Seq[Parameter])

case class SqlBuilder(var name: String = "main"/*for the nested requests*/) {

  var parametersNumber = 0
  private var page: Int = 0
  private var pageSize: Int = 0
  //TODO union with page-pageSize
  private var limitSimple: Option[Int] = None
  private[sqlbuilder] val whereClause = new WhereClause(this)
  private[sqlbuilder] val havingClause = new HavingClause(this)
  private var _groupBy: GroupBy = _
  private var _orderBy: OrderBy = _
  private var _select: Select = _
  private var limitByNumber = 0
  private var limitByField: Option[String] = None
  var withSelect: Option[WithSelect] = None

  def select: Select = {
    this._select = new Select(this)
    _select
  }

  def where: ClauseEntry = whereClause.create()

  def having: ClauseEntry = havingClause.create()

  def groupBy(fields: String*): SqlBuilder = {
    this._groupBy = GroupBy(fields)
    this
  }

  def orderBy = {
    this._orderBy = new OrderBy(this)
    _orderBy
  }

  def limit(limit: Int): SqlBuilder = {
    this.limitSimple = Some(limit)
    this
  }

  def limit(page: Int, pageSize: Int): SqlBuilder = {
    this.page = page
    this.pageSize = pageSize + 1
    this
  }

  def limitBy(number: Int, field: String): SqlBuilder = {
    this.limitByNumber = number
    this.limitByField = Some(field)
    this
  }

  def parameters: mutable.Buffer[Parameter] = this.withSelect.map(ws => ws.sqlBuilder.parameters).getOrElse(mutable.Buffer()) ++ whereClause.parameters ++ havingClause.parameters ++ this._select.parameters

  def sqlWithParameters(): SqlWithParameters = {
    SqlWithParameters(
      sql = buildStringSql(),
      parameters = this.parameters.toSeq
    )
  }

  def buildStringSql(): String = {
    val withClause = this.withSelect.map(ws => "WITH (" + ws.sqlBuilder.buildStringSql() + ") AS " + ws.alias + " ").getOrElse("")

    val selectFrom = this._select.toString
    val where = if (whereClause.initialized) {
      whereClause.toString
    } else ""
    val groupBy = if (this._groupBy != null) s" GROUP BY ${this._groupBy.toString}" else ""
    val havingString = if (this.havingClause.initialized) {
      havingClause.toString
    } else ""
    val orderBy = if (this._orderBy != null ) s" ORDER BY ${this._orderBy.toString}" else ""

    val limit = if (pageSize != 0 || page != 0) {
      s"LIMIT ${(pageSize - 1) * page}, $pageSize"
    } else if (limitSimple.isDefined) {
      s"LIMIT ${limitSimple.get}"
    } else {
      ""
    }
    withClause +
      s"$selectFrom " +
      s"$where " +
      s"$groupBy " +
      s"$havingString " +
      s"$orderBy " +
      s"${this.limitByField.map(lField => s"LIMIT ${this.limitByNumber} BY $lField").getOrElse("")} " +
      s"$limit"
  }
}