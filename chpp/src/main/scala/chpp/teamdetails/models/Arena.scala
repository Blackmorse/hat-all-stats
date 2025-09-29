package chpp.teamdetails.models

import cats.syntax.all._
import com.lucidchart.open.xtract.XmlReader._
import com.lucidchart.open.xtract.{XmlReader, __}

case class Arena(arenaId: Int,
                 arenaName: String)

object Arena {
  implicit val reader: XmlReader[Arena] = (
    (__ \ "ArenaID").read[Int],
    (__ \ "ArenaName").read[String],
    ).mapN(apply)
}
