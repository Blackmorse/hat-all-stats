package chpp.teamdetails.models

import cats.syntax.all._
import com.lucidchart.open.xtract.XmlReader._
import com.lucidchart.open.xtract.{XmlReader, __}

case class Flag(leagueId: Int,
                leagueName: String,
                countryCode: String)

object Flag {
  implicit val reader: XmlReader[Flag] = (
    (__ \ "LeagueId").read[Int],
    (__ \ "LeagueName").read[String],
    (__ \ "CountryCode").read[String],
  ).mapN(apply)
}