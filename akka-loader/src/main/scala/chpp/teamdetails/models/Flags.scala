package chpp.teamdetails.models

import cats.syntax.all._
import com.lucidchart.open.xtract.XmlReader._
import com.lucidchart.open.xtract.{XmlReader, __}

case class Flags(homeFlags: Seq[Flag],
                 awaFlags: Seq[Flag])

object Flags {
  implicit val reader: XmlReader[Flags] = (
    (__ \ "HomeFlags" \ "Flag").read(seq[Flag]),
    (__ \ "AwaFlags" \ "Flag").read(seq[Flag]),
    ).mapN(apply _)
}