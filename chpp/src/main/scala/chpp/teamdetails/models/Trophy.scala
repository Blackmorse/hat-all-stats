package chpp.teamdetails.models

import java.util.Date

import cats.syntax.all._
import chpp.BaseXmlMapper
import com.lucidchart.open.xtract.XmlReader._
import com.lucidchart.open.xtract.{XmlReader, __}

case class Trophy(trophyTypeId: TrophyTypeId.Value,
                  trophySeason: Int,
                  leagueLevel: Int,
                  leagueLevelUnitName: String,
                  gainedDate: Date,
                  imageUrl: Option[String])

object Trophy extends BaseXmlMapper  {
  implicit val reader: XmlReader[Trophy] = (
    (__ \ "TrophyTypeId").read(`enum`(TrophyTypeId)),
    (__ \ "TrophySeason").read[Int],
    (__ \ "LeagueLevel").read[Int],
    (__ \ "LeagueLevelUnitName").read[String],
    (__ \ "GainedDate").read[String].map(date),
    (__ \ "ImageUrl").read[String].optional,
    ).mapN(apply)
}
