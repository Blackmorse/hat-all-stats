package chpp.matches.models

import cats.syntax.all._
import com.lucidchart.open.xtract.XmlReader._
import com.lucidchart.open.xtract.{XmlReader, __}

case class Matches(isYouth: Boolean,
                   team: Team)

object Matches {
  implicit val reader: XmlReader[Matches] = (
    (__ \ "IsYouth").read[Boolean],
    (__ \ "Team").read[Team]
  ).mapN(apply _)
}