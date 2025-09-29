package chpp.worlddetails.models

import com.lucidchart.open.xtract.XmlReader._
import com.lucidchart.open.xtract.{XmlReader, __}

case class WorldDetails(leagueList: Seq[League])

object WorldDetails {
  implicit val reader: XmlReader[WorldDetails] = (
    (__ \ "LeagueList" \ "League").read(using seq[League])
  ).map(apply)
}
