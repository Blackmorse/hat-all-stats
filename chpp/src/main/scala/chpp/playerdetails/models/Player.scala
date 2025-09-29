package chpp.playerdetails.models

import cats.syntax.all._
import com.lucidchart.open.xtract.XmlReader._
import com.lucidchart.open.xtract.{XmlReader, __}
import chpp.BaseXmlMapper

import java.util.Date

case class Player(playerId: Long,
                  firstName: String,
                  lastName: String,
                  playerNumber: Int,
                  age: Int,
                  ageDays: Int,
                  arrivalDate: Date,
                  playerForm: Int,
                  cards: Int,
                  injuryLevel: Int,
                  experience: Int,
                  motherClub: MotherClub,
                  leaderShip: Int,
                  specialty: Int,
                  nativeLeagueId: Int,
                  nativeLeagueName: String,
                  tsi: Int,
                  owningTeam: OwningTeam,
                  salary: Long,
                  careerGoals: Option[Int],
                  careerHattricks: Option[Int],
                 //A lot more options, but Scala 2x Tuples restriction
                 )

object Player extends BaseXmlMapper {
  implicit val reader: XmlReader[Player] = (
    (__ \ "PlayerID").read[Long],
    (__ \ "FirstName").read[String],
    (__ \ "LastName").read[String],
    (__ \ "PlayerNumber").read[Int],
    (__ \ "Age").read[Int],
    (__ \ "AgeDays").read[Int],
    (__ \ "ArrivalDate").read[String].map(date),
    (__ \ "PlayerForm").read[Int],
    (__ \ "Cards").read[Int],
    (__ \ "InjuryLevel").read[Int],
    (__ \ "Experience").read[Int],
    (__ \ "MotherClub").read[MotherClub],
    (__ \ "Leadership").read[Int],
    (__ \ "Specialty").read[Int],
    (__ \ "NativeLeagueID").read[Int],
    (__ \ "NativeLeagueName").read[String],
    (__ \ "TSI").read[Int],
    (__ \ "OwningTeam").read[OwningTeam],
    (__ \ "Salary").read[Long],
    (__ \ "CareerGoals").read[Int].optional,
    (__ \ "CareerHattricks").read[Int].optional,
  ).mapN(apply)
}
