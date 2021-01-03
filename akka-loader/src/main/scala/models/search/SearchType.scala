package models.search

object SearchType extends Enumeration {
  val PLAYERS = Value("0")
  val ARENAS = Value("1")
  val MANAGERS = Value("2")
  val SERIES = Value("3")
  val TEAMS = Value("4")
  val REGIONS = Value("5")
  val MATCH = Value("6")
}
