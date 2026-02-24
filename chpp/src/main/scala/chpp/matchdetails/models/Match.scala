package chpp.matchdetails.models

import java.util.Date
import cats.syntax.all._
import chpp.BaseXmlMapper
import chpp.commonmodels.MatchType
import com.lucidchart.open.xtract.XmlReader._
import com.lucidchart.open.xtract.{XmlReader, __}

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
    (__ \ "MatchType").read(using `enum`(MatchType)),
    (__ \ "MatchContextId").read[Int],
    (__ \ "MatchRuleId").read[Int],
    (__ \ "CupLevel").read[Int],
    (__ \ "CupLevelIndex").read[Int],
    (__ \ "MatchDate").read[String].map(date),
    (__ \ "FinishedDate").read[String].map(date),
    (__ \ "AddedMinutes").read[Int],
    (__ \ "HomeTeam").read[HomeAwayTeam](using HomeTeam.reader),
    (__ \ "AwayTeam").read[HomeAwayTeam](using AwayTeam.reader),
    (__ \ "Arena").read[Arena],
    (__ \ "Scorers" \ "Goal").read(using seq[Goal]),
    (__ \ "Bookings" \ "Booking").read(using seq[Booking]),
    (__ \ "Injuries" \ "Injury").read(using seq[Injury]),
    ).mapN(apply)
}
