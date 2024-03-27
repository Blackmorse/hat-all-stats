package chpp.matches

import org.apache.pekko.http.scaladsl.model.HttpRequest
import chpp.{AbstractRequest, OauthTokens, RequestCreator}
import chpp.matches.models.Matches

import java.util.Date

case class MatchesRequest(teamId: Option[Long] = None,
                     isYouth: Option[Boolean] = None,
                     lastMatchDate: Option[Date] = None) extends AbstractRequest[Matches] {
  override def createRequest()(implicit oauthTokens: OauthTokens): HttpRequest = {
    val map = RequestCreator.params("matches", "2.8",
      "TeamID" -> teamId,
      "isYouth" -> isYouth,
      "LastMatchDate" -> lastMatchDate)

    RequestCreator.create(map)
  }
}
