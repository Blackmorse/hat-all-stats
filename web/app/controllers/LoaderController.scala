package controllers

import databases.dao.RestClickhouseDAO
import databases.requests.matchdetails.HistoryInfoRequest
import hattid.CommonData
import models.web.NotFoundError
import play.api.Logging
import play.api.cache.AsyncCacheApi
import play.api.libs.json.{JsSuccess, JsValue, Json, Reads}
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents}
import service.leagueinfo.{Finished, LeagueInfoService, Loading, Scheduled}
import zio.{ZIO, ZLayer}

import java.util.Date
import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext.Implicits.global

case class LeagueTime(leagueId: Int, time: Date)

object LeagueTime {
  implicit val reads: Reads[LeagueTime] = Json.reads[LeagueTime]
}

@Singleton
class LoaderController @Inject()(val controllerComponents: ControllerComponents,
                                 val restClickhouseDAO: RestClickhouseDAO,
                                 val leagueInfoService: LeagueInfoService,
                                 val cache: AsyncCacheApi)
    extends RestController with Logging {

  def leagueRound(season: Int, leagueId: Int, round: Int): Action[AnyContent] = asyncZio {
    HistoryInfoRequest.execute(leagueId = Some(leagueId), season = Some(season), round = Some(round))
      .flatMap(roundInfos => {
        if (roundInfos.isEmpty) {
          NotFound(s"Not found history for league $leagueId, season $season, round $round")
          ZIO.fail(NotFoundError("", "", ""))
        } else {
          leagueInfoService.leagueInfo.add(roundInfos)
          leagueInfoService.leagueInfo(leagueId).loadingInfo = Finished

          //Salvador is the last league
          if (leagueId == CommonData.LAST_SERIES_LEAGUE_ID) {
            cache.remove("overview.world")
            leagueInfoService.leagueInfo.leagueInfo.values.foreach(leagueInfo => leagueInfo.loadingInfo = Finished)
          }
          cache.remove(s"overview.$leagueId")
          ZIO.succeed("")
        }
      }).provide(ZLayer.succeed(restClickhouseDAO))
  }

  def scheduleInfo(): Action[JsValue] = Action(parse.json) { request =>

    request.body.validate[Seq[LeagueTime]] match {
      case JsSuccess(schedules, _) =>
        schedules.foreach(leagueTime => {
          logger.info(s"Scheduled time for ${leagueTime.leagueId}: ${leagueTime.time} (${leagueTime.time.getTime})")
          leagueInfoService.leagueInfo(leagueTime.leagueId).loadingInfo = Scheduled(leagueTime.time)
        })
        Ok("")
      case _ =>
        BadRequest("")
    }
  }

  def loadingStarted(leagueId: Int): Action[AnyContent] = Action {
    leagueInfoService.leagueInfo(leagueId).loadingInfo = Loading
    Ok("")
  }
}
