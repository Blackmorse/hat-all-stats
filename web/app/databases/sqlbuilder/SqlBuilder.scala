package databases.sqlbuilder

import anorm.{NamedParameter, Row, SQL, SimpleSql}
import databases.sqlbuilder.clause.{ClauseEntry, HavingClause, WhereClause}
import hattid.LoddarStatsUtils
import models.web.{Asc, Desc, SortingDirection}

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

  object fields {
    val hatstats = "rating_midfield * 3 + rating_left_att + rating_right_att + rating_mid_att + rating_left_def + rating_right_def + rating_mid_def"
    val oppositeHatstats = "opposite_rating_midfield * 3 + opposite_rating_left_att + opposite_rating_right_att + opposite_rating_mid_att + opposite_rating_left_def + opposite_rating_right_def + opposite_rating_mid_def"

    val loddarStats = LoddarStatsUtils.homeLoddarStats
    val oppositeLoddarStats = LoddarStatsUtils.awayLoddarStats
  }
}

case class SqlBuilder(baseSql: String,
                      newApi: Boolean = false,
                      name: String = "main"/*for the nested requests*/) {

  var parametersNumber = 0
  private var page = 0
  private var pageSize = 16
  private var sortingDirection: String = "desc"
  private var sortBy: Option[String] = None
  private val orderBies = mutable.Buffer[String]()
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

  def limitBy(number: Int, field: String) = {
    this.limitByNumber = number
    this.limitByField = Some(field)
    this
  }

  def page(page: Int): SqlBuilder = {
    this.page = page
    this
  }

  def pageSize(pageSize: Int): SqlBuilder = {
    this.pageSize = pageSize
    this
  }

  def orderByOldBuilder(ob: String*): SqlBuilder = {
    this.orderBies ++= ob
    this
  }

  @deprecated(message = "use orderBy")
  def sortBy(sb: String): SqlBuilder = {
    this.sortBy = Some(sb)
    this
  }

  def sortingDirection(direction: SortingDirection): SqlBuilder = {
    this.sortingDirection = direction match {
      case Desc => "desc"
      case Asc => "asc"
    }
    this
  }

  def build: SimpleSql[Row] = {
    val finalSql = if (!this.newApi) {

      val sql = baseSql.replace("__where__", whereClause.toString)
        .replace("__having__", havingClause.toString)

      val result = sql
        .replace("__limit__", s" limit ${page * pageSize}, ${pageSize + 1}")
        .replace("__sortingDirection__", sortingDirection)

      val obs = orderBies.map(ob => s"$ob $sortingDirection").mkString(",")
      val tr = if (orderBies.nonEmpty)
        result.replace("__orderBy__", s" ORDER BY $obs")
      else result.replace("__orderBy__", " ")

      this.sortBy.map(sb => tr.replace("__sortBy__", sb)).getOrElse(tr)

    } else {
      buildNewApi()
    }

    SQL(finalSql)
      .on((whereClause.parameters ++ havingClause.parameters).toSeq
        .filter(parameter => parameter.isInstanceOf[ValueParameter])
        .map(_.asInstanceOf[ValueParameter])
        .map(parameter => NamedParameter.namedWithString((s"${parameter.name}_${parameter.parameterNumber}", parameter.value))): _*
      )
  }

  private def buildNewApi(): String = {
    val selectFrom = this._select.toString
    val where = if (whereClause.initialized) {
      whereClause.toString
    }
    val groupBy = if (this._groupBy != null) s" GROUP BY ${this._groupBy.toString}" else ""
    val havingString = if (this.havingClause != null) {
      havingClause.toString
    }
    val orderBy = if (this._orderBy != null ) s" ORDER BY ${this._orderBy.toString}" else ""

    s"$selectFrom " +
      s"$where " +
      s"$groupBy " +
      s"$havingString " +
      s"$orderBy " +
      s"${this.limitByField.map(lField => s"LIMIT ${this.limitByNumber} BY $lField").getOrElse("")} " +
      s" LIMIT ${page * pageSize}, ${pageSize + 1}"
  }
}