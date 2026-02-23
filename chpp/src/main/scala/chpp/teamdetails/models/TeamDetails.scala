package chpp.teamdetails.models

import cats.syntax.all._
import com.lucidchart.open.xtract.XmlReader._
import com.lucidchart.open.xtract.{XmlReader, __}

case class TeamDetails(user: User,
                       teams: Seq[Team])

object TeamDetails {
  implicit val reader: XmlReader[TeamDetails] = (
    (__ \ "User").read[User],
    (__ \ "Teams" \ "Team").read(using seq[Team]),
  ).mapN(apply)
}
