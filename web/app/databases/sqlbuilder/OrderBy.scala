package databases.sqlbuilder

import models.web.{Asc, Desc, SortingDirection}

class OrderBy(val sqlBuilder: SqlBuilder) {
  private var orderByFields: Seq[OrderByField] = _

  def apply(orderByFields: OrderByField*): SqlBuilder = {
    this.orderByFields = orderByFields
    sqlBuilder
  }

  override def toString: String = {
    this.orderByFields.map(_.toString).mkString(", ")
  }
}

class OrderByField(val name: String) {
  private var _direction: SortingDirection = Desc

  def to(sd: SortingDirection): OrderByField = {
    this._direction = sd
    this
  }

  def desc: OrderByField = {
    this._direction = Desc
    this
  }

  def asc: OrderByField = {
    this._direction = Asc
    this
  }

  override def toString: String = {
    val directionString = this._direction match {
      case Asc => ""
      case Desc => "DESC"
    }
    s"$name $directionString"
  }
}
