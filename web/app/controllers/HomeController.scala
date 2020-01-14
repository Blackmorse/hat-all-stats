package controllers

import databases.ClickhouseDAO
import hattrick.Hattrick
import javax.inject._
import play.api._
import play.api.mvc._

import scala.concurrent._
import ExecutionContext.Implicits.global

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(val controllerComponents: ControllerComponents,
                               val hattrick: Hattrick,
                               val clickhouseDAO: ClickhouseDAO,
                               val configuration: Configuration) extends BaseController {


  def index() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.league.bestTeams("Russia", configuration.get[Int]("hattrick.defaultCountryId")))
  }
}
