package controllers

import databases.ClickhouseDAO
import javax.inject.{Inject, Singleton}
import models.web.{AbstractWebDetails, SeasonInfo, WebPagedEntities}
import play.api.mvc.{BaseController, ControllerComponents}
import service.DefaultService
import utils.Romans

import scala.concurrent.ExecutionContext.Implicits.global

case class WebDivisionLevelDetails(leagueName: String, leagueId: Int, seasonInfo: SeasonInfo, divisionLevel: Int, divisionLevelRoman: String,
                                   leagueUnitLinks: Seq[(String, String)]) extends AbstractWebDetails

@Singleton
class DivisionLevelController@Inject() (val controllerComponents: ControllerComponents,
                                        val clickhouseDAO: ClickhouseDAO,
                                        val defaultService: DefaultService) extends BaseController {
  def bestTeams(leagueId: Int, season: Int, divisionLevel: Int, page: Int) = Action.async{ implicit request =>
    val leagueName = defaultService.leagueIdToCountryNameMap(leagueId).getEnglishName

    val seasonFunction: Int => String = s => routes.DivisionLevelController.bestTeams(leagueId, s, divisionLevel, 0).url
    val seasonInfo = SeasonInfo(season, defaultService.seasonsWithLinks(leagueId, seasonFunction))

    val details = WebDivisionLevelDetails(leagueName, leagueId, seasonInfo, divisionLevel, Romans(divisionLevel),
      leagueUnitNumbers(leagueId, season, divisionLevel))

    val pageUrlFunc: Int => String = p => routes.DivisionLevelController.bestTeams(leagueId, season, divisionLevel, p).url

    clickhouseDAO.bestTeams(leagueId = Some(leagueId), season = Some(season), divisionLevel = Some(divisionLevel), page = page)
      .map(bestTeams => Ok(views.html.divisionlevel.bestTeams(details, WebPagedEntities(bestTeams, page, pageUrlFunc))))
  }

  def bestLeagueUnits(leagueId: Int, season: Int, divisionLevel: Int, page: Int) = Action.async{  implicit request =>
    val leagueName = defaultService.leagueIdToCountryNameMap(leagueId).getEnglishName

    val seasonFunction: Int => String = s => routes.DivisionLevelController.bestLeagueUnits(leagueId, s, divisionLevel, 0).url
    val seasonInfo = SeasonInfo(season, defaultService.seasonsWithLinks(leagueId, seasonFunction))

    val details = WebDivisionLevelDetails(leagueName, leagueId, seasonInfo, divisionLevel, Romans(divisionLevel),
      leagueUnitNumbers(leagueId, season, divisionLevel))

    val pageUrlFunc: Int => String = p => routes.DivisionLevelController.bestLeagueUnits(leagueId, season, divisionLevel, p).url

    clickhouseDAO.bestLeagueUnits(leagueId = Some(leagueId), season = Some(season), divisionLevel = Some(divisionLevel), page = page)
      .map(bestLeagueUnits => Ok(views.html.divisionlevel.bestLeagueUnits(details, WebPagedEntities(bestLeagueUnits, page, pageUrlFunc))))

  }

  def leagueUnitNumbers(leagueId: Int, season: Int, divisionLevel: Int): Seq[(String, String)] =
    defaultService.leagueNumbersMap(divisionLevel).map(number =>
      number.toString -> routes.LeagueUnitController.bestTeams(leagueId, season, divisionLevel, number).url)
}
