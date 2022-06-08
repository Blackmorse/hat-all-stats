package models.web

import sqlbuilder.{Ascending, Descending, SqlSortingDirection}

sealed abstract class SortingDirection {
  def reverse: SortingDirection

  def toSql: SqlSortingDirection
}

object Asc extends SortingDirection {
  override def toString: String = "filter.asc"

  override def reverse: SortingDirection = Desc

  override def toSql: SqlSortingDirection = Ascending
}

object Desc extends SortingDirection {
  override def toString: String = "filter.desc"

  override def reverse: SortingDirection = Asc

  override def toSql: SqlSortingDirection = Descending
}
