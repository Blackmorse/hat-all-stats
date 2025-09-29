package chpp.players.models

import java.util.Date

import cats.syntax.all._
import chpp.BaseXmlMapper
import com.lucidchart.open.xtract.XmlReader._
import com.lucidchart.open.xtract.{XmlReader, __}

case class LastMatch(date: Date,
                     matchId: Long,
                     positionCode: MatchRoleId.Value,
                     playedMinutes: Int,
                     rating: Double,
                     ratingEndOfMatch: Double)

object LastMatch extends BaseXmlMapper {
  implicit val reader: XmlReader[LastMatch] = (
      (__ \ "Date").read[String].map(date),
      (__ \ "MatchId").read[Long],
      (__ \ "PositionCode").read(`enum`(MatchRoleId)),
      (__ \ "PlayedMinutes").read[Int],
      (__ \ "Rating").read[String].map(double),
      (__ \ "RatingEndOfGame").read[String].map(double),
    ).mapN(apply)
}
