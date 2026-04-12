package chpp.managercompendium.models

import cats.syntax.all._
import com.lucidchart.open.xtract.{XmlReader, __}

case class YouthLeague(youthLeagueId: Long, youthLeagueName: String)

object YouthLeague {
  implicit val reader: XmlReader[YouthLeague] = (
    (__ \ "YouthLeagueId").read[Long],
    (__ \ "YouthLeagueName").read[String]
  ).mapN(apply)
}
