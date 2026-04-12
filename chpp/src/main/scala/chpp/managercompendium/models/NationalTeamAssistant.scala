package chpp.managercompendium.models

case class NationalTeamAssistant(nationalTeam: NationalTeam)

object NationalTeamAssistant {
  import com.lucidchart.open.xtract.{XmlReader, __}
  import cats.syntax.all._

  implicit val reader: XmlReader[NationalTeamAssistant] = (
    (__ \ "NationalTeam").read[NationalTeam]
  ).map(apply)
}
