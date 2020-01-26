package controllers

import databases.ClickhouseDAO
import hattrick.Hattrick
import javax.inject.{Inject, Singleton}
import models.web.{AbstractWebDetails, SeasonInfo, WebPagedEntities}
import play.api.mvc.{BaseController, ControllerComponents}
import service.{DefaultService, LeagueUnitCalculatorService, LeagueUnitTeamStat}
import utils.LeagueNameParser

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class WebLeagueUnitDetails(leagueName: String, leagueId: Int, seasonInfo: SeasonInfo, divisionLevel: Int,
                                leagueUnitNumber: Int, leagueUnitName: String, leagueUnitId: Long, teamLinks: Seq[(String, String)])
    extends AbstractWebDetails

@Singleton
class LeagueUnitController @Inject()(val controllerComponents: ControllerComponents,
                                     val hattrick: Hattrick,
                                     val defaultService: DefaultService,
                                     val clickhouseDAO: ClickhouseDAO,
                                     val leagueUnitCalculatorService: LeagueUnitCalculatorService) extends BaseController {

  def bestTeams(leagueUnitId: Long, season: Int, page: Int) = Action.async{
    val leagueDetailsFuture = Future(hattrick.api.leagueDetails().leagueLevelUnitId(leagueUnitId).execute())

    leagueDetailsFuture.flatMap ( leagueDetails => {
      val leagueFixture = hattrick.api.leagueFixtures().season(defaultService.seasonForLeagueId(season, leagueDetails.getLeagueId))
        .leagueLevelUnitId(leagueUnitId).execute()

      val leagueName = defaultService.leagueIdToCountryNameMap(leagueDetails.getLeagueId).getEnglishName
      val leagueUnitNumber = LeagueNameParser.getLeagueUnitNumberByName(leagueDetails.getLeagueLevelUnitName)

      val seasonInfoFunc: Int => String = s => routes.LeagueUnitController.bestTeams(leagueUnitId, s, 0).url
      val seasonInfo = SeasonInfo(season, defaultService.seasonsWithLinks(leagueDetails.getLeagueId, seasonInfoFunc))

      val leagueUnitTeamStats = leagueUnitCalculatorService.calculate(leagueFixture)

      val details = WebLeagueUnitDetails(leagueName, leagueDetails.getLeagueId,
        seasonInfo, leagueDetails.getLeagueLevel, leagueUnitNumber,
        leagueDetails.getLeagueLevelUnitName, leagueDetails.getLeagueLevelUnitId, teamLinks(leagueUnitTeamStats))

      val pageUrlFunc: Int => String = p => routes.LeagueUnitController.bestTeams(leagueDetails.getLeagueLevelUnitId, p).url

      clickhouseDAO.bestTeams(leagueId = Some(leagueDetails.getLeagueId),
        season = Some(season), divisionLevel = Some(leagueDetails.getLeagueLevel),
        leagueUnitId = Some(leagueDetails.getLeagueLevelUnitId), page = page)
        .map(bestTeams => Ok(views.html.leagueunit.bestTeams(details, leagueUnitTeamStats, WebPagedEntities(bestTeams, page, pageUrlFunc))))
    } )
  }

  private def teamLinks(leagueTeamStats: Seq[LeagueUnitTeamStat]): Seq[(String, String)] = {
    leagueTeamStats.map(stat => stat.teamName -> routes.TeamController.matches(stat.teamId).url)
  }
}
