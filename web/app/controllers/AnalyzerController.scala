package controllers

import cache.ZioCacheModule.HattidEnv
import chpp.AuthConfig
import chpp.commonmodels.MatchType
import models.clickhouse.NearestMatch
import models.web.HattidError
import models.web.analyzer.MatchOpponentCombinedInfo
import models.web.matches.SingleMatch
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import service.ChppService
import zio.ZIO

import javax.inject.Inject

class AnalyzerController @Inject()(val controllerComponents: ControllerComponents,
                                   val hattidEnvironment: zio.ZEnvironment[HattidEnv]) extends RestController(hattidEnvironment) {
  type Team = (Long, String)

  def combineMatches(firstTeamId: Long, firstMatchId: Long,
                     secondTeamId: Long, secondMatchId: Long): Action[AnyContent] = asyncZio {
    combineMatchesOptZio(firstTeamId, Some(firstMatchId), Some(secondTeamId), Some(secondMatchId))
  }

  def opponentTeamMatches(opponentTeamId: Long): Action[AnyContent] = asyncZio {
    teamPlayedMatches(opponentTeamId)
  }

  def currentTeamAndOpponentTeamMatches(teamId: Long): Action[AnyContent] = asyncZio {
    for {
      (currentTeamPlayedMatches, currentTeamNextOpponents) <- currentTeamPlayedMatchesAndUpcomingOpponents(teamId)
      opponentPlayedMatches <- getTeamPlayedMatches(currentTeamNextOpponents.headOption)
      combinedMatchOpt      <- combineMatchesOptZio(firstTeamId = teamId,
                                                  currentTeamPlayedMatches.lastOption.map(_.matchId),
                                                  secondTeamIdOpt = currentTeamNextOpponents.headOption.map(_._1),
                                                  secondMatchIdOpt = opponentPlayedMatches.lastOption.map(_.matchId))
    } yield {
      MatchOpponentCombinedInfo(
        currentTeamPlayedMatches = currentTeamPlayedMatches,
        currentTeamNextOpponents = currentTeamNextOpponents,
        opponentPlayedMatches = opponentPlayedMatches,
        simulatedMatch = combinedMatchOpt
      )
    }
  }

  extension [R, E, A](zio: Option[ZIO[R, E, A]])
    def opt: ZIO[R, E, Option[A]] =
      zio match {
        case Some(z) => z.map(Some(_))
        case None    => ZIO.succeed(None)
      }

  private def combineMatchesOptZio(firstTeamId: Long, 
                                      firstMatchIdOpt: Option[Long],
                                      secondTeamIdOpt: Option[Long], 
                                      secondMatchIdOpt: Option[Long]): ZIO[AuthConfig & ChppService, HattidError, Option[SingleMatch]] = {
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

  private def getTeamPlayedMatches(teamOpt: Option[Team]): ZIO[AuthConfig & ChppService, HattidError, Seq[NearestMatch]] = {
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

  private def currentTeamPlayedMatchesAndUpcomingOpponents(teamId: Long): ZIO[AuthConfig & ChppService, HattidError, (Seq[NearestMatch], Seq[(Long, String)])] = {
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

  private def teamPlayedMatches(teamId: Long): ZIO[AuthConfig & ChppService, HattidError, Seq[NearestMatch]] =
    ZIO.serviceWithZIO[ChppService](_.matches(teamId))
      .map(opponentMatches => {
        opponentMatches.team.matchList
          .filter(m => m.matchType == MatchType.LEAGUE_MATCH || m.matchType == MatchType.CUP_MATCH)
          .filter(_.status == "FINISHED")
          .sortBy(_.matchDate)
          .takeRight(3)
          .map(NearestMatch.chppMatchToNearestMatch)
      })
}
