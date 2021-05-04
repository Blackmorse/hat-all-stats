package databases.sqlbuilder

import anorm.{NamedParameter, ParameterValue, Row, SQL, SimpleSql}
import databases.requests.OrderingKeyPath
import databases.sqlbuilder.clause.{HavingClause, WhereClause}
import models.web.{Asc, Desc, RestStatisticsParameters, SortingDirection}

import scala.collection.mutable


case class SqlBuilder(baseSql: String) {
  var parametersNumber = 0
  private var page = 0
  private var pageSize = 16
  private var sortingDirection: String = "desc"
  private var sortBy: Option[String] = None
  private val orderBies = mutable.Buffer[String]()
  private[sqlbuilder] val whereClause = new WhereClause(this)
  private[sqlbuilder] val havingClause = new HavingClause(this)

  def where = whereClause.create()

  def having = havingClause.create()


  def page(page: Int): SqlBuilder = {
    this.page = page
    this
  }

  def pageSize(pageSize: Int): SqlBuilder = {
    this.pageSize = pageSize
    this
  }

  def orderBy(ob: String*): SqlBuilder = {
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
    val sql = baseSql.replace("__where__", whereClause.toString)
      .replace("__having__", havingClause.toString)

    val result = sql
      .replace("__limit__", s" limit ${page * pageSize}, ${pageSize + 1}")
      .replace("__sortingDirection__", sortingDirection)

    val obs = orderBies.map(ob => s"$ob $sortingDirection").mkString(",")
    val tr = if (orderBies.nonEmpty)
      result.replace("__orderBy__", s" ORDER BY $obs")
    else result.replace("__orderBy__", " ")

    val withSortByResult = this.sortBy.map(sb => tr.replace("__sortBy__", sb)).getOrElse(tr)

    SQL(withSortByResult)
      .on((whereClause.parameters ++ havingClause.parameters).toSeq.map(parameter => NamedParameter.namedWithString((s"${parameter.name}_${parameter.parameterNumber}", parameter.value))): _*)
  }
}