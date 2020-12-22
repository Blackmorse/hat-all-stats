package databases.requests.model

case class Role(name: String, htIds: Seq[Int], internalId: Int)

object Roles {
  val all = Seq(
    Role("none", Seq(), 0),
    Role("keeper", Seq(100), 1),
    Role("wingback", Seq(101, 105), 2),
    Role("defender", Seq(102, 103, 104), 3),
    Role("winger", Seq(106, 110), 4),
    Role("midfielder", Seq(107, 108, 109), 5),
    Role("forward", Seq(111, 112, 113), 6)
  )

  val mapping = Map(
    0 -> "none",
    1 -> "keeper",
    2 -> "wingback",
    3 -> "defender",
    4 -> "winger",
    5 -> "midfielder",
    6 -> "forward"
  )

  def of(name: String): Option[Role] = all.find(_.name == name)
}
