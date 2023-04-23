package controllers

import chpp.matchdetails.MatchDetailsRequest
import chpp.matchdetails.models.MatchDetails
import databases.dao.RestClickhouseDAO
import databases.requests.matchdetails.SimilarMatchesRequest
import models.clickhouse.TeamMatchInfo
import models.web.matches.SingleMatch
import play.api.libs.json.{JsSuccess, JsValue, Json}
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents}
import service.SimilarMatchesService
import webclients.ChppClient

import java.util.Date
import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import databases.requests.matchdetails.AnnoySimilarMatchesRequest
import play.api.mvc.Request

case class WebTeamMatch(teamMatchInfo: TeamMatchInfo, date: Date,
                        homeTeamId: Long, homeTeamName: String, homeTeamGoals: Int,
                        awayTeamId: Long, awayTeamName: String, awayTeamGoals: Int)

@Singleton
class MatchController @Inject()(val controllerComponents: ControllerComponents,
                                val similarMatchesService: SimilarMatchesService,
                                val chppClient: ChppClient,
                                implicit val restClickhouseDAO: RestClickhouseDAO) extends BaseController {

  def similarMatches(matchId: Long, accuracy: Double): Action[AnyContent] = Action.async { implicit request =>
    similarMatchesService.similarMatchesStats(matchId, accuracy)
      .map(similarMatchesStats =>
        similarMatchesStats.map(s => Ok(Json.toJson(s))).getOrElse(Ok("")))
  }

  def similarMatchesWithAnnoy(matchId: Long, 
              accuracy: Int,
              considerTacticType: Boolean,
              considerTacticSkill: Boolean,
              considerSetPiecesLevels: Boolean): Action[AnyContent] =  Action(parse.json).async {
    similarMatchesService.similarMatchesAnnoyStats(matchId, accuracy, considerTacticType, considerTacticSkill, considerSetPiecesLevels)
      .map {
        case Left(notFoundError) => NotFound(Json.toJson(notFoundError))
        case Right(similarMatchesStats) => Ok(Json.toJson(similarMatchesStats))
      }
  }

  def similarMatchesByRatingsWithAnnoy(accuracy: Int,
              considerTacticType: Boolean,
              considerTacticSkill: Boolean,
              considerSetPiecesLevels: Boolean): Action[JsValue] =  Action(parse.json).async { implicit request: Request[JsValue] => 
    (request.body.validate[SingleMatch] match {
      case JsSuccess(singleMatch, _) => Right(singleMatch)
      case _ => Left(BadRequest("Match stats are corrupted"))
    }).map(singleMatch => AnnoySimilarMatchesRequest.execute(singleMatch, accuracy, considerTacticType, considerTacticSkill, considerSetPiecesLevels))
      .map(statsFuture => statsFuture.map(stats => Ok(Json.toJson(stats))))
      .getOrElse(Future(BadRequest("Error")))
  }

  def similarMatchesByRatings(accuracy: Double): Action[JsValue] = Action(parse.json).async { implicit request =>
    (request.body.validate[SingleMatch] match {
      case JsSuccess(singleMatch, _) => Right(singleMatch)
      case _ => Left(())
    }).map(singleMatch => SimilarMatchesRequest.execute(singleMatch, accuracy))
      .map(statsFuture => statsFuture.map(stats => Ok(Json.toJson(stats))))
      .getOrElse(Future(BadRequest("")))
  }

  def singleMatch(matchId: Long): Action[AnyContent] = Action(parse.json).async {
    chppClient.executeUnsafe[MatchDetails, MatchDetailsRequest](MatchDetailsRequest(matchId = Some(matchId)))
      .map(matchDetails => {
        val matc = matchDetails.matc
        val homeTeam = matc.homeTeam
        val awayTeam = matc.awayTeam
        val singleMatch = SingleMatch.fromHomeAwayTeams(
          homeTeam = homeTeam,
          awayTeam = awayTeam,
          homeGoals = Some(homeTeam.goals),
          awayGoals = Some(awayTeam.goals),
          matchId = Some(matc.matchId))
        Ok(Json.toJson(singleMatch))
      })
  }
}

