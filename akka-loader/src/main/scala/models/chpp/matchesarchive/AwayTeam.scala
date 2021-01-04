package models.chpp.matchesarchive

import com.lucidchart.open.xtract.{XmlReader, __}
import cats.syntax.all._

case class AwayTeam(awayTeamId: Int,
                    awayTeamName: String)

object AwayTeam {
  implicit val reader: XmlReader[AwayTeam] = (
    (__ \ "AwayTeamID").read[Int],
    (__ \ "AwayTeamName").read[String],
    ).mapN(apply _)
}
