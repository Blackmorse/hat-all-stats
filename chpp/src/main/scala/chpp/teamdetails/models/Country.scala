package chpp.teamdetails.models

import cats.syntax.all._
import com.lucidchart.open.xtract.XmlReader._
import com.lucidchart.open.xtract.{XmlReader, __}

case class Country(countryId: Int,
                   countryName: String)

object Country {
  implicit val reader: XmlReader[Country] = (
    (__ \ "CountryID").read[Int],
    (__ \ "CountryName").read[String],
    ).mapN(apply)
}
