package chpp.matches.models

import cats.syntax.all._
import com.lucidchart.open.xtract.XmlReader._
import com.lucidchart.open.xtract.{XmlReader, __}

case class AwayTeam(awayTeamId: Long,
                    awayTeamName: String,
                    awayTeamShortName: String)

object AwayTeam {
  implicit val reader: XmlReader[AwayTeam] = (
    (__ \ "AwayTeamID").read[Long],
    (__ \ "AwayTeamName").read[String],
    (__ \ "AwayTeamNameShortName").read[String]
  ).mapN(apply _)
}


