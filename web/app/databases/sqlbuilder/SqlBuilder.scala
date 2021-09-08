package databases.sqlbuilder

import anorm.{NamedParameter, Row, SQL, SimpleSql}
import databases.sqlbuilder.clause.{ClauseEntry, HavingClause, WhereClause}
import hattid.LoddarStatsUtils

case class GroupBy(fields: Seq[String]) {
  override def toString: String = {
    fields.mkString(", ")
  }
}

object SqlBuilder {
  trait func {
    def apply(field: Field): Field
  }

  object avg extends func {
    override def apply(field: Field): Field = {
      new Field(s"avg(${field.name})")
    }
  }

  object max extends func {
    override def apply(field: Field): Field = {
      new Field(s"max(${field.name})")
    }
  }

  object identity extends func {
    override def apply(field: Field): Field = field
  }

  object implicits {
    implicit def stringToField(string: String): Field =
      new Field(string)

    implicit def stringToOrderByField(field: String): OrderByField =
      new OrderByField(field)
  }

  object fields {
    import SqlBuilder.implicits._

    val hatstats: Field = "rating_midfield * 3 + rating_left_att + rating_right_att + rating_mid_att + rating_left_def + rating_right_def + rating_mid_def"
    val oppositeHatstats: Field = "opposite_rating_midfield * 3 + opposite_rating_left_att + opposite_rating_right_att + opposite_rating_mid_att + opposite_rating_left_def + opposite_rating_right_def + opposite_rating_mid_def"

    val loddarStats: Field = LoddarStatsUtils.homeLoddarStats
    val oppositeLoddarStats: Field = LoddarStatsUtils.awayLoddarStats
  }
}

case class SqlBuilder(name: String = "main"/*for the nested requests*/) {

  var parametersNumber = 0
  private var page: Int = 0
  private var pageSize: Int = 0
  private[sqlbuilder] val whereClause = new WhereClause(this)
  private[sqlbuilder] val havingClause = new HavingClause(this)
  private var _groupBy: GroupBy = _
  private var _orderBy: OrderBy = _
  private var _select: Select = _
  private var limitByNumber = 0
  private var limitByField: Option[String] = None

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
    this.page = 0
    this.pageSize = limit + 1
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

  def build: SimpleSql[Row] = {
    val finalSql = buildStringSql()
    println(finalSql)
    val parameters = whereClause.parameters ++ havingClause.parameters ++ this._select.parameters

    SQL(finalSql)
      .on(parameters.toSeq
        .filter(parameter => parameter.isInstanceOf[ValueParameter])
        .map(_.asInstanceOf[ValueParameter])
        .map(parameter => {
          NamedParameter.namedWithString((s"${parameter.sqlBuilderName}_${parameter.name}_${parameter.parameterNumber}", parameter.value))
        }): _*
      )
  }

  def buildStringSql(): String = {
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
      s"LIMIT ${(pageSize - 1) * page}, ${pageSize}"
    } else {
      ""
    }
   s"$selectFrom " +
      s"$where " +
      s"$groupBy " +
      s"$havingString " +
      s"$orderBy " +
      s"${this.limitByField.map(lField => s"LIMIT ${this.limitByNumber} BY $lField").getOrElse("")} " +
      s"$limit"
  }
}