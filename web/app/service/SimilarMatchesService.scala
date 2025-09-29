package service

import databases.dao.RestClickhouseDAO
import databases.requests.ClickhouseRequest.DBIO
import databases.requests.matchdetails.{AnnoySimilarMatchesRequest, SimilarMatchesRequest}
import databases.requests.model.`match`.SimilarMatchesStats
import models.web.matches.SingleMatch
import webclients.ChppClient

import javax.inject.{Inject, Singleton}

@Singleton
class SimilarMatchesService @Inject()
                                     (chppClient: ChppClient,
                                      chppService: ChppService,
                                      implicit val restClickhouseDAO: RestClickhouseDAO){
  def similarMatchesStats(matchId: Long, accuracy: Double): DBIO[Option[SimilarMatchesStats]] = {
    for {
      matchDetails <- chppService.matchDetails(matchId)
      singleMatch = SingleMatch.fromHomeAwayTeams(
        homeTeam = matchDetails.matc.homeTeam,
        awayTeam = matchDetails.matc.awayTeam,
        homeGoals = Some(matchDetails.matc.homeTeam.goals),
        awayGoals = Some(matchDetails.matc.awayTeam.goals),
        matchId = Some(matchDetails.matc.matchId))
      res <- SimilarMatchesRequest.execute(singleMatch, accuracy)
    } yield res
  }

  def similarMatchesAnnoyStats(matchId: Long, accuracy: Int, considerTacticType: Boolean, considerTacticSkill: Boolean, considerSetPiecesLevel: Boolean): DBIO[Option[SimilarMatchesStats]] = {
    for {
      matchDetails <- chppService.matchDetails(matchId)
      singleMatch = SingleMatch.fromHomeAwayTeams(
        homeTeam = matchDetails.matc.homeTeam,
        awayTeam = matchDetails.matc.awayTeam,
        homeGoals = Some(matchDetails.matc.homeTeam.goals),
        awayGoals = Some(matchDetails.matc.awayTeam.goals),
        matchId = Some(matchDetails.matc.matchId))
      res <- AnnoySimilarMatchesRequest.execute(singleMatch, accuracy, considerTacticType, considerTacticSkill, considerSetPiecesLevel)
    } yield res
  }
}

