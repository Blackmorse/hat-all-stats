package chpp.players.models

import java.util.Date

import cats.syntax.all._
import chpp.BaseXmlMapper
import com.lucidchart.open.xtract.XmlReader._
import com.lucidchart.open.xtract.{XmlReader, __}

case class PlayerPart(playerId: Long,
                      firstName: String,
                      nickName: String,
                      lastName: String,
                      playerNumber: Int,
                      age: Int,
                      ageDays: Int,
                      arrivalDate: Date,
                      ownerNotes: String,
                      TSI: Int,
                      playerForm: Int,
                      statement: String,
                      experience: Int,
                      loyalty: Int,
                      motherClubBonus: Boolean,
                      leadership: Int,
                      salary: Int,
                      isAbroad: Int,
                      aggreeability: Int,
                      aggressiveness: Int,
                      honesty: Int)

object PlayerPart extends BaseXmlMapper {
  implicit val reader: XmlReader[PlayerPart] = (
    (__ \ "PlayerID").read[Long],
    (__ \ "FirstName").read[String],
    (__ \ "NickName").read[String],
    (__ \ "LastName").read[String],
    (__ \ "PlayerNumber").read[Int],
    (__ \ "Age").read[Int],
    (__ \ "AgeDays").read[Int],
    (__ \ "ArrivalDate").read[String].map(date),
    (__ \ "OwnerNotes").read[String],
    (__ \ "TSI").read[Int],
    (__ \ "PlayerForm").read[Int],
    (__ \ "Statement").read[String],
    (__ \ "Experience").read[Int],
    (__ \ "Loyalty").read[Int],
    (__ \ "MotherClubBonus").read[Boolean],
    (__ \ "Leadership").read[Int],
    (__ \ "Salary").read[Int],
    (__ \ "IsAbroad").read[Int],
    (__ \ "Agreeability").read[Int],
    (__ \ "Aggressiveness").read[Int],
    (__ \ "Honesty").read[Int],
  ).mapN(apply _)
}

case class Player(playerPart: PlayerPart,
                  leagueGoals: Int,
                  cupGoals: Int,
                  friendliesGoals: Int,
                  careerGoals: Int,
                  careerHattricks: Int,
                  matchesCurrentTeam: Int,
                  goalsCurrentTeam: Int,
                  speciality: Int,
                  transferListed: Int,
                  nationalTeamId: Int,
                  countryId: Int,
                  caps: Int,
                  capsU20: Int,
                  cards: Int,
                  injuryLevel: Option[Int],
                  staminaSkill: Int,
                  trainerData: Option[TrainerData],
                  lastMatch: LastMatch)

object Player extends BaseXmlMapper{
  implicit val reader: XmlReader[Player] = (
      (__).read[PlayerPart],
      (__ \ "LeagueGoals").read[Int],
      (__ \ "CupGoals").read[Int],
      (__ \ "FriendliesGoals").read[Int],
      (__ \ "CareerGoals").read[Int],
      (__ \ "CareerHattricks").read[Int],
      (__ \ "MatchesCurrentTeam").read[Int],
      (__ \ "GoalsCurrentTeam").read[Int],
      (__ \ "Specialty").read[Int],
      (__ \ "TransferListed").read[Int],
      (__ \ "NationalTeamID").read[Int],
      (__ \ "CountryID").read[Int],
      (__ \ "Caps").read[Int],
      (__ \ "CapsU20").read[Int],
      (__ \ "Cards").read[Int],
      (__ \ "InjuryLevel").read[Int].optional,
      (__ \ "StaminaSkill").read[Int],
      (__ \ "TrainerData").read[TrainerData].optional,
      (__ \ "LastMatch").read[LastMatch],
    ).mapN(apply _)
}
