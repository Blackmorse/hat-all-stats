package controllers

import databases.ClickhouseDAO
import javax.inject.{Inject, Singleton}
import play.api.mvc.{BaseController, ControllerComponents}

import scala.concurrent._
import play.api._
import play.api.mvc._
import service.DefaultService

import ExecutionContext.Implicits.global

@Singleton
class LeagueController @Inject() (val controllerComponents: ControllerComponents,
                                  val clickhouseDAO: ClickhouseDAO,
                                  val defaultService: DefaultService) extends BaseController {

  def bestTeams(leagueId: Int, season: Int) = Action.async { implicit request =>
    val leagueName = defaultService.leagueIdToCountryNameMap(leagueId)

    clickhouseDAO.bestTeamsForLeague(leagueId, season)
      .map(bestTeams => Ok(views.html.league.bestTeams(leagueName, leagueId, season, bestTeams)))
  }

  def bestLeagueUnits(leagueId: Int, season: Int) = Action.async {implicit request =>
    val leagueName = defaultService.leagueIdToCountryNameMap(leagueId)

    clickhouseDAO.bestLeagueUnitsForLeague(leagueId, season)
      .map(bestLeagueUnits => Ok(views.html.league.bestLeagueUnits(leagueName, leagueId, season, bestLeagueUnits)))
  }
}
