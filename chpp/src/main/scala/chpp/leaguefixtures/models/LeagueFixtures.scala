package chpp.leaguefixtures.models

import cats.syntax.all._
import com.lucidchart.open.xtract.XmlReader._
import com.lucidchart.open.xtract.{XmlReader, __}

case class LeagueFixtures(leagueLevelUnitId: Int,
                          leagueLevelUnitName: String,
                          season: Int,
                          matches: Seq[Match])

object LeagueFixtures {
  implicit val reader: XmlReader[LeagueFixtures] = (
    (__ \ "LeagueLevelUnitID").read[Int],
    (__ \ "LeagueLevelUnitName").read[String],
    (__ \ "Season").read[Int],
    (__ \ "Match").read(using seq[Match]),
  ).mapN(apply)
}
