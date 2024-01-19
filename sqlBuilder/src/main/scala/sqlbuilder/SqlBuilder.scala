package sqlbuilder

import anorm.{NamedParameter, ParameterValue, Row, SQL, SimpleSql}
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

case class SqlWithParameters(sql: String, parameters: Seq[Parameter]) {
  def build: SimpleSql[Row] = {
    SQL(sql)
      .on(parameters.toSeq
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
  private val settingsMap = mutable.Map[String, Any]()
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

  def setting(name: String, value: Any): SqlBuilder = {
    settingsMap += (name -> value)
    this
  }

  def parameters: mutable.Buffer[Parameter] = this.withSelect.map(ws => ws.sqlBuilder.parameters).getOrElse(mutable.Buffer()) ++ whereClause.parameters ++ havingClause.parameters ++ this._select.parameters

  def sqlWithParameters(): SqlWithParameters = {
    val str = buildStringSql()
    SqlWithParameters(
      sql = str,
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

    val settingsPart = if(settingsMap.isEmpty) {
      ""
    } else {
      val set= settingsMap.map{
        case (k, v: Int) => s"$k = $v"
        case (k, v: Any) => s"""$k = "$v""""
      }.mkString(", ")
      s"SETTINGS $set"
    }
    withClause +
      s"$selectFrom " +
      s"$where " +
      s"$groupBy " +
      s"$havingString " +
      s"$orderBy " +
      s"${this.limitByField.map(lField => s"LIMIT ${this.limitByNumber} BY $lField").getOrElse("")} " +
      s"$limit " +
      s"$settingsPart "
  }
}
