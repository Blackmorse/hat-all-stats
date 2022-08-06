package chpp.playerdetails.models

import cats.syntax.all._
import com.lucidchart.open.xtract.XmlReader._
import com.lucidchart.open.xtract.{XmlReader, __}

case class MotherClub(teamId: Long, teamName: String)

object MotherClub {
  implicit val reader: XmlReader[MotherClub] = (
    (__ \ "TeamID").read[Long],
    (__ \ "TeamName").read[String]
  ).mapN(apply _)
}
