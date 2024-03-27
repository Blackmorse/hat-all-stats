package chpp.leaguedetails

import org.apache.pekko.http.scaladsl.model.HttpRequest
import chpp.leaguedetails.models.LeagueDetails
import chpp.{AbstractRequest, OauthTokens, RequestCreator}

case class LeagueDetailsRequest(leagueUnitId: Option[Int]) extends AbstractRequest[LeagueDetails] {
  override def createRequest()(implicit oauthTokens: OauthTokens): HttpRequest = {
    val map = RequestCreator.params("leaguedetails", "1.6",
      "leagueLevelUnitID" -> leagueUnitId)

    RequestCreator.create(map)
  }
}
