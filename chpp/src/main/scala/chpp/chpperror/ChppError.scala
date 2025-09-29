package chpp.chpperror

import cats.implicits.catsSyntaxTuple10Semigroupal
import chpp.BaseXmlMapper
import com.lucidchart.open.xtract.{XmlReader, __}

import java.util.Date

case class ChppError(fileName: String,
                     version: String,
                     userId: Long,
                     fetchedDate: Date,
                     error: String,
                     errorCode: Int,
                     errorGuid: String,
                     server: String,
                     request: String,
                     lineNumber: Option[Int])

object ChppError extends BaseXmlMapper {
  implicit val reader: XmlReader[ChppError] = (
    (__ \\ "FileName").read[String],
    (__ \\ "Version").read[String],
    (__ \\ "UserID").read[Long],
    (__ \\ "FetchedDate").read[String].map(date),
    (__ \\ "Error").read[String],
    (__ \\ "ErrorCode").read[Int],
    (__ \\ "ErrorGUID").read[String],
    (__ \\ "Server").read[String],
    (__ \\ "Request").read[String],
    (__ \\ "LineNumber").read[Int].optional
  ).mapN(apply)
}
