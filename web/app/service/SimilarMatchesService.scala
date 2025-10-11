package service

import chpp.AuthConfig
import databases.dao.RestClickhouseDAO
import databases.requests.ClickhouseRequest.DBIO
import databases.requests.matchdetails.{AnnoySimilarMatchesRequest, SimilarMatchesRequest}
import databases.requests.model.`match`.SimilarMatchesStats
import models.web.HattidError
import models.web.matches.SingleMatch
import webclients.ChppClient
import zio.ZIO

import javax.inject.{Inject, Singleton}

@Singleton
class SimilarMatchesService @Inject() {
  def similarMatchesStats(matchId: Long, accuracy: Double): ZIO[AuthConfig & ChppService & RestClickhouseDAO, HattidError, Option[SimilarMatchesStats]] = {
    for {
      chppService  <- ZIO.service[ChppService]
      matchDetails <- chppService.matchDetails(matchId)
      singleMatch  = SingleMatch.fromHomeAwayTeams(
        homeTeam = matchDetails.matc.homeTeam,
        awayTeam = matchDetails.matc.awayTeam,
        homeGoals = Some(matchDetails.matc.homeTeam.goals),
        awayGoals = Some(matchDetails.matc.awayTeam.goals),
        matchId = Some(matchDetails.matc.matchId))
      res <- SimilarMatchesRequest.execute(singleMatch, accuracy)
    } yield res
  }

  def similarMatchesAnnoyStats(matchId: Long, accuracy: Int, considerTacticType: Boolean, considerTacticSkill: Boolean, considerSetPiecesLevel: Boolean): ZIO[AuthConfig & ChppService & RestClickhouseDAO, HattidError, Option[SimilarMatchesStats]] = {
    for {
      chppService  <- ZIO.service[ChppService]
      matchDetails <- chppService.matchDetails(matchId)
      singleMatch  = SingleMatch.fromHomeAwayTeams(
        homeTeam = matchDetails.matc.homeTeam,
        awayTeam = matchDetails.matc.awayTeam,
        homeGoals = Some(matchDetails.matc.homeTeam.goals),
        awayGoals = Some(matchDetails.matc.awayTeam.goals),
        matchId = Some(matchDetails.matc.matchId))
      res <- AnnoySimilarMatchesRequest.execute(singleMatch, accuracy, considerTacticType, considerTacticSkill, considerSetPiecesLevel)
    } yield res
  }
}

