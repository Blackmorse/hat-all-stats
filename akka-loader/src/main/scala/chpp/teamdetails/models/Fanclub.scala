package chpp.teamdetails.models

import cats.syntax.all._
import com.lucidchart.open.xtract.XmlReader._
import com.lucidchart.open.xtract.{XmlReader, __}

case class Fanclub(fanclubId: Int,
                   fanclubName: String,
                   fanclubSize: Int)

object Fanclub {
  implicit val reader: XmlReader[Fanclub] = (
    (__ \ "FanclubID").read[Int],
    (__ \ "FanclubName").read[String],
    (__ \ "FanclubSize").read[Int],
    ).mapN(apply _)
}
