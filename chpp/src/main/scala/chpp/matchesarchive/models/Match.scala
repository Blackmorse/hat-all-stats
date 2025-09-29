package chpp.matchesarchive.models

import java.util.Date
import cats.syntax.all._
import chpp.BaseXmlMapper
import chpp.commonmodels.MatchType
import com.lucidchart.open.xtract.XmlReader._
import com.lucidchart.open.xtract.{XmlReader, __}

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
    (__ \ "MatchType").read(`enum`(MatchType)),
    (__ \ "MatchContextId").read[Int],
    (__ \ "MatchRuleId").read[Int],
    (__ \ "CupLevel").read[Int],
    (__ \ "CupLevelIndex").read[Int],
    (__ \ "HomeGoals").read[Int],
    (__ \ "AwayGoals").read[Int],
  ).mapN(apply)
}
