package service

import chpp.matchdetails.MatchDetailsRequest
import chpp.matchdetails.models.MatchDetails
import databases.dao.RestClickhouseDAO
import databases.requests.matchdetails.SimilarMatchesRequest
import databases.requests.model.`match`.SimilarMatchesStats
import webclients.ChppClient

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class SimilarMatchesService @Inject()
                                     (chppClient: ChppClient,
                                       implicit val restClickhouseDAO: RestClickhouseDAO){
  def similarMatchesStats(matchId: Long, accuracy: Double): Future[Option[SimilarMatchesStats]] = {
    chppClient.execute[MatchDetails, MatchDetailsRequest](MatchDetailsRequest(matchId = Some(matchId)))
      .flatMap(matchDetails => SimilarMatchesRequest.execute(matchDetails, accuracy))
  }
}
