package chpp.players.models

import cats.syntax.all._
import com.lucidchart.open.xtract.XmlReader._
import com.lucidchart.open.xtract.{XmlReader, __}

case class Team(teamId: Int,
                teamName: String,
                playerList: Seq[Player])

object Team {
  implicit val reader: XmlReader[Team] = (
    (__ \ "TeamID").read[Int],
    (__ \ "TeamName").read[String],
    (__ \ "PlayerList" \ "Player").read(seq[Player]),
    ).mapN(apply)
}
