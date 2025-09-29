package sqlbuilder

sealed abstract class SqlSortingDirection

case object Ascending extends SqlSortingDirection {
  override def toString: String = "ASC"
}

case object Descending extends SqlSortingDirection {
  override def toString: String = "DESC"
}

class OrderBy(val sqlBuilder: SqlBuilder) {
  private var orderByFields: Seq[OrderByField] = scala.compiletime.uninitialized

  def apply(orderByFields: OrderByField*): SqlBuilder = {
    this.orderByFields = orderByFields
    sqlBuilder
  }

  override def toString: String = {
    this.orderByFields.map(_.toString).mkString(", ")
  }
}

class OrderByField(val name: String) {
  private var _direction: SqlSortingDirection = Descending
  private var _withFillTo: Option[String] = None

  def to(sd: SqlSortingDirection): OrderByField = {
    this._direction = sd
    this
  }

  def desc: OrderByField = {
    this._direction = Descending
    this
  }

  def asc: OrderByField = {
    this._direction = Ascending
    this
  }

  def withFillTo(to: String): OrderByField = {
    this._withFillTo = Some(to)
    this
  }

  override def toString: String = {
    val withFill = _withFillTo.map(wf => s"WITH FILL TO $wf").getOrElse("")
    s"$name ${_direction.toString} $withFill"
  }
}
