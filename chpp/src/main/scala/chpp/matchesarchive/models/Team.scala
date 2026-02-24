package chpp.matchesarchive.models

import cats.syntax.all._
import com.lucidchart.open.xtract.XmlReader._
import com.lucidchart.open.xtract.{XmlReader, __}

case class Team(teamId: Int,
                teamName: String,
                matchList: Seq[Match])

object Team {
  implicit val reader: XmlReader[Team] = (
    (__ \ "TeamID").read[Int],
    (__ \ "TeamName").read[String],
    (__ \ "MatchList" \ "Match").read(using seq[Match]),
    ).mapN(apply)
}
