package chpp.search.models

import cats.syntax.all._
import chpp.BaseXmlMapper
import com.lucidchart.open.xtract.{XmlReader, __}

case class Result(resultId: Long, resultName: String)

object Result extends BaseXmlMapper {
  implicit val reader: XmlReader[Result] = (
    (__ \ "ResultID").read[Long],
    (__ \ "ResultName").read[String]
  ).mapN(apply _)
}