package chpp.matchdetails.models

import cats.syntax.all._
import com.lucidchart.open.xtract.XmlReader._
import com.lucidchart.open.xtract.{XmlReader, __}

case class Booking(bookingPlayerId: Long,
                   bookingPlayerName: String,
                   bookingTeamId: Int,
                   bookingType: BookingType.Value,
                   bookingMinute: Int,
                   matchPart: Int)

object Booking {
  implicit val reader: XmlReader[Booking] = (
    (__ \ "BookingPlayerID").read[Long],
    (__ \ "BookingPlayerName").read[String],
    (__ \ "BookingTeamID").read[Int],
    (__ \ "BookingType").read(using `enum`(BookingType)),
    (__ \ "BookingMinute").read[Int],
    (__ \ "MatchPart").read[Int],
  ).mapN(apply)
}
