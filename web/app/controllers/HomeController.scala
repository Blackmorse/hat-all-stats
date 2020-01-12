package controllers

import hattrick.Hattrick
import javax.inject._
import play.api._
import play.api.mvc._

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(val controllerComponents: ControllerComponents,
                               hattrick: Hattrick,
                               configuration: Configuration) extends BaseController {


  def leagueUnit(leagueUnitId: Long) = Action { implicit  request: Request[AnyContent] =>
    val leagueDetails = hattrick.api.leagueDetails().leagueLevelUnitId(leagueUnitId).execute()
    Ok(views.html.league(leagueDetails))
  }

  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index() = Action { implicit request: Request[AnyContent] =>
    val leagueId = configuration.get[Int]("hattrick.defaultCountryId")
    val twoOneId = hattrick.api.search()
      .searchType(3)
      .searchLeagueId(leagueId)
      .searchString("II.1")
      .execute()
        .getSearchResults.get(0)
        .getResultId
    Redirect(routes.HomeController.leagueUnit(twoOneId - 1))
  }
}
