package chpp.managercompendium.models

import com.lucidchart.open.xtract.{XmlReader, __}
import cats.syntax.all._

case class League(leagueId: Int, leagueName: String, season: Int)

object League {
  implicit val reader: XmlReader[League] = (
    (__ \ "LeagueId").read[Int],
    (__ \ "LeagueName").read[String],
    (__ \ "Season").read[Int],
  ).mapN(apply)
}
