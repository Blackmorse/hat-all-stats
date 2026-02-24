package chpp.matches.models

import chpp.commonmodels.MatchType
import cats.syntax.all._
import chpp.BaseXmlMapper
import com.lucidchart.open.xtract.XmlReader._
import com.lucidchart.open.xtract.{XmlReader, __}

import java.util.Date

case class Match(matchId: Long,
                 homeTeam: HomeTeam,
                 awayTeam: AwayTeam,
                 matchDate: Date,
                 sourceSystem: String,
                 matchType: MatchType.Value,
                 matchContextId: Int,
                 cupLevel: Int,
                 cupLevelIndex: Int,
                 homeGoals: Option[Int],
                 awayGoals: Option[Int],
                 status: String)

object Match extends BaseXmlMapper {
  implicit val reader: XmlReader[Match] = (
    (__ \ "MatchID").read[Long],
    (__ \ "HomeTeam").read[HomeTeam],
    (__ \ "AwayTeam").read[AwayTeam],
    (__ \ "MatchDate").read[String].map(date),
    (__ \ "SourceSystem").read[String],
    (__ \ "MatchType").read(using `enum`(MatchType)),
    (__ \ "MatchContextId").read[Int],
    (__ \ "CupLevel").read[Int],
    (__ \ "CupLevelIndex").read[Int],
    (__ \ "HomeGoals").read[Int].optional,
    (__ \ "AwayGoals").read[Int].optional,
    (__ \ "Status").read[String]
  ).mapN(apply)
}
