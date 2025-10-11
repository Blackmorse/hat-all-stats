package controllers

import cache.ZioCacheModule.HattidEnv
import chpp.matchdetails.MatchDetailsRequest
import chpp.matchdetails.models.MatchDetails
import databases.dao.RestClickhouseDAO
import databases.requests.matchdetails.SimilarMatchesRequest
import models.clickhouse.TeamMatchInfo
import models.web.matches.SingleMatch
import play.api.libs.json.{JsSuccess, JsValue, Json}
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import service.{ChppService, SimilarMatchesService}
import webclients.ChppClient

import java.util.Date
import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext.Implicits.global
import databases.requests.matchdetails.AnnoySimilarMatchesRequest
import databases.requests.model.`match`.SimilarMatchesStats
import models.web.{BadRequestError, HattidError}
import play.api.mvc.Request
import zio.{ZIO, ZLayer}
import zio.prelude.Validation

case class WebTeamMatch(teamMatchInfo: TeamMatchInfo, date: Date,
                        homeTeamId: Long, homeTeamName: String, homeTeamGoals: Int,
                        awayTeamId: Long, awayTeamName: String, awayTeamGoals: Int)

@Singleton
class MatchController @Inject()(val controllerComponents: ControllerComponents,
                                val similarMatchesService: SimilarMatchesService,
                                val chppClient: ChppClient,
                                val chppService: ChppService,
                                val restClickhouseDAO: RestClickhouseDAO,
                                val hattidEnvironment: zio.ZEnvironment[HattidEnv]) extends RestController(hattidEnvironment) {

  def similarMatches(matchId: Long, accuracy: Double): Action[AnyContent] = asyncZio {
    similarMatchesService.similarMatchesStats(matchId, accuracy)
      .provide(ZLayer.succeed(restClickhouseDAO))
  }

  def similarMatchesWithAnnoy(matchId: Long, 
              accuracy: Int,
              considerTacticType: Boolean,
              considerTacticSkill: Boolean,
              considerSetPiecesLevels: Boolean): Action[AnyContent] =  asyncZio {
    similarMatchesService.similarMatchesAnnoyStats(matchId, accuracy, considerTacticType, considerTacticSkill, considerSetPiecesLevels)
      .provide(ZLayer.succeed(restClickhouseDAO))
  }

  private def validate(request: Request[JsValue]): Validation[BadRequestError, SingleMatch] = {
    request.body.validate[SingleMatch] match {
      case JsSuccess(singleMatch, _) => Validation.succeed(singleMatch)
      case _ => Validation.fail(BadRequestError("Match stats are corrupted"))
    }
  }

  def similarMatchesByRatingsWithAnnoy(accuracy: Int,
                                       considerTacticType: Boolean,
                                       considerTacticSkill: Boolean,
                                       considerSetPiecesLevels: Boolean) : Action[JsValue] = asyncZioPost {
    (for {
      request <- ZIO.service[Request[JsValue]]
      singleMatch <- validate(request).toZIO
      statsOpt <- AnnoySimilarMatchesRequest.execute(singleMatch, accuracy, considerTacticType, considerTacticSkill, considerSetPiecesLevels)
    } yield statsOpt)
      .provideSome[Request[JsValue]](ZLayer.succeed(restClickhouseDAO))
  }

  def similarMatchesByRatings(accuracy: Double): Action[JsValue] = asyncZioPost {
    (for {
      request <- ZIO.service[Request[JsValue]]
      singleMatch <- validate(request).toZIO
      statsOpt <- SimilarMatchesRequest.execute(singleMatch, accuracy)
    } yield statsOpt)
      .provideSome[Request[JsValue]](ZLayer.succeed(restClickhouseDAO))
  }

  def singleMatch(matchId: Long): Action[AnyContent] = asyncZio {
    chppService.matchDetails(matchId)
      .map(matchDetails => {
        val matc = matchDetails.matc
        val homeTeam = matc.homeTeam
        val awayTeam = matc.awayTeam
        SingleMatch.fromHomeAwayTeams(
          homeTeam = homeTeam,
          awayTeam = awayTeam,
          homeGoals = Some(homeTeam.goals),
          awayGoals = Some(awayTeam.goals),
          matchId = Some(matc.matchId))
      })
  }
}

