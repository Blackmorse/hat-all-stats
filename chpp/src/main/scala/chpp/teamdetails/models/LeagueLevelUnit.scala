package chpp.teamdetails.models

import cats.syntax.all._
import com.lucidchart.open.xtract.XmlReader._
import com.lucidchart.open.xtract.{XmlReader, __}

case class LeagueLevelUnit(leagueLevelUnitId: Int,
                           leagueLevelUnitName: String,
                           leagueLevel: Int)

object LeagueLevelUnit {
  implicit val reader: XmlReader[LeagueLevelUnit] = (
    (__ \ "LeagueLevelUnitID").read[Int],
    (__ \ "LeagueLevelUnitName").read[String],
    (__ \ "LeagueLevel").read[Int],
    ).mapN(apply)
}
