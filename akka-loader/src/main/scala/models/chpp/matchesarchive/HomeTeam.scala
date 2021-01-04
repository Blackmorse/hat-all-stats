package models.chpp.matchesarchive

import com.lucidchart.open.xtract.{XmlReader, __}
import cats.syntax.all._

case class HomeTeam(homeTeamId: Int,
                    homeTeamName: String)

object HomeTeam {
  implicit val reader: XmlReader[HomeTeam] = (
    (__ \ "HomeTeamID").read[Int],
    (__ \ "HomeTeamName").read[String],
  ).mapN(apply _)
}
