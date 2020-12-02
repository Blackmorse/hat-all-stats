package controllers

import javax.inject.Inject
import play.api.mvc.{BaseController, ControllerComponents}
import service.leagueinfo.LeagueInfoService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ErrorController @Inject() (val leagueInfoService: LeagueInfoService,
                                  val controllerComponents: ControllerComponents)
  extends BaseController with MessageSupport {

  def errorForLeague(leagueId: Int) = Action.async { implicit request =>

    val leagueInfo = leagueInfoService.leagueInfo.leagueInfo.getOrElse(leagueId, leagueInfoService.leagueInfo(1000))

    val details = WebLeagueDetails(leagueInfo = leagueInfo,
      currentRound = leagueInfoService.leagueInfo.currentRound(leagueInfo.leagueId),
      divisionLevelsLinks = leagueInfoService.divisionLevelLinks(leagueInfo.leagueId))

    Future(Ok(views.html.errors.errorForLeague(details)(messages)))
  }
}
