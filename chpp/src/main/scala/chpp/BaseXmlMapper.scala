package chpp

import java.text.SimpleDateFormat
import java.util.{Date, TimeZone}

abstract class BaseXmlMapper {
  private val format = ThreadLocal.withInitial(() => {
      val f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
      f.setTimeZone(TimeZone.getTimeZone("CET"))
      f
})

  def double(s: String): Double = s.replace(",", ".").toDouble
  def date(s: String): Date = format.get().parse(s)
}
