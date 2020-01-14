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


  def leagueUnit(leagueUnitId: Long) = Action { implicit  request: Request[AnyContent] =>
    val leagueDetails = hattrick.api.leagueDetails().leagueLevelUnitId(leagueUnitId).execute()


//    clickhouse.query(" select team_id, team_name, toInt32(avg(rating_midfield * 3 + rating_right_def + rating_left_def + rating_mid_def + rating_right_att + rating_mid_att + rating_left_att)) as hatstats from hattrick.match_details where division_level = 1 group by team_id, team_name order by hatstats desc limit 8")
//      .onComplete(println(_))

    clickhouseDAO.bestTeams.onComplete(println(_))

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
