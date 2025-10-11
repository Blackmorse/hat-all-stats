package controllers

import cache.ZioCacheModule.HattidEnv
import databases.dao.RestClickhouseDAO
import databases.requests.matchdetails.HistoryInfoRequest
import hattid.CommonData
import models.web.{BadRequestError, NotFoundError}
import play.api.Logging
import play.api.cache.AsyncCacheApi
import play.api.libs.json.{JsSuccess, JsValue, Json, Reads}
import play.api.mvc.{Action, AnyContent, ControllerComponents, Request}
import service.leagueinfo.*
import zio.prelude.Validation
import zio.{Unsafe, ZIO, ZLayer}

import java.util.Date
import javax.inject.{Inject, Singleton}

case class LeagueTime(leagueId: Int, time: Date)

object LeagueTime {
  implicit val reads: Reads[LeagueTime] = Json.reads[LeagueTime]
}

@Singleton
class LoaderController @Inject()(val controllerComponents: ControllerComponents,
                                 val hattidEnvironment: zio.ZEnvironment[HattidEnv])
    extends RestController(hattidEnvironment) with Logging {
  
  private val layer = LeagueInfoServiceZIO.layer

  def leagueRound(season: Int, leagueId: Int, round: Int): Action[AnyContent] = asyncZio {
    HistoryInfoRequest.execute(leagueId = Some(leagueId), season = Some(season), round = Some(round))
      .flatMap(roundInfos => {
        if (roundInfos.isEmpty) {
          NotFound(s"Not found history for league $leagueId, season $season, round $round")
          ZIO.fail(NotFoundError("", "", ""))
        } else {
          Unsafe.unsafe { implicit unsafe =>
            zio.Runtime.default.unsafe.runToFuture {
              (for {
                // TODO - update overview cache in case it's a reload of some round?
                leagueInfoServiceZIO <- ZIO.service[LeagueInfoServiceZIO]
                _                    <- leagueInfoServiceZIO.addAnotherRound(leagueId, season, round, roundInfos)
                _                    <- leagueInfoServiceZIO.setLoadingStatus(leagueId, Finished)
                                      //Salvador is the last league
                _                    <- if (leagueId == CommonData.LAST_SERIES_LEAGUE_ID) leagueInfoServiceZIO.finishAll() 
                                        else ZIO.unit
              } yield ())
                .mapError(error => new Exception(error.toString))
                .provideEnvironment(hattidEnvironment)
            } 
          }
          
          ZIO.succeed("")
        }
      })
  }
  
  def scheduleInfo(): Action[JsValue] = asyncZioPost {
    (for {
      leagueInfoService <- ZIO.service[LeagueInfoServiceZIO]
      request           <- ZIO.service[Request[JsValue]]
      schedules         <- validateRequest(request).toZIO
      _                 <- ZIO.collectAll(
                          schedules.map(leagueTime => leagueInfoService.setLoadingStatus(leagueTime.leagueId, Scheduled(leagueTime.time)))
                        )
    } yield "")
      .provideSomeEnvironment[Request[JsValue]](zenv => zenv ++ hattidEnvironment)
  }
  
  private def validateRequest(request: Request[JsValue]): Validation[BadRequestError, Seq[LeagueTime]] = {
    request.body.validate[Seq[LeagueTime]] match {
      case JsSuccess(schedules, _) => Validation.succeed(schedules)
      case _ => Validation.fail(BadRequestError("Corrupted Schedule Info"))
    }
  }
  
  def loadingStarted(leagueId: Int): Action[AnyContent] = asyncZio {
    ZIO.serviceWithZIO[LeagueInfoServiceZIO](leagueInfoService => leagueInfoService.setLoadingStatus(leagueId, Loading))
      .map(_ => "")
  }
}
