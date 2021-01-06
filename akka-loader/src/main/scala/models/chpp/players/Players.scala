package models.chpp.players

import cats.syntax.all._
import com.lucidchart.open.xtract.{XmlReader, __}
import com.lucidchart.open.xtract.XmlReader._

case class Players(isYouth: Boolean,
                   actionType: String,
                   isPlayingMatch: Boolean,
                   team: Team)

object Players {
  implicit val reader: XmlReader[Players] = (
    (__ \ "IsYouth").read[Boolean],
    (__ \ "ActionType").read[String],
    (__ \ "IsPlayingMatch").read[Boolean],
    (__ \ "Team").read[Team],
    ).mapN(apply _)
}
