package chpp.search

import chpp.AbstractRequest
import chpp.search.models.{Search, SearchType}

import java.net.URLEncoder

case class SearchRequest(searchType: Option[SearchType.Value] = None,
                         searchString: Option[String] = None,
                         searchString2: Option[String] = None,
                         searchId: Option[Int] = None,
                         searchLeagueId: Option[Int] = None,
                         pageIndex: Option[Int] = None) extends AbstractRequest[Search]("search", "1.2",
  "searchType" -> searchType,
  "searchString" -> searchString.map(s => URLEncoder.encode(s, "UTF-8").replace("+", "%20")),
  "searchString2" -> searchString2.map(s => URLEncoder.encode(s, "UTF-8").replace("+", "%20")),
  "searchID" -> searchId,
  "searchLeagueID" -> searchLeagueId,
  "pageIndex" -> pageIndex)
