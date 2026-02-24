package chpp.matchdetails.models

import cats.syntax.all._
import com.lucidchart.open.xtract.XmlReader._
import com.lucidchart.open.xtract.{XmlReader, __}

case class Arena(arenaId: Int,
                 arenaName: String,
                 weatherId: WeatherId.Value,
                 soldTotal: Int,
                 soldTerraces: Option[Int],
                 soldBasic: Option[Int],
                 soldRoof: Option[Int],
                 soldVip: Option[Int])

object Arena {
  implicit val reader: XmlReader[Arena] = (
    (__ \ "ArenaID").read[Int],
    (__ \ "ArenaName").read[String],
    (__ \ "WeatherID").read(using `enum`(WeatherId)),
    (__ \ "SoldTotal").read[Int],
    (__ \ "SoldTerraces").read[Int].optional,
    (__ \ "SoldBasic").read[Int].optional,
    (__ \ "SoldRoof").read[Int].optional,
    (__ \ "SoldVIP").read[Int].optional,
  ).mapN(apply)
}
