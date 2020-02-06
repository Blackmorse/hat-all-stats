package controllers

import databases.ClickhouseDAO
import databases.clickhouse.{Accumulated, AvgMax, OnlyRound, StatisticsCHRequest}
import hattrick.Hattrick
import javax.inject.{Inject, Singleton}
import models.web._
import play.api.mvc.{BaseController, Call, ControllerComponents}
import service.{DefaultService, LeagueUnitCalculatorService, LeagueUnitTeamStat}
import utils.LeagueNameParser

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class WebLeagueUnitDetails(leagueName: String, leagueId: Int, divisionLevel: Int,
                                leagueUnitNumber: Int, leagueUnitName: String, leagueUnitId: Long,
                                teamLinks: Seq[(String, String)])
    extends AbstractWebDetails

@Singleton
class LeagueUnitController @Inject()(val controllerComponents: ControllerComponents,
                                     val hattrick: Hattrick,
                                     val defaultService: DefaultService,
                                     implicit val clickhouseDAO: ClickhouseDAO,
                                     val leagueUnitCalculatorService: LeagueUnitCalculatorService,
                                     val viewDataFactory: ViewDataFactory) extends BaseController {

  def bestTeams(leagueUnitId: Long, statisticsParametersOpt: Option[StatisticsParameters]) = Action.async{
    val statisticsParameters = statisticsParametersOpt.getOrElse(StatisticsParameters(defaultService.currentSeason, 0, Avg, "hatstats"))
    val leagueDetailsFuture = Future(hattrick.api.leagueDetails().leagueLevelUnitId(leagueUnitId).execute())

    leagueDetailsFuture.flatMap ( leagueDetails => {
      val leagueFixture = hattrick.api.leagueFixtures().season(defaultService.seasonForLeagueId(statisticsParameters.season, leagueDetails.getLeagueId))
        .leagueLevelUnitId(leagueUnitId).execute()

      val leagueName = defaultService.leagueIdToCountryNameMap(leagueDetails.getLeagueId).getEnglishName
      val leagueUnitNumber = LeagueNameParser.getLeagueUnitNumberByName(leagueDetails.getLeagueLevelUnitName)

      val tillRound = statisticsParameters.statsType match {
        case Round(round) => Some(round)
        case _ => None
      }

      val func: StatisticsParameters => Call = sp => routes.LeagueUnitController.bestTeams(leagueUnitId, Some(sp))
      val leagueUnitTeamStats = leagueUnitCalculatorService.calculate(leagueFixture, tillRound)

      val details = WebLeagueUnitDetails(leagueName = leagueName,
        leagueId = leagueDetails.getLeagueId,
        divisionLevel = leagueDetails.getLeagueLevel,
        leagueUnitNumber = leagueUnitNumber,
        leagueUnitName = leagueDetails.getLeagueLevelUnitName,
        leagueUnitId = leagueDetails.getLeagueLevelUnitId,
        teamLinks = teamLinks(leagueUnitTeamStats))

      StatisticsCHRequest.bestHatstatsTeamRequest.execute(leagueId = Some(leagueDetails.getLeagueId),
        divisionLevel = Some(leagueDetails.getLeagueLevel),
        leagueUnitId = Some(leagueDetails.getLeagueLevelUnitId),
        statisticsParameters = statisticsParameters)
        .map(bestTeams => {
            val viewData = viewDataFactory.create(details = details,
              func = func,
              statisticsType = AvgMax,
              statisticsParameters = statisticsParameters,
              statisticsCHRequest = StatisticsCHRequest.bestHatstatsTeamRequest,
              entities = bestTeams)

            Ok(views.html.leagueunit.bestTeams(viewData, leagueUnitTeamStats))
          })
    } )
  }

  def playerStats(leagueUnitId: Long, statisticsParametersOpt: Option[StatisticsParameters]) = Action.async {implicit  request =>
    val statisticsParameters = statisticsParametersOpt.getOrElse(StatisticsParameters(defaultService.currentSeason, 0, Accumulate, "scored"))

    val leagueDetailsFuture = Future(hattrick.api.leagueDetails().leagueLevelUnitId(leagueUnitId).execute())

    val func: StatisticsParameters => Call = sp => routes.LeagueUnitController.playerStats(leagueUnitId, Some(sp))

    leagueDetailsFuture.flatMap ( leagueDetails => {
      val leagueFixture = hattrick.api.leagueFixtures().season(defaultService.seasonForLeagueId(statisticsParameters.season, leagueDetails.getLeagueId))
        .leagueLevelUnitId(leagueUnitId).execute()

      val leagueName = defaultService.leagueIdToCountryNameMap(leagueDetails.getLeagueId).getEnglishName
      val leagueUnitNumber = LeagueNameParser.getLeagueUnitNumberByName(leagueDetails.getLeagueLevelUnitName)

      val tillRound = statisticsParameters.statsType match {
        case Round(round) => Some(round)
        case _ => None
      }

      val leagueUnitTeamStats = leagueUnitCalculatorService.calculate(leagueFixture, tillRound)

      val details = WebLeagueUnitDetails(leagueName = leagueName,
        leagueId = leagueDetails.getLeagueId,
        divisionLevel = leagueDetails.getLeagueLevel,
        leagueUnitNumber = leagueUnitNumber,
        leagueUnitName = leagueDetails.getLeagueLevelUnitName,
        leagueUnitId = leagueDetails.getLeagueLevelUnitId,
        teamLinks = teamLinks(leagueUnitTeamStats))


      StatisticsCHRequest.playerStatsRequest.execute(leagueId = Some(leagueDetails.getLeagueId),
        divisionLevel = Some(leagueDetails.getLeagueLevel),
        leagueUnitId = Some(leagueDetails.getLeagueLevelUnitId),
        statisticsParameters = statisticsParameters)
        .map(playerStats => {
          val viewData = viewDataFactory.create(details = details,
            func = func,
            statisticsType = Accumulated,
            statisticsParameters = statisticsParameters,
            statisticsCHRequest =  StatisticsCHRequest.playerStatsRequest,
            entities = playerStats)

          Ok(views.html.leagueunit.playerStats(viewData))
        })
    })
  }

  def teamState(leagueUnitId: Long, statisticsParametersOpt: Option[StatisticsParameters]) = Action.async{ implicit request =>
    val leagueDetailsFuture = Future(hattrick.api.leagueDetails().leagueLevelUnitId(leagueUnitId).execute())

    leagueDetailsFuture.flatMap ( leagueDetails => {
      val currentRound = defaultService.currentRound(leagueDetails.getLeagueId)

      val statisticsParameters = statisticsParametersOpt.getOrElse(StatisticsParameters(defaultService.currentSeason, 0, Round(currentRound), "rating"))

      val func: StatisticsParameters => Call = sp => routes.LeagueUnitController.teamState(leagueUnitId, Some(sp))

      val leagueFixture = hattrick.api.leagueFixtures().season(defaultService.seasonForLeagueId(statisticsParameters.season, leagueDetails.getLeagueId))
        .leagueLevelUnitId(leagueUnitId).execute()

      val leagueName = defaultService.leagueIdToCountryNameMap(leagueDetails.getLeagueId).getEnglishName
      val leagueUnitNumber = LeagueNameParser.getLeagueUnitNumberByName(leagueDetails.getLeagueLevelUnitName)

      val tillRound = statisticsParameters.statsType match {
        case Round(round) => Some(round)
        case _ => None
      }

      val leagueUnitTeamStats = leagueUnitCalculatorService.calculate(leagueFixture, tillRound)

      StatisticsCHRequest.teamStateRequest.execute(leagueId = Some(leagueDetails.getLeagueId),
        divisionLevel = Some(leagueDetails.getLeagueLevel),
        leagueUnitId = Some(leagueUnitId),
        statisticsParameters = statisticsParameters)
          .map(teamState => {
            val details = WebLeagueUnitDetails(
              leagueName = leagueName,
              leagueId = leagueDetails.getLeagueId,
              divisionLevel = leagueDetails.getLeagueLevel,
              leagueUnitNumber = leagueUnitNumber,
              leagueUnitName =  leagueDetails.getLeagueLevelUnitName,
              leagueUnitId = leagueUnitId,
              teamLinks = teamLinks(leagueUnitTeamStats))

            val viewData = viewDataFactory.create(details = details,
              func = func,
              statisticsType = OnlyRound,
              statisticsParameters = statisticsParameters,
              statisticsCHRequest = StatisticsCHRequest.teamStateRequest,
              entities = teamState)

            Ok(views.html.leagueunit.teamState(viewData))
          })
    })
  }

  private def teamLinks(leagueTeamStats: Seq[LeagueUnitTeamStat]): Seq[(String, String)] = {
    leagueTeamStats.map(stat => stat.teamName -> routes.TeamController.matches(stat.teamId).url)
  }
}
