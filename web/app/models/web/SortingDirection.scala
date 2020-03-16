package models.web

sealed abstract class SortingDirection

object Asc extends SortingDirection {
  override def toString: String = "filter.asc"
}

object Desc extends SortingDirection {
  override def toString: String = "filter.desc"
}
