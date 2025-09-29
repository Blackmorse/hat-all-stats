package chpp.matchlineup.models

import chpp.BaseXmlMapper
import chpp.players.models.MatchRoleId
import com.lucidchart.open.xtract.{XmlReader, __}
import com.lucidchart.open.xtract.XmlReader._
import cats.syntax.all._

case class LineupPlayer(playerId: Long,
                        roleId: MatchRoleId.Value,
                        firstName: String,
                        lastName: String,
                        nickName: String,
                        ratingStars: Option[Double],
                        ratingStarsEndOfMatch: Option[Double],
                        behaviour: Option[Int])

object LineupPlayer extends BaseXmlMapper {
  implicit val reader: XmlReader[LineupPlayer] = (
    (__ \ "PlayerID").read[Long],
    (__ \ "RoleID").read(`enum`(MatchRoleId)).default(MatchRoleId.UNKNOWN_SLOT),
    (__ \ "FirstName").read[String],
    (__ \ "LastName").read[String],
    (__ \ "NickName").read[String],
    (__ \ "RatingStars").read[String].map(double).optional,
    (__ \ "RatingStarsEndOfMatch").read[String].map(double).optional,
    (__ \ "Behaviour").read[Int].optional,
  ).mapN (apply)
}
