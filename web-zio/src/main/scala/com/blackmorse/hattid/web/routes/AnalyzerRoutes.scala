package com.blackmorse.hattid.web.routes

import chpp.commonmodels.MatchType
import com.blackmorse.hattid.web.zios.*
import com.blackmorse.hattid.web.models.clickhouse.NearestMatch
import com.blackmorse.hattid.web.models.web.HattidError
import com.blackmorse.hattid.web.models.web.analyzer.MatchOpponentCombinedInfo
import com.blackmorse.hattid.web.models.web.matches.SingleMatch
import com.blackmorse.hattid.web.service.ChppService
import zio.*
import zio.http.*
import zio.json.*

object AnalyzerRoutes {
  private val GetTeamAnalyzer = Method.GET / "api" / "team" / "analyzer"
  private type Team = (Long, String)

  val routes: Seq[Route[CHPPServices, HattidError]] = Seq(
    GetTeamAnalyzer / "teamAndOpponentMatches" -> teamAndOpponentMatchesHandler,
    GetTeamAnalyzer / "opponentTeamMatches" -> opponentTeamMatchesHandler,
  )
  
  private def combineMatchesHandler = handler { (req: Request) =>
    for {
      firstTeamId   <- req.longParam("firstTeamId")
      firstMatchId  <- req.longParam("firstMatchId")
      secondTeamId  <- req.longParam("secondTeamId")
      secondMatchId <- req.longParam("secondMatchId")
      `match`          <- combineMatchesOptZio(
        firstTeamId = firstTeamId,
        firstMatchIdOpt = Some(firstMatchId),
        secondTeamIdOpt = Some(secondTeamId),
        secondMatchIdOpt = Some(secondMatchId))
    } yield Response.json(`match`.toJson)
  }
  
  private def opponentTeamMatchesHandler = handler { (req: Request) =>
    for {
      teamId  <- req.longParam("teamId")
      matches <- teamPlayedMatches(teamId) 
    } yield Response.json(matches.toJson)
  }

  private def teamPlayedMatches(teamId: Long): ZIO[CHPPServices, HattidError, Seq[NearestMatch]] =
    ZIO.serviceWithZIO[ChppService](_.matches(teamId))
      .map(opponentMatches => {
        opponentMatches.team.matchList
          .filter(m => m.matchType == MatchType.LEAGUE_MATCH || m.matchType == MatchType.CUP_MATCH)
          .filter(_.status == "FINISHED")
          .sortBy(_.matchDate)
          .takeRight(3)
          .map(NearestMatch.chppMatchToNearestMatch)
      })

  private def teamAndOpponentMatchesHandler = handler { (req: Request) =>
    for {
      teamId                  <- req.longParam("teamId")
      (currentTeamPlayedMatches, currentTeamNextOpponents) 
                              <- currentTeamPlayedMatchesAndUpcomingOpponents(teamId)
      opponentPlayedMatches   <- getTeamPlayedMatches(currentTeamNextOpponents.headOption)
      combinedMatchOpt        <- combineMatchesOptZio(firstTeamId = teamId,
        currentTeamPlayedMatches.lastOption.map(_.matchId),
        secondTeamIdOpt = currentTeamNextOpponents.headOption.map(_._1),
        secondMatchIdOpt = opponentPlayedMatches.lastOption.map(_.matchId))
    } yield {
      Response.json(
        MatchOpponentCombinedInfo(
          currentTeamPlayedMatches = currentTeamPlayedMatches,
          currentTeamNextOpponents = currentTeamNextOpponents,
          opponentPlayedMatches = opponentPlayedMatches,
          simulatedMatch = combinedMatchOpt
        ).toJson
      )
    }
  }

  private def getTeamPlayedMatches(teamOpt: Option[Team]): ZIO[CHPPServices, HattidError, Seq[NearestMatch]] = {
    teamOpt.map(team => {
      ZIO.serviceWithZIO[ChppService](_.matches(team._1))
        .map(opponentMatches => {
          opponentMatches.team.matchList
            .filter(m => m.matchType == MatchType.LEAGUE_MATCH || m.matchType == MatchType.CUP_MATCH)
            .filter(_.status == "FINISHED")
            .sortBy(_.matchDate)
            .takeRight(3)
            .map(NearestMatch.chppMatchToNearestMatch)
        })
    }).getOrElse(ZIO.succeed(Seq()))
  }

  private def currentTeamPlayedMatchesAndUpcomingOpponents(teamId: Long): ZIO[CHPPServices, HattidError, (Seq[NearestMatch], Seq[(Long, String)])] = {
    ZIO.serviceWithZIO[ChppService](_.matches(teamId))
      .map(matches => {
        val currentTeamPlayedMatches = matches.team.matchList
          .filter(m => m.matchType == MatchType.LEAGUE_MATCH || m.matchType == MatchType.CUP_MATCH)
          .filter(_.status == "FINISHED")
          .sortBy(_.matchDate)
          .takeRight(3)
          .map(NearestMatch.chppMatchToNearestMatch)

        val currentTeamNextOpponents = matches.team.matchList
          .filter(m => m.matchType == MatchType.LEAGUE_MATCH || m.matchType == MatchType.CUP_MATCH)
          .filter(_.status == "UPCOMING")
          .sortBy(_.matchDate)
          .take(3)
          .map(matc => {
            if (matc.homeTeam.homeTeamId == teamId) {
              (matc.awayTeam.awayTeamId, matc.awayTeam.awayTeamName)
            } else {
              (matc.homeTeam.homeTeamId, matc.homeTeam.homeTeamName)
            }
          })

        (currentTeamPlayedMatches, currentTeamNextOpponents)
      })
  }

  extension [R, E, A](zio: Option[ZIO[R, E, A]])
    private def opt: ZIO[R, E, Option[A]] =
      zio match {
        case Some(z) => z.map(Some(_))
        case None => ZIO.succeed(None)
      }

  private def combineMatchesOptZio(firstTeamId: Long,
                                   firstMatchIdOpt: Option[Long],
                                   secondTeamIdOpt: Option[Long],
                                   secondMatchIdOpt: Option[Long]): ZIO[CHPPServices, HattidError, Option[SingleMatch]] = {
    (for {
      firstMatchId <- firstMatchIdOpt
      secondTeamId <- secondTeamIdOpt
      secondMatchId <- secondMatchIdOpt
    } yield {
      val firstMatchDetailsZIO = ZIO.serviceWithZIO[ChppService](_.matchDetails(firstMatchId))
      val secondMatchDetailsZIO = ZIO.serviceWithZIO[ChppService](_.matchDetails(secondMatchId))

      firstMatchDetailsZIO <&> secondMatchDetailsZIO map { case (firstMatch, secondMatch) =>
        val firstTeam = if (firstMatch.matc.homeTeam.teamId == firstTeamId) firstMatch.matc.homeTeam else firstMatch.matc.awayTeam
        val secondTeam = if (secondMatch.matc.homeTeam.teamId == secondTeamId) secondMatch.matc.homeTeam else secondMatch.matc.awayTeam

        SingleMatch.fromHomeAwayTeams(
          homeTeam = firstTeam,
          awayTeam = secondTeam,
          homeGoals = None,
          awayGoals = None,
          matchId = None)
      }
    }).opt
  }
}
