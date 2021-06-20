package chpp.search

import cats.syntax.all._
import chpp.BaseXmlMapper
import com.lucidchart.open.xtract.{XmlReader, __}

case class SearchParams(searchType: Int,
                        searchString: String,
                        searchString2: String,
                        searchId: Int,
                        searchLeagueInt: Int)

object SearchParams extends BaseXmlMapper {
  implicit val reader: XmlReader[SearchParams] = (
    (__ \ "SearchType").read[Int],
    (__ \ "SearchString").read[String],
    (__ \ "SearchString2").read[String],
    (__ \ "SearchID").read[Int],
    (__ \ "SearchLeagueID").read[Int],
  ).mapN(apply _)
}
