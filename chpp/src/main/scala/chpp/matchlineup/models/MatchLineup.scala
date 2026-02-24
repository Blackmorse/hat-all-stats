package chpp.matchlineup.models

import chpp.commonmodels.MatchType
import chpp.matchesarchive.models.{AwayTeam, HomeTeam}
import chpp.teamdetails.models.Arena
import com.lucidchart.open.xtract.{XmlReader, __}
import com.lucidchart.open.xtract.XmlReader._
import cats.syntax.all._

case class MatchLineup(homeTeam: HomeTeam,
                       awayTeam: AwayTeam,
                       matchType: MatchType.Value,
                       matchContextId: Int,
                       arena: Arena,
                       team: Team
                      )

object MatchLineup {
  implicit val reader: XmlReader[MatchLineup] = (
    (__ \ "HomeTeam").read[HomeTeam],
    (__ \ "AwayTeam").read[AwayTeam],
    (__ \ "MatchType").read(using `enum`(MatchType)),
    (__ \ "MatchContextId").read[Int],
    (__ \ "Arena").read[Arena],
    (__ \ "Team").read[Team]
  ).mapN(apply)
}
