package models.chpp.matchdetails

import com.lucidchart.open.xtract.{XmlReader, __}
import cats.syntax.all._

case class HomeTeam(homeTeamId: Int,
                    homeTeamName: String,
                    homeGoals: Int)

object HomeTeam {
  implicit val reader: XmlReader[HomeTeam] = (
    (__ \ "HomeTeamID").read[Int],
    (__ \ "HomeTeamName").read[String],
    (__ \ "HomeGoals").read[Int]
    ).mapN(apply _)
}
