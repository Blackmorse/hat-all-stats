package controllers

import javax.inject.{Inject, Singleton}
import play.api.mvc.ControllerComponents
import play.api.i18n.I18nSupport
import play.api.mvc.BaseController

import scala.concurrent.ExecutionContext.Implicits.global
import service.{DefaultService, LeagueInfoService, OverviewStatsService}

@Singleton
class WorldController @Inject() (val controllerComponents: ControllerComponents,
            val overviewStatsService: OverviewStatsService,
             val leagueInfoService: LeagueInfoService)
        extends BaseController with I18nSupport with MessageSupport {
            
  def overview() = Action.async {implicit request =>
    val pageSize = request.cookies.get("hattid_page_size").map(_.value.toInt).getOrElse(DefaultService.PAGE_SIZE)
    overviewStatsService.overviewStatistics().map(overviewStatistics => {
      Ok(views.html.world.worldOverview(overviewStatistics,
        leagueInfoService.leagueInfo(1000).league,
        Some(leagueInfoService.leagueInfo),
        pageSize)(messages))
    })
  }
}
