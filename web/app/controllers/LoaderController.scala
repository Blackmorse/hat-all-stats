package controllers

import java.util.Date

import databases.ClickhouseDAO
import javax.inject.{Inject, Singleton}
import play.api.Logging
import play.api.cache.AsyncCacheApi
import play.api.libs.json.{JsSuccess, Json}
import play.api.mvc.{BaseController, ControllerComponents}
import service.leagueinfo.{Finished, LeagueInfoService, Loading, Scheduled}

case class LeagueTime(leagueId: Int, time: Date)

object LeagueTime {
  implicit val reads = Json.reads[LeagueTime]
}

@Singleton
class LoaderController @Inject()(val controllerComponents: ControllerComponents,
                                 val clickhouseDAO: ClickhouseDAO,
                                 val leagueInfoService: LeagueInfoService,
                                 val cache: AsyncCacheApi)
    extends BaseController with Logging {

  def leagueRound(season: Int, leagueId: Int, round: Int) = Action {
    val roundInfos = clickhouseDAO
      .historyInfo(leagueId = Some(leagueId), season = Some(season), round = Some(round))

    leagueInfoService.leagueInfo.add(roundInfos)
    leagueInfoService.leagueInfo(leagueId).loadingInfo = Finished

    //Salvador is the last league
    if(leagueId == 100) {
      cache.remove("overview.world")
      leagueInfoService.leagueInfo.leagueInfo.values.foreach(leagueInfo => leagueInfo.loadingInfo = Finished)
    }
    cache.remove(s"overview.$leagueId")
    Ok("")
  }

  def scheduleInfo() = Action(parse.json) { request =>

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

  def loadingStarted(leagueId: Int) = Action {
    leagueInfoService.leagueInfo(leagueId).loadingInfo = Loading
    Ok("")
  }
}
