package chpp.managercompendium.models

import com.lucidchart.open.xtract.{XmlReader, __}
import cats.syntax.all._

case class YouthTeam(youthTeamId: Long,
                     youthTeamName: String,
                     youthLeague: YouthLeague)

object YouthTeam {
  implicit val reader: XmlReader[YouthTeam] = (
    (__ \ "YouthTeamId").read[Long],
    (__ \ "YouthTeamName").read[String],
    (__ \ "YouthLeague").read[YouthLeague]
  ).mapN(apply)
}
