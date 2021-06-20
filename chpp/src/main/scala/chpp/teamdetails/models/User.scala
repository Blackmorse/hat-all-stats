package chpp.teamdetails.models

import java.util.Date

import cats.syntax.all._
import chpp.BaseXmlMapper
import com.lucidchart.open.xtract.XmlReader._
import com.lucidchart.open.xtract.{XmlReader, __}

case class User(userId: Int,
                language: Option[String],
                supporterTies: String,
                loginname: String,
                name: String,
                icq: String,
                signupDate: Date,
                activationDate: Date,
                lastLoginDate: Date,
                hasManagerLicense: Boolean)

object User extends BaseXmlMapper {
  implicit val reader: XmlReader[User] = (
    (__ \ "UserID").read[Int],
    (__ \ "Language").read[String].optional,
    (__ \ "SupporterTier").read[String],
    (__ \ "Loginname").read[String],
    (__ \ "Name").read[String],
    (__ \ "ICQ").read[String],
    (__ \ "SignupDate").read[String].map(date),
    (__ \ "ActivationDate").read[String].map(date),
    (__ \ "LastLoginDate").read[String].map(date),
    (__ \ "HasManagerLicese").read[Boolean],
  ).mapN(apply _)
}
