package controllers

import databases.ClickhouseDAO
import javax.inject.{Inject, Singleton}
import models.web.{AbstractWebDetails, SeasonInfo, WebPagedEntities}
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.{BaseController, ControllerComponents}
import service.DefaultService
import utils.Romans

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._

case class DivisionLevelForm(divisionLevel: Int)

case class WebLeagueDetails(leagueName: String, leagueId: Int, form: Form[DivisionLevelForm],
                            divisionLevelsLinks: Seq[(String, String)], seasonInfo: SeasonInfo) extends AbstractWebDetails

@Singleton
class LeagueController @Inject() (val controllerComponents: ControllerComponents,
                                  val clickhouseDAO: ClickhouseDAO,
                                  val defaultService: DefaultService) extends BaseController with play.api.i18n.I18nSupport {

  val form: Form[DivisionLevelForm] = Form(mapping(
    "division_level" -> number
    )(DivisionLevelForm.apply)(DivisionLevelForm.unapply))


  def bestTeams(leagueId: Int, season: Int, page: Int) = Action.async { implicit request =>
    val leagueName = defaultService.leagueIdToCountryNameMap(leagueId).getEnglishName

    val seasonFunction: Int => String = s => routes.LeagueController.bestTeams(leagueId ,s ,0).url
    val seasonInfo = SeasonInfo(season, defaultService.seasonsWithLinks(leagueId, seasonFunction))

    val details = WebLeagueDetails(leagueName, leagueId, form, divisionLevels(leagueId, season), seasonInfo)

    val pageUrlFunc: Int => String = p => routes.LeagueController.bestTeams(leagueId, season, p).url

    clickhouseDAO.bestTeams(leagueId = Some(leagueId), season = Some(season), page = page)
      .map(bestTeams => Ok(views.html.league.bestTeams(details, WebPagedEntities(bestTeams, page, pageUrlFunc))))
  }

  def bestLeagueUnits(leagueId: Int, season: Int, page: Int) = Action.async {implicit request =>
    val leagueName = defaultService.leagueIdToCountryNameMap(leagueId).getEnglishName

    val seasonFunction: Int => String = s => routes.LeagueController.bestLeagueUnits(leagueId ,s ,0).url
    val seasonInfo = SeasonInfo(season, defaultService.seasonsWithLinks(leagueId, seasonFunction))

    val details = WebLeagueDetails(leagueName, leagueId, form, divisionLevels(leagueId, season), seasonInfo)

    val pageUrlFunc: Int => String = p => routes.LeagueController.bestLeagueUnits(leagueId, season, p).url

    clickhouseDAO.bestLeagueUnits(leagueId = Some(leagueId), season = Some(season), page = page)
      .map(bestLeagueUnits => Ok(views.html.league.bestLeagueUnits(details, WebPagedEntities(bestLeagueUnits, page, pageUrlFunc))))
  }


  private def divisionLevels(leagueId: Int, season: Int): Seq[(String, String)] = {
    val maxLevels = defaultService.leagueIdToCountryNameMap(leagueId).getNumberOfLevels
    (1 to maxLevels)
      .map(i => Romans(i) -> routes.DivisionLevelController.bestTeams(leagueId, season, i).url )
  }
}


