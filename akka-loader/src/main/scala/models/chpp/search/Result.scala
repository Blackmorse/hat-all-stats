package models.chpp.search

import cats.syntax.all._
import com.lucidchart.open.xtract.{XmlReader, __}
import models.chpp.BaseXmlMapper

case class Result(resultId: Long, resultName: String)

object Result extends BaseXmlMapper {
  implicit val reader: XmlReader[Result] = (
    (__ \ "ResultID").read[Long],
    (__ \ "ResultName").read[String]
  ).mapN(apply _)
}