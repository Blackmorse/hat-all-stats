package chpp

import java.util.{Date, TimeZone}

abstract class BaseXmlMapper {
  private val format = {
    val f = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    f.setTimeZone(TimeZone.getTimeZone("CET"))
    f
  }

  def double(s: String): Double = s.replace(",", ".").toDouble
  def date(s: String): Date = format.parse(s)
}
