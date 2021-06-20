package chpp.leaguedetails.models

import cats.syntax.all._
import chpp.BaseXmlMapper
import com.lucidchart.open.xtract.XmlReader._
import com.lucidchart.open.xtract.{XmlReader, __}


case class LeagueDetails(leagueId: Int,
                         leagueName: String,
                         leagueLevel: String,
                         maxLevel: Int,
                         leagueLevelUnitId: Int,
                         leagueLevelUnitName: String,
                         currentMatchRound: Int,
                         teams: Seq[Team])

object LeagueDetails extends BaseXmlMapper {
  implicit val reader: XmlReader[LeagueDetails] = (
    (__ \ "LeagueID").read[Int],
    (__ \ "LeagueName").read[String],
    (__ \ "LeagueLevel").read[String],
    (__ \ "MaxLevel").read[Int],
    (__ \ "LeagueLevelUnitID").read[Int],
    (__ \ "LeagueLevelUnitName").read[String],
    (__ \ "CurrentMatchRound").read[Int],
    (__ \ "Team").read(seq[Team]),
    ).mapN(apply _)
}
