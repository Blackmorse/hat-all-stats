package chpp.matchlineup.models

import chpp.players.models.MatchRoleId
import cats.syntax.all._
import com.lucidchart.open.xtract.{XmlReader, __}
import com.lucidchart.open.xtract.XmlReader._

case class StartingLineupPlayer(playerId: Long,
                                roleId: MatchRoleId.Value,
                                firstName: String,
                                lastName: String,
                                nickName: String,
                                behaviour: Option[Int])

object StartingLineupPlayer {
  implicit val reader: XmlReader[StartingLineupPlayer] = (
    (__ \ "PlayerID").read[Long],
    (__ \ "RoleID").read(using `enum`(MatchRoleId)),
    (__ \ "FirstName").read[String],
    (__ \ "LastName").read[String],
    (__ \ "NickName").read[String],
    (__ \ "Behaviour").read[Int].optional,
  ).mapN (apply)
}
