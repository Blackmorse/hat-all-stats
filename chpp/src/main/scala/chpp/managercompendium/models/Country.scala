package chpp.managercompendium.models

import cats.syntax.all.*
import chpp.teamdetails.models.Country as apply
import com.lucidchart.open.xtract.XmlReader.*
import com.lucidchart.open.xtract.{XmlReader, __}

case class Country(countryId: Int,
                   countryName: String)

object Country {
  implicit val reader: XmlReader[Country] = (
    (__ \ "CountryId").read[Int],
    (__ \ "CountryName").read[String],
  ).mapN(apply)
}
