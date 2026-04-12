package chpp.managercompendium.models

import com.lucidchart.open.xtract.{XmlReader, __}
import cats.syntax.all._

case class LeagueLevelUnit(leagueLevelUnitId: Long,
                           leagueLevelUnitName: String)

object LeagueLevelUnit {
  implicit val reader: XmlReader[LeagueLevelUnit] = (
    (__ \ "LeagueLevelUnitId").read[Long],
    (__ \ "LeagueLevelUnitName").read[String]
  ).mapN(apply)
}
