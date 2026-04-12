package chpp.managercompendium.models

import com.lucidchart.open.xtract.{XmlReader, __}
import com.lucidchart.open.xtract.XmlReader._
import cats.syntax.all._

case class Manager(userId: Long,
                   loginName: String,
                   supporterTier: String,
                   language: Language,
                   country: Country,
                   currency: Currency,
                   teams: Seq[Team],
                   nationalTeamCoach: Option[NationalTeamCoach],
                   nationalTeamAssistant: Option[NationalTeamAssistant],
                   avatar: Avatar)

object Manager {
  implicit val reader: XmlReader[Manager] = (
    (__ \ "UserId").read[Long],
    (__ \ "Loginname").read[String],
    (__ \ "SupporterTier").read[String],
    (__ \ "Language").read[Language],
    (__ \ "Country").read[Country],
    (__ \ "Currency").read[Currency],
    (__ \ "Teams" \ "Team").read(using seq[Team]),
    (__ \ "NationalTeamCoach").read[Option[NationalTeamCoach]],
    (__ \ "NationalTeamAssistant").read[Option[NationalTeamAssistant]],
    (__ \ "Avatar").read[Avatar]
  ).mapN(apply)
}