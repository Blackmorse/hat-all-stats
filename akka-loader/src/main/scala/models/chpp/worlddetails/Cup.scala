package models.chpp.worlddetails

import cats.syntax.all._

import com.lucidchart.open.xtract.{XmlReader, __}

case class Cup(cupId: Int,
               cupName: String,
               cupLeagueLevel: Int,
               cupLevel: Int,
               cupLevelIndex: Int,
               matchRound: Int,
               matchRoundsLeft: Int)

object Cup {
  implicit val reader: XmlReader[Cup] = (
    (__ \ "CupID").read[Int],
    (__ \ "CupName").read[String],
    (__ \ "CupLeagueLevel").read[Int],
    (__ \ "CupLevel").read[Int],
    (__ \ "CupLevelIndex").read[Int],
    (__ \ "MatchRound").read[Int],
    (__ \ "MatchRoundsLeft").read[Int]
    ).mapN(apply _)
}
