package controllers

import com.blackmorse.hattrick.api.leaguedetails.model.LeagueDetails
import databases.ClickhouseDAO
import hattrick.Hattrick
import javax.inject.{Inject, Singleton}
import play.api.mvc.{BaseController, ControllerComponents}
import service.DefaultService
import utils.{LeagueNameParser, Romans}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import collection.JavaConverters._

case class WebLeagueUnitDetails(leagueName: String, leagueId: Int, season: Int, divisionLevel: Int,
                                leagueUnitNumber: Int, leagueUnitName: String, leagueUnitId: Long, teamLinks: Seq[(String, String)])

@Singleton
class LeagueUnitController @Inject()(val controllerComponents: ControllerComponents,
                                     val hattrick: Hattrick,
                                     val defaultService: DefaultService,
                                     val clickhouseDAO: ClickhouseDAO) extends BaseController {

  def bestTeams(leagueId: Int, season: Int, divisionLevel: Int, leagueUnitNumber: Int) = Action.async{implicit  request =>

    val leagueUnitId = hattrick.api.search()
      .searchLeagueId(leagueId)
      .searchType(3)
      .searchString(Romans(divisionLevel) + "." + leagueUnitNumber)
        .execute().getSearchResults.get(0).getResultId

    val leagueName = defaultService.leagueIdToCountryNameMap(leagueId).getEnglishName

    val leagueDetailsFuture = Future(hattrick.api.leagueDetails().leagueLevelUnitId(leagueUnitId).execute())

    val bestTeamsFuture = clickhouseDAO.bestTeams(leagueId = Some(leagueId), season = Some(season), divisionLevel = Some(divisionLevel),
      leagueUnitId = Some(leagueUnitId))

    leagueDetailsFuture.zipWith(bestTeamsFuture){case(leagueDetails, bestTeams) =>

      val details = WebLeagueUnitDetails(leagueName, leagueId, season, divisionLevel, leagueUnitNumber,
        Romans(divisionLevel) + "." + leagueUnitNumber, leagueUnitId, teamLinks(leagueDetails))

      Ok(views.html.leagueunit.bestTeams(details, leagueDetails, bestTeams))}
  }

  def bestTeamsById(leagueUnitId: Long) = Action.async{
    val leagueDetails = hattrick.api.leagueDetails().leagueLevelUnitId(leagueUnitId).execute()

    val leagueName = defaultService.leagueIdToCountryNameMap(leagueDetails.getLeagueId).getEnglishName

    val leagueUnitNumber = LeagueNameParser.getLeagueUnitNumberByName(leagueDetails.getLeagueLevelUnitName)

    val details = WebLeagueUnitDetails(leagueName, leagueDetails.getLeagueId,
      defaultService.currentSeason, leagueDetails.getLeagueLevel, leagueUnitNumber,
      leagueDetails.getLeagueLevelUnitName, leagueDetails.getLeagueLevelUnitId, teamLinks(leagueDetails))

    clickhouseDAO.bestTeams(leagueId = Some(leagueDetails.getLeagueId),
      season = Some(defaultService.currentSeason), divisionLevel = Some(leagueDetails.getLeagueLevel),
      leagueUnitId = Some(leagueDetails.getLeagueLevelUnitId))
      .map(bestTeams => Ok(views.html.leagueunit.bestTeams(details, leagueDetails, bestTeams)))
  }

  private def teamLinks(leagueDetails: LeagueDetails): Seq[(String, String)] = {
    leagueDetails.getTeams.asScala.map(team => team.getTeamName -> routes.TeamController.matches(team.getTeamId).url)
  }
}
