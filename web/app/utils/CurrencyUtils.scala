package utils

import chpp.worlddetails.models.Country

object CurrencyUtils {
  def currencyRate(country: Option[Country]): Double =
    country.map(_.currencyRate).getOrElse(10.0)

  def currencyName(country: Option[Country]): String =
    country.map(_.currencyName).getOrElse("$")
}
