package chpp.search

import akka.http.scaladsl.model.HttpRequest
import chpp.search.models.{Search, SearchType}
import chpp.{AbstractRequest, OauthTokens, RequestCreator}

case class SearchRequest(searchType: Option[SearchType.Value] = None,
                         searchString: Option[String] = None,
                         searchString2: Option[String] = None,
                         searchId: Option[Int] = None,
                         searchLeagueId: Option[Int] = None,
                         pageIndex: Option[Int] = None) extends AbstractRequest[Search] {
  override def createRequest()(implicit oauthTokens: OauthTokens): HttpRequest = {
    val map = RequestCreator.params("search", "1.2",
      "searchType" -> searchType,
      "searchString" -> searchString,
      "searchString2" -> searchString2,
      "searchID" -> searchId,
      "searchLeagueID" -> searchLeagueId,
      "pageIndex" -> pageIndex)

    RequestCreator.create(map)
  }
}
