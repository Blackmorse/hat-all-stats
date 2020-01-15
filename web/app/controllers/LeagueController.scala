package controllers

import databases.ClickhouseDAO
import hattrick.Hattrick
import javax.inject.{Inject, Singleton}
import play.api.mvc.{BaseController, ControllerComponents}

import scala.concurrent._
import play.api._
import play.api.mvc._

import ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class LeagueController @Inject() (val controllerComponents: ControllerComponents,
                                  val clickhouseDAO: ClickhouseDAO,
                                  val hattrick: Hattrick) extends BaseController {

  def bestTeams(leagueId: Int) = Action.async { implicit request =>
    val bestTeamsFuture = clickhouseDAO.bestTeamsForLeague(leagueId)

    val leagueNameFuture = Future(hattrick.api.worldDetails().leagueId(leagueId).execute().getLeagueList.get(0).getEnglishName)

    bestTeamsFuture.zipWith(leagueNameFuture){case(bestTeams, leagueName) =>
      Ok(views.html.league.bestTeams(leagueName, leagueId, bestTeams))}
  }

  def bestLeagueUnits(leagueId: Int) = Action.async {implicit request =>

    val leagueNameFuture = Future(hattrick.api.worldDetails().leagueId(leagueId).execute().getLeagueList.get(0).getEnglishName)

    val bestLeagueUnitsFuture = clickhouseDAO.bestLeagueUnitsForLeague(leagueId)

    bestLeagueUnitsFuture.zipWith(leagueNameFuture){case(bestLeagueUnits, leagueName) =>
      Ok(views.html.league.bestLeagueUnits(leagueName, leagueId, bestLeagueUnits))}
  }
}
