package chpp.managercompendium.models

case class NationalTeamCoach(nationalTeam: NationalTeam)

object NationalTeamCoach {
  import com.lucidchart.open.xtract.{XmlReader, __}
  import cats.syntax.all._

  implicit val reader: XmlReader[NationalTeamCoach] = (
    (__ \ "NationalTeam").read[NationalTeam]
  ).map(apply)
}
