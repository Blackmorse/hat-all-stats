package controllers

import databases.ClickhouseDAO
import javax.inject.{Inject, Singleton}
import play.api.mvc.{BaseController, ControllerComponents}
import service.DefaultService

@Singleton
class LoaderController @Inject()(val controllerComponents: ControllerComponents,
                                 val clickhouseDAO: ClickhouseDAO,
                                val defaultService: DefaultService) extends BaseController {


  def leagueRound(season: Int, leagueId: Int, round: Int) = Action {
    val roundInfos = clickhouseDAO
      .historyInfo(leagueId = Some(leagueId), season = Some(season), round = Some(round))

    defaultService.leagueInfo.add(roundInfos)

    Ok("")
  }
}
