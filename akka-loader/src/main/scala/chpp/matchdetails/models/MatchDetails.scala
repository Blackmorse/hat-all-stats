package chpp.matchdetails.models

import com.lucidchart.open.xtract.{XmlReader, __}

case class MatchDetails(matc: Match)

object MatchDetails {
  implicit val reader: XmlReader[MatchDetails] = (
    (__ \ "Match").read[Match],
  ).map(apply _)
}
