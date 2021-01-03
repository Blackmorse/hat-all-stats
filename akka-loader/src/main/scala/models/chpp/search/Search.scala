package models.chpp.search

import com.lucidchart.open.xtract.XmlReader._
import com.lucidchart.open.xtract.{XmlReader, __}
import cats.syntax.all._
import models.chpp.BaseXmlMapper

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
    ).mapN(apply _)
}