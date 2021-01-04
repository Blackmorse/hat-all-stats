package models.chpp.matchesarchive

import com.lucidchart.open.xtract.{XmlReader, __}
import cats.syntax.all._
import com.lucidchart.open.xtract.XmlReader._

case class Team(teamId: Int,
                teamName: String,
                matchList: Seq[Match])

object Team {
  implicit val reader: XmlReader[Team] = (
    (__ \ "TeamID").read[Int],
    (__ \ "TeamName").read[String],
    (__ \ "MatchList" \ "Match").read(seq[Match]),
    ).mapN(apply _)
}
