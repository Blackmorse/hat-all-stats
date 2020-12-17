package databases.requests.model

case class Role(name: String, htIds: Seq[Int])

object Roles {
  val all = Seq(
    Role("none", Seq()),
    Role("keeper", Seq(100)),
    Role("wingback", Seq(101, 105)),
    Role("defender", Seq(102, 103, 104)),
    Role("winger", Seq(106, 110)),
    Role("midfielder", Seq(107, 108, 109)),
    Role("forward", Seq(111, 112, 113))
  )

  def of(name: String): Option[Role] = all.find(_.name == name)
}
