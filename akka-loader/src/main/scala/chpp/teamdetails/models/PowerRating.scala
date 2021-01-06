package chpp.teamdetails.models

import cats.syntax.all._
import com.lucidchart.open.xtract.XmlReader._
import com.lucidchart.open.xtract.{XmlReader, __}

case class PowerRating(globalRanking: Int,
                       leagueRanking: Int,
                       regionRanking: Int,
                       powerRating: Int)

object PowerRating {
  implicit val reader: XmlReader[PowerRating] = (
    (__ \ "GlobalRanking").read[Int],
    (__ \ "LeagueRanking").read[Int],
    (__ \ "RegionRanking").read[Int],
    (__ \ "PowerRating").read[Int],
    ).mapN(apply _)
}
