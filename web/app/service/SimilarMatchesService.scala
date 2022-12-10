package service

import chpp.matchdetails.MatchDetailsRequest
import chpp.matchdetails.models.MatchDetails
import databases.dao.RestClickhouseDAO
import databases.requests.matchdetails.SimilarMatchesRequest
import databases.requests.model.`match`.SimilarMatchesStats
import models.web.matches.SingleMatch
import webclients.ChppClient

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class SimilarMatchesService @Inject()
                                     (chppClient: ChppClient,
                                       implicit val restClickhouseDAO: RestClickhouseDAO){
  def similarMatchesStats(matchId: Long, accuracy: Double): Future[Option[SimilarMatchesStats]] = {
    chppClient.executeUnsafe[MatchDetails, MatchDetailsRequest](MatchDetailsRequest(matchId = Some(matchId)))
      .flatMap(matchDetails => {
        val singleMatch = SingleMatch.fromHomeAwayTeams(
          homeTeam = matchDetails.matc.homeTeam,
          awayTeam = matchDetails.matc.awayTeam,
          homeGoals = Some(matchDetails.matc.homeTeam.goals),
          awayGoals = Some(matchDetails.matc.awayTeam.goals),
          matchId = Some(matchDetails.matc.matchId))

        SimilarMatchesRequest.execute(singleMatch, accuracy)
      })
  }
}
