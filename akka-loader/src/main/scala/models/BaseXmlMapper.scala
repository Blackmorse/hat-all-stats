package models

import java.util.Date

abstract class BaseXmlMapper {
  private val format = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

  def double(s: String): Double = s.replace(",", ".").toDouble
  def date(s: String): Date = format.parse(s)
}
