package models.search

import models.BaseXmlMapper
import cats.syntax.all._
import com.lucidchart.open.xtract.{XmlReader, __}

case class Result(resultId: Long, resultName: String)

object Result extends BaseXmlMapper {
  implicit val reader: XmlReader[Result] = (
    (__ \ "ResultID").read[Long],
    (__ \ "ResultName").read[String]
  ).mapN(apply _)
}