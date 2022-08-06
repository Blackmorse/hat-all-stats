package chpp.playerdetails.models

import com.lucidchart.open.xtract.XmlReader._
import com.lucidchart.open.xtract.{XmlReader, __}
import cats.syntax.all._

case class OwningTeam(teamId: Long, teamName: String, leagueId: Int)

object OwningTeam {
  implicit val reader: XmlReader[OwningTeam] = (
    (__ \ "TeamID").read[Long],
    (__ \ "TeamName").read[String],
    (__ \ "LeagueID").read[Int]
  ).mapN(apply _)
}