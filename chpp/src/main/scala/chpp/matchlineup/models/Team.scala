package chpp.matchlineup.models

import com.lucidchart.open.xtract.{XmlReader, __}
import com.lucidchart.open.xtract.XmlReader._
import cats.syntax.all._

case class Team(teamId: Long,
                teamName: String,
                experienceLevel: Int,
                styleOfPlay: Int,
                startingLineup: Seq[StartingLineupPlayer],
                substitutions: Seq[Substitution],
                lineup: Seq[LineupPlayer],
               )

object Team {
  implicit val reader: XmlReader[Team] = (
    (__ \ "TeamID").read[Long],
    (__ \ "TeamName").read[String],
    (__ \ "ExperienceLevel").read[Int],
    (__ \ "StyleOfPlay").read[Int],
    (__ \ "StartingLineup" \ "Player").read(seq[StartingLineupPlayer]),
    (__ \ "Substitutions" \ "Substitution").read(seq[Substitution]),
    (__ \ "Lineup" \ "Player").read(seq[LineupPlayer])
  ).mapN(apply _)
}
