package requests
import java.util.Date

import akka.http.scaladsl.model.HttpRequest
import models.{OauthTokens, RequestCreator}

case class MatchesArchiveRequest(teamId: Option[Long] = None,
                                 isYouth: Option[Boolean] = None,
                                 firstMatchDate: Option[Date] = None,
                                 lastMatchDate: Option[Date] = None,
                                 season: Option[Int] = None,
                                 includeHto: Option[Boolean] = None) extends AbstractRequest {
  override def createRequest()(implicit oauthTokens: OauthTokens): HttpRequest = {
    val map = RequestCreator.params("matchesarchive", "1.4",
      "teamID" -> teamId,
        "isYouth" -> isYouth,
        "FirstMatchDate" -> firstMatchDate,
        "LastMatchDate" -> lastMatchDate,
        "season" -> season,
        "includeHTO" -> includeHto
        )

    RequestCreator.create(map)
  }
}
