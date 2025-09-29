package chpp.teamdetails.models

import com.lucidchart.open.xtract.XmlReader._
import com.lucidchart.open.xtract.{XmlReader, __}

case class Trainer(playerId: Long)

object Trainer {
  implicit val reader: XmlReader[Trainer] = (
    (__ \ "PlayerID").read[Long],
    ).map(apply)
}
