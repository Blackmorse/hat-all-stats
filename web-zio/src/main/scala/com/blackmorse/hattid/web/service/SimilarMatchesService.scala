package com.blackmorse.hattid.web.service

import com.blackmorse.hattid.web.databases.requests.matchdetails.{AnnoySimilarMatchesRequest, SimilarMatchesRequest}
import com.blackmorse.hattid.web.databases.requests.model.`match`.SimilarMatchesStats
import com.blackmorse.hattid.web.zios.CHPPServices
import com.blackmorse.hattid.web.databases.ClickhousePool.ClickhousePool
import com.blackmorse.hattid.web.models.web.HattidError
import com.blackmorse.hattid.web.models.web.matches.SingleMatch
import zio.ZIO
import zio.http.Client

class SimilarMatchesService {
  def similarMatchesStats(matchId: Long, accuracy: Double): ZIO[CHPPServices & ClickhousePool, HattidError, Option[SimilarMatchesStats]] = {
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

  def similarMatchesAnnoyStats(matchId: Long, accuracy: Int, considerTacticType: Boolean, considerTacticSkill: Boolean, considerSetPiecesLevel: Boolean): ZIO[CHPPServices & ClickhousePool, HattidError, Option[SimilarMatchesStats]] = {
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

