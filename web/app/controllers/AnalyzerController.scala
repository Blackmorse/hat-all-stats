package controllers

import chpp.commonmodels.MatchType
import chpp.matchdetails.MatchDetailsRequest
import chpp.matchdetails.models.MatchDetails
import chpp.matches.MatchesRequest
import chpp.matches.models.Matches
import models.clickhouse.NearestMatch
import models.web.analyzer.MatchOpponentCombinedInfo
import models.web.matches.SingleMatch
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import webclients.ChppClient

import javax.inject.Inject
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class AnalyzerController @Inject()(val controllerComponents: ControllerComponents,
                                   val chppClient: ChppClient) extends RestController {
  type Team = (Long, String)

  def combineMatches(firstTeamId: Long, firstMatchId: Long,
                     secondTeamId: Long, secondMatchId: Long): Action[AnyContent] = Action.async { implicit request =>
    combineMatchesOptFuture(firstTeamId, Some(firstMatchId), Some(secondTeamId), Some(secondMatchId))
      .map(singleMatch => Ok(Json.toJson(singleMatch)))
  }

  def opponentTeamMatches(opponentTeamId: Long): Action[AnyContent] = Action.async { implicit request =>
    teamPlayedMatches(opponentTeamId)
      .map(matches => Ok(Json.toJson(matches)))
  }

  def currentTeamAndOpponentTeamMatches(teamId: Long): Action[AnyContent] = Action.async { implicit request =>
    (for {
      (currentTeamPlayedMatches, currentTeamNextOpponents) <- currentTeamPlayedMatchesAndUpcomingOpponents(teamId)
      opponentPlayedMatches <- getTeamPlayedMatches(currentTeamNextOpponents.headOption)
      combinedMatchOpt <- combineMatchesOptFuture(firstTeamId = teamId,
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
    }).map(r => Ok(Json.toJson(r)))
  }

  private def combineMatchesOptFuture(firstTeamId: Long, firstMatchIdOpt: Option[Long],
                                   secondTeamIdOpt: Option[Long], secondMatchIdOpt: Option[Long]): Future[Option[SingleMatch]] = {
    (for {
      firstMatchId  <- firstMatchIdOpt
      secondTeamId  <- secondTeamIdOpt
      secondMatchId <- secondMatchIdOpt
    } yield {
      val firstMatchFuture = chppClient.execute[MatchDetails, MatchDetailsRequest](MatchDetailsRequest(matchId = Some(firstMatchId)))
      val secondMatchFuture = chppClient.execute[MatchDetails, MatchDetailsRequest](MatchDetailsRequest(matchId = Some(secondMatchId)))

      firstMatchFuture.zip(secondMatchFuture).map { case (firstMatch, secondMatch) =>
        val firstTeam = if (firstMatch.matc.homeTeam.teamId == firstTeamId) firstMatch.matc.homeTeam else firstMatch.matc.awayTeam
        val secondTeam = if (secondMatch.matc.homeTeam.teamId == secondTeamId) secondMatch.matc.homeTeam else secondMatch.matc.awayTeam

        SingleMatch.fromHomeAwayTeams(
          homeTeam = firstTeam,
          awayTeam = secondTeam,
          homeGoals = None,
          awayGoals = None,
          matchId = None)
      }
    }) match {
      case Some(future) => future.map(Some(_))
      case None => Future(None)
    }
  }

  private def getTeamPlayedMatches(teamOpt: Option[Team]): Future[Seq[NearestMatch]] = {
    teamOpt.map(team => {
      chppClient.execute[Matches, MatchesRequest](MatchesRequest(teamId = Some(team._1)))
        .map(opponentMatches => {
          opponentMatches.team.matchList
            .filter(m => m.matchType == MatchType.LEAGUE_MATCH || m.matchType == MatchType.CUP_MATCH)
            .filter(_.status == "FINISHED")
            .sortBy(_.matchDate)
            .takeRight(3)
            .map(NearestMatch.chppMatchToNearestMatch)
        })
    }).getOrElse(Future(Seq()))
  }

  private def currentTeamPlayedMatchesAndUpcomingOpponents(teamId: Long): Future[(Seq[NearestMatch], Seq[Team])] = {
    chppClient.execute[Matches, MatchesRequest](MatchesRequest(teamId = Some(teamId)))
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

  private def teamPlayedMatches(teamId: Long): Future[Seq[NearestMatch]] =
    chppClient.execute[Matches, MatchesRequest](MatchesRequest(teamId = Some(teamId)))
      .map(opponentMatches => {
        opponentMatches.team.matchList
          .filter(m => m.matchType == MatchType.LEAGUE_MATCH || m.matchType == MatchType.CUP_MATCH)
          .filter(_.status == "FINISHED")
          .sortBy(_.matchDate)
          .takeRight(3)
          .map(NearestMatch.chppMatchToNearestMatch)
      })
}
