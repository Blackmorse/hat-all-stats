package controllers

import javax.inject.Inject
import play.api.mvc.{BaseController, ControllerComponents}
import service.DefaultService
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future

class ErrorController @Inject() (val defaultService: DefaultService,
                                  val controllerComponents: ControllerComponents)
  extends BaseController with MessageSupport {

  def errorForLeague(leagueId: Int) = Action.async { implicit request =>

    val leagueInfo = defaultService.leagueInfo.leagueInfo.getOrElse(leagueId, defaultService.leagueInfo(1000))

    val details = WebLeagueDetails(leagueInfo = leagueInfo,
      divisionLevelsLinks = defaultService.divisionLevelLinks(leagueInfo.leagueId))

    Future(Ok(views.html.errors.errorForLeague(details)(messages)))
  }
}
