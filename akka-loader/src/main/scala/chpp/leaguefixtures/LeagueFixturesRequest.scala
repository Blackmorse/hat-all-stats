package chpp.leaguefixtures

import akka.http.scaladsl.model.HttpRequest
import chpp.{AbstractRequest, OauthTokens, RequestCreator}

case class LeagueFixturesRequest(leagueLevelUnitId: Option[Int] = None,
                                 season: Option[Int] = None) extends AbstractRequest {
  override def createRequest()(implicit oauthTokens: OauthTokens): HttpRequest = {
    val map = RequestCreator.params("leaguefixtures", "1.2",
    "leagueLevelUnitID" -> leagueLevelUnitId,
    "season" -> season)

    RequestCreator.create(map)
  }
}
