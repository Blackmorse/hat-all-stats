package models.web

sealed abstract class SortingDirection {
  def reverse: SortingDirection
}

object Asc extends SortingDirection {
  override def toString: String = "filter.asc"

  override def reverse: SortingDirection = Desc
}

object Desc extends SortingDirection {
  override def toString: String = "filter.desc"

  override def reverse: SortingDirection = Asc
}
