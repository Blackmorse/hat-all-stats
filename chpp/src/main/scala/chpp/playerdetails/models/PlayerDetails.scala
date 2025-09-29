package chpp.playerdetails.models

import com.lucidchart.open.xtract.{XmlReader, __}

case class PlayerDetails(player: Player)

object PlayerDetails {
  implicit val reader: XmlReader[PlayerDetails] = (
    (__ \ "Player").read[Player]
  ).map(apply)
}
