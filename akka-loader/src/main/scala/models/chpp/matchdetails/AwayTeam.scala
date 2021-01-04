package models.chpp.matchdetails

import com.lucidchart.open.xtract.{XmlReader, __}
import cats.syntax.all._

case class AwayTeam(awayTeamId: Int,
                    awayTeamName: String,
                    awayGoals: Int)

object AwayTeam {
  implicit val reader: XmlReader[AwayTeam] = (
    (__ \ "AwayTeamID").read[Int],
    (__ \ "AwayTeamName").read[String],
    (__ \ "AwayGoals").read[Int],
    ).mapN(apply _)
}
