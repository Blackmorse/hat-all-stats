package controllers

import databases.ClickhouseDAO
import javax.inject.{Inject, Singleton}
import play.api.mvc.{BaseController, ControllerComponents}
import service.DefaultService
import utils.Romans

import scala.concurrent.ExecutionContext
import scala.concurrent.ExecutionContext.Implicits.global

case class WebDivisionLevelDetails(leagueName: String, leagueId: Int, season: Int, divisionLevel: Int, divisionLevelRoman: String,
                                   leagueUnitLinks: Seq[(String, String)])

@Singleton
class DivisionLevelController@Inject() (val controllerComponents: ControllerComponents,
                                        val clickhouseDAO: ClickhouseDAO,
                                        val defaultService: DefaultService) extends BaseController {
  def bestTeams(leagueId: Int, season: Int, divisionLevel: Int) = Action.async{ implicit request =>
    val leagueName = defaultService.leagueIdToCountryNameMap(leagueId).getEnglishName

    val details = WebDivisionLevelDetails(leagueName, leagueId, season, divisionLevel, Romans(divisionLevel),
      leagueUnitNumbers(leagueId, season, divisionLevel))

    clickhouseDAO.bestTeams(leagueId = Some(leagueId), season = Some(season), divisionLevel = Some(divisionLevel))
      .map(bestTeams => Ok(views.html.divisionlevel.bestTeams(details, bestTeams)))
  }

  def bestLeagueUnits(leagueId: Int, season: Int, divisionLevel: Int) = Action.async{  implicit request =>
    val leagueName = defaultService.leagueIdToCountryNameMap(leagueId).getEnglishName

    val details = WebDivisionLevelDetails(leagueName, leagueId, season, divisionLevel, Romans(divisionLevel),
      leagueUnitNumbers(leagueId, season, divisionLevel))

    clickhouseDAO.bestLeagueUnits(leagueId = Some(leagueId), season = Some(season), divisionLevel = Some(divisionLevel))
      .map(bestLeagueUnits => Ok(views.html.divisionlevel.bestLeagueUnits(details, bestLeagueUnits)))

  }

  def leagueUnitNumbers(leagueId: Int, season: Int, divisionLevel: Int): Seq[(String, String)] =
    defaultService.leagueNumbersMap(divisionLevel).map(number =>
      number.toString -> routes.LeagueUnitController.bestTeams(leagueId, season, divisionLevel, number).url)
}
