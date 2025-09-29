package chpp.teamdetails.models

import cats.syntax.all._
import com.lucidchart.open.xtract.XmlReader._
import com.lucidchart.open.xtract.{XmlReader, __}

case class Flags(homeFlags: Seq[Flag],
                 awayFlags: Seq[Flag])

object Flags {
  implicit val reader: XmlReader[Flags] = (
    (__ \ "HomeFlags" \ "Flag").read(seq[Flag]),
    (__ \ "AwayFlags" \ "Flag").read(seq[Flag]),
    ).mapN(apply)
}