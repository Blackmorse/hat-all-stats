package models.chpp.matchdetails

import cats.syntax.all._
import com.lucidchart.open.xtract.XmlReader._
import com.lucidchart.open.xtract.{XmlReader, __}

case class MatchDetails(matc: Match)

object MatchDetails {
  implicit val reader: XmlReader[MatchDetails] = (
    (__ \ "Match").read[Match],
  ).map(apply _)
}
