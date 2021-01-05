package models.chpp.matchdetails

import java.util.Date

import cats.syntax.all._
import com.lucidchart.open.xtract.XmlReader._
import com.lucidchart.open.xtract.{XmlReader, __}
import models.chpp.BaseXmlMapper
import models.chpp.matchesarchive.MatchType

case class Match(matchId: Long,
                 matchType: MatchType.Value,
                 matchContextId: Int,
                 matchRuleId: Int,
                 cupLevel: Int,
                 cupLevelIndex: Int,
                 matchDate: Date,
                 finishedDate: Date,
                 addedMinutes: Int,
                 homeTeam: HomeAwayTeam,
                 awayTeam: HomeAwayTeam,
                 arena: Arena,
                 scorers: Seq[Goal],
                 bookings: Seq[Booking],
                 injuries: Seq[Injury])

object Match extends BaseXmlMapper {
  implicit val reader: XmlReader[Match] = (
    (__ \ "MatchID").read[Long],
    (__ \ "MatchType").read(enum(MatchType)),
    (__ \ "MatchContextId").read[Int],
    (__ \ "MatchRuleId").read[Int],
    (__ \ "CupLevel").read[Int],
    (__ \ "CupLevelIndex").read[Int],
    (__ \ "MatchDate").read[String].map(date),
    (__ \ "FinishedDate").read[String].map(date),
    (__ \ "AddedMinutes").read[Int],
    (__ \ "HomeTeam").read[HomeAwayTeam](HomeTeam.reader),
    (__ \ "AwayTeam").read[HomeAwayTeam](AwayTeam.reader),
    (__ \ "Arena").read[Arena],
    (__ \ "Scorers" \ "Goal").read(seq[Goal]),
    (__ \ "Bookings" \ "Booking").read(seq[Booking]),
    (__ \ "Injuries" \ "Injury").read(seq[Injury]),
    ).mapN(apply _)
}
