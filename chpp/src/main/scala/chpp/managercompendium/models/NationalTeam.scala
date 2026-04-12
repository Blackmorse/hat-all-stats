package chpp.managercompendium.models

case class NationalTeam(nationalTeamId: Long, nationalTeamName: String)

object NationalTeam {
  import com.lucidchart.open.xtract.{XmlReader, __}
  import cats.syntax.all._

  implicit val reader: XmlReader[NationalTeam] = (
    (__ \ "NationalTeamId").read[Long],
    (__ \ "NationalTeamName").read[String]
  ).mapN(apply)
}
