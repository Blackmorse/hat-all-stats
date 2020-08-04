package controllers

import databases.ClickhouseDAO
import javax.inject.{Inject, Singleton}
import play.api.cache.AsyncCacheApi
import play.api.mvc.{BaseController, ControllerComponents}
import service.LeagueInfoService

@Singleton
class LoaderController @Inject()(val controllerComponents: ControllerComponents,
                                 val clickhouseDAO: ClickhouseDAO,
                                 val leagueInfoService: LeagueInfoService,
                                 val cache: AsyncCacheApi) extends BaseController {

  def leagueRound(season: Int, leagueId: Int, round: Int) = Action {
    val roundInfos = clickhouseDAO
      .historyInfo(leagueId = Some(leagueId), season = Some(season), round = Some(round))

    leagueInfoService.leagueInfo.add(roundInfos)

    //Honduras is last league
    if(leagueId == 99) {
      cache.remove("overview.world")
    }
    Ok("")
  }
}
