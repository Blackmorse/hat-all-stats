package chpp.worlddetails.models

import cats.syntax.all._
import chpp.BaseXmlMapper
import com.lucidchart.open.xtract.{XmlReader, __}

case class Country(countryId: Int,
                   countryName: String,
                   currencyName: String,
                   currencyRate: Double,
                   countryCode: String,
                   dateFormat: String,
                   timeFormat: String)

object Country extends BaseXmlMapper{
  implicit val reader: XmlReader[Country] = (
      (__ \ "CountryID").read[Int],
      (__ \ "CountryName").read[String],
      (__ \ "CurrencyName").read[String],
      (__ \ "CurrencyRate").read[String].map(double),
      (__ \ "CountryCode").read[String],
      (__ \ "DateFormat").read[String],
      (__ \ "TimeFormat").read[String]
    ).mapN(apply)
}