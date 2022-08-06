package chpp.avatars.models

import cats.syntax.all._
import com.lucidchart.open.xtract.XmlReader._
import com.lucidchart.open.xtract.{XmlReader, __}

case class AvatarContainer(team: Team)

object AvatarContainer {
  implicit val reader: XmlReader[AvatarContainer] = (
    (__ \ "Team").read[Team]
      .map(apply _)
  )
}

case class Team(teamId: Int, players: Seq[Player])

object Team {
  implicit val reader: XmlReader[Team] = (
    (__ \ "TeamId").read[Int],
    (__ \ "Players" \ "Player").read(seq[Player])
  ).mapN(apply _)
}
