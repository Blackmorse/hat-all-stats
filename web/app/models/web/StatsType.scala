package models.web

abstract class StatsType

case class MultiplyRoundsType(function: String) extends StatsType {
  override def toString: String = function
}

object Accumulate extends StatsType {
  override def toString: String = "all"
}

object Avg extends MultiplyRoundsType("avg")

object Max extends MultiplyRoundsType("max")


case class Round(round: Int) extends StatsType {
  override def toString: String = round.toString
}
