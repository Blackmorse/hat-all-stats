package chpp.matchdetails.models

import cats.syntax.all._
import com.lucidchart.open.xtract.XmlReader._
import com.lucidchart.open.xtract.{XmlReader, __}

case class Injury(injuryPlayerId: Long,
                  injuryPlayerName: String,
                  injuryTeamId: Int,
                  injuryType: InjuryType.Value,
                  injuryMinute: Int,
                  matchPart: Int)

object Injury {
  implicit val reader: XmlReader[Injury] = (
    (__ \ "InjuryPlayerID").read[Long],
    (__ \ "InjuryPlayerName").read[String],
    (__ \ "InjuryTeamID").read[Int],
    (__ \ "InjuryType").read(using `enum`(InjuryType)),
    (__ \ "InjuryMinute").read[Int],
    (__ \ "MatchPart").read[Int],
  ).mapN(apply)
}
