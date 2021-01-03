package models.worlddetails

import cats.syntax.all._
import com.lucidchart.open.xtract.{XmlReader, __}
import models.BaseXmlMapper

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
    ).mapN(apply _)
}