package requests

import akka.http.scaladsl.model.HttpRequest
import models.{OauthTokens, RequestCreator}
import models.search.SearchType

case class SearchRequest(searchType: Option[SearchType.Value] = None,
                         searchString: Option[String] = None,
                         searchString2: Option[String] = None,
                         searchId: Option[Int] = None,
                         searchLeagueId: Option[Int] = None,
                         pageIndex: Option[Int] = None) extends AbstractRequest {
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
