package service

import databases.RestClickhouseDAO
import databases.requests.matchdetails.SimilarMatchesRequest
import databases.requests.model.`match`.SimilarMatchesStats
import hattrick.Hattrick

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class SimilarMatchesService @Inject()
                                     (hattrick: Hattrick,
                                       implicit val restClickhouseDAO: RestClickhouseDAO){
  def similarMatchesStats(matchId: Long, accuracy: Double): Future[Option[SimilarMatchesStats]] = {
    Future(hattrick.api.matchDetails().matchId(matchId).execute())
      .flatMap(matchDetails => SimilarMatchesRequest.execute(matchDetails, accuracy))
  }
}
