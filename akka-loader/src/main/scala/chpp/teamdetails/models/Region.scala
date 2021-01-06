package chpp.teamdetails.models

import cats.syntax.all._
import com.lucidchart.open.xtract.XmlReader._
import com.lucidchart.open.xtract.{XmlReader, __}


case class Region(regionId: Int,
                  regionName: String)

object Region {
  implicit val reader: XmlReader[Region] = (
    (__ \ "RegionID").read[Int],
    (__ \ "RegionName").read[String],
    ).mapN(apply _)
}
