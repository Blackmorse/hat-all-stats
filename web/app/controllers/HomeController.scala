package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import service.DefaultService

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(val controllerComponents: ControllerComponents,
                               val leagueController: LeagueController,
                               val configuration: Configuration,
                               val defaultService: DefaultService) extends BaseController {


  def index() = Action { implicit request: Request[AnyContent] =>
    Redirect(routes.LeagueController.bestTeams(defaultService.defaultLeagueId, None))
  }

  def lang(lang: String) = Action { implicit request: Request[AnyContent] =>
    Redirect(request.headers("Referer"), 302).withSession(request.session - "lang" + ("lang" -> lang))
  }
}
