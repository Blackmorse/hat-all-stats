package chpp.matches.models

import cats.syntax.all._
import com.lucidchart.open.xtract.XmlReader._
import com.lucidchart.open.xtract.{XmlReader, __}

case class HomeTeam(homeTeamId: Long,
                    homeTeamName: String,
                    homeTeamShortName: String)

object HomeTeam {
  implicit val reader: XmlReader[HomeTeam] = (
    (__ \ "HomeTeamID").read[Long],
    (__ \ "HomeTeamName").read[String],
    (__ \ "HomeTeamNameShortName").read[String]
  ).mapN(apply)
}
