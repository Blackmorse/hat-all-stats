package chpp.matchlineup.models

import chpp.players.models.MatchRoleId
import cats.syntax.all._
import chpp.BaseXmlMapper
import com.lucidchart.open.xtract.{XmlReader, __}
import com.lucidchart.open.xtract.XmlReader._

case class Substitution(teamId: Long,
                        subjectPlayerId: Long,
                        objectPlayerId: Long,
                        orderType: Int,
                        newPositionId: MatchRoleId.Value,
                        newPositionBehaviour: Int,
                        matchMinute: Int,
                        matchPart: Int)

object Substitution {
  implicit val reader: XmlReader[Substitution] = (
    (__ \ "TeamID").read[Long],
    (__ \ "SubjectPlayerID").read[Long],
    (__ \ "ObjectPlayerID").read[Long],
    (__ \ "OrderType").read[Int],
    (__ \ "NewPositionId").read(enum(MatchRoleId)),
    (__ \ "NewPositionBehaviour").read[Int],
    (__ \ "MatchMinute").read[Int],
    (__ \ "MatchPart").read[Int],
  ).mapN(apply _)
}