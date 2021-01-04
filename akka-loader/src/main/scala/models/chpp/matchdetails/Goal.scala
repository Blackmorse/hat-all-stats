package models.chpp.matchdetails

import cats.syntax.all._
import com.lucidchart.open.xtract.XmlReader._
import com.lucidchart.open.xtract.{XmlReader, __}

case class Goal(scorerPlayerId: Long,
                scorerPlayerName: String,
                scorerTeamId: Int,
                scorerHomeGoals: Int,
                scorerAwayGoals: Int,
                scorerMinute: Int,
                matchPart: Int)

object Goal {
  implicit val reader: XmlReader[Goal] = (
    (__ \ "ScorerPlayerID").read[Long],
    (__ \ "ScorerPlayerName").read[String],
    (__ \ "ScorerTeamID").read[Int],
    (__ \ "ScorerHomeGoals").read[Int],
    (__ \ "ScorerAwayGoals").read[Int],
    (__ \ "ScorerMinute").read[Int],
    (__ \ "MatchPart").read[Int],
  ).mapN(apply _)
}