package chpp.search.models

import cats.syntax.all._
import chpp.BaseXmlMapper
import chpp.search.SearchParams
import com.lucidchart.open.xtract.XmlReader._
import com.lucidchart.open.xtract.{XmlReader, __}

case class Search(searchParams: SearchParams,
                  pageIndex: Int,
                  pages: Int,
                  searchResults: Seq[Result])

object Search extends BaseXmlMapper {
  implicit val reader: XmlReader[Search] = (
    (__ \ "SearchParams").read[SearchParams],
    (__ \ "PageIndex").read[Int],
    (__ \ "Pages").read[Int],
    (__ \ "SearchResults" \ "Result").read(seq[Result])
    ).mapN(apply)
}