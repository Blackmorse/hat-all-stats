package models.chpp.matchesarchive

import java.util.Date

import cats.syntax.all._
import com.lucidchart.open.xtract.{XmlReader, __}
import com.lucidchart.open.xtract.XmlReader._
import models.chpp.BaseXmlMapper

case class Match(matchId: Long,
                 homeTeam: HomeTeam,
                 awayTeam: AwayTeam,
                 matchDate: Date,
                 matchType: MatchType.Value,
                 matchContextId: Int,
                 matchRuleId: Int,
                 cupLevel: Int,
                 cupLevelIndex: Int,
                 homeGoals: Int,
                 awayGoals: Int
                )

object Match extends BaseXmlMapper {
  implicit val reader: XmlReader[Match] = (
    (__ \ "MatchID").read[Long],
    (__ \ "HomeTeam").read[HomeTeam],
    (__ \ "AwayTeam").read[AwayTeam],
    (__ \ "MatchDate").read[String].map(date),
    (__ \ "MatchType").read(enum(MatchType)),
    (__ \ "MatchContextId").read[Int],
    (__ \ "MatchRuleId").read[Int],
    (__ \ "CupLevel").read[Int],
    (__ \ "CupLevelIndex").read[Int],
    (__ \ "HomeGoals").read[Int],
    (__ \ "AwayGoals").read[Int],
  ).mapN(apply _)
}
