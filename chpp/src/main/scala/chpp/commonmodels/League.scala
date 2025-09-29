package chpp.commonmodels

import cats.syntax.all._
import com.lucidchart.open.xtract.XmlReader._
import com.lucidchart.open.xtract.{XmlReader, __}

case class League(leagueId: Int,
                  leagueName: String)

object League {
  implicit val reader: XmlReader[League] = (
    (__ \ "LeagueID").read[Int],
    (__ \ "LeagueName").read[String],
    ).mapN(apply)
}
