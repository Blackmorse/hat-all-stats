package chpp.managercompendium.models

import cats.syntax.all._
import com.lucidchart.open.xtract.{XmlReader, __}
import chpp.BaseXmlMapper

case class Currency(currencyName: String, currencyRate: Double)

object Currency extends BaseXmlMapper {
  implicit val reader: XmlReader[Currency] = (
    (__ \ "CurrencyName").read[String],
    (__ \ "CurrencyRate").read[String].map(double)
  ).mapN(apply)
}
