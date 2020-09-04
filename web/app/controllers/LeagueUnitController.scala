package controllers

import com.blackmorse.hattrick.model.enums.SearchType
import databases.ClickhouseDAO
import databases.clickhouse.{Accumulated, AvgMax, OnlyRound, StatisticsCHRequest}
import hattrick.Hattrick
import javax.inject.{Inject, Singleton}
import models.web._
import play.api.mvc.{BaseController, Call, ControllerComponents}
import service.{DefaultService, LeagueInfoService, LeagueInfo, LeagueUnitCalculatorService,  LeagueUnitTeamStat}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class WebLeagueUnitDetails(leagueInfo: LeagueInfo, currentRound: Int, divisionLevel: Int,
                                leagueUnitName: String, leagueUnitId: Long,
                                teamLinks: Seq[(String, String)])
    extends AbstractWebDetails

@Singleton
class LeagueUnitController @Inject()(val controllerComponents: ControllerComponents,
                                     val hattrick: Hattrick,
                                     val leagueInfoService: LeagueInfoService,
                                     val defaultService: DefaultService,
                                     implicit val clickhouseDAO: ClickhouseDAO,
                                     val leagueUnitCalculatorService: LeagueUnitCalculatorService,
                                     val viewDataFactory: ViewDataFactory) extends BaseController with MessageSupport {

  def bestTeamsByName(leagueUnitName: String, leagueId: Int, statisticsParametersOpt: Option[StatisticsParameters]) = {
    val search = hattrick.api.search().searchType(SearchType.SERIES).searchLeagueId(leagueId).searchString(leagueUnitName).execute()
    val leagueUnitId = search.getSearchResults.get(0).getResultId

    bestTeams(leagueUnitId, statisticsParametersOpt)
  }

  def bestTeams(leagueUnitId: Long, statisticsParametersOpt: Option[StatisticsParameters]) = Action.async{ implicit request =>
    val leagueDetailsFuture = Future(hattrick.api.leagueDetails().leagueLevelUnitId(leagueUnitId).execute())

    leagueDetailsFuture.flatMap ( leagueDetails => {
      val (statisticsParameters, cookies) = defaultService.statisticsParameters(statisticsParametersOpt,
          leagueId = leagueDetails.getLeagueId(),
          statsType = Avg,
          sortColumn = "hatstats")

      val leagueFixture = hattrick.api.leagueFixtures().season(leagueInfoService.getAbsoluteSeasonFromRelative(statisticsParameters.season, leagueDetails.getLeagueId))
        .leagueLevelUnitId(leagueUnitId).execute()

      val tillRound = statisticsParameters.statsType match {
        case Round(round) => Some(round)
        case _ => None
      }

      val func: StatisticsParameters => Call = sp => routes.LeagueUnitController.bestTeams(leagueUnitId, Some(sp))
      val leagueUnitTeamStats = leagueUnitCalculatorService.calculate(leagueFixture, tillRound)

      val details = WebLeagueUnitDetails(leagueInfo = leagueInfoService.leagueInfo(leagueDetails.getLeagueId),
        currentRound = leagueInfoService.leagueInfo.currentRound(leagueDetails.getLeagueId),
        divisionLevel = leagueDetails.getLeagueLevel,
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

            Ok(views.html.leagueunit.bestTeams(viewData, leagueUnitTeamStats)(messages)).withCookies(cookies: _*)
          })
    } )
  }

  def playerStats(leagueUnitId: Long, statisticsParametersOpt: Option[StatisticsParameters]) = Action.async {implicit  request =>
    val leagueDetailsFuture = Future(hattrick.api.leagueDetails().leagueLevelUnitId(leagueUnitId).execute())

    val func: StatisticsParameters => Call = sp => routes.LeagueUnitController.playerStats(leagueUnitId, Some(sp))

    leagueDetailsFuture.flatMap ( leagueDetails => {
      val (statisticsParameters, cookies) = defaultService.statisticsParameters(statisticsParametersOpt, 
          leagueId = leagueDetails.getLeagueId(),
          statsType = Accumulate,
          sortColumn = "scored")

      val leagueFixture = hattrick.api.leagueFixtures().season(leagueInfoService.getAbsoluteSeasonFromRelative(statisticsParameters.season, leagueDetails.getLeagueId))
        .leagueLevelUnitId(leagueUnitId).execute()

      val tillRound = statisticsParameters.statsType match {
        case Round(round) => Some(round)
        case _ => None
      }

      //TODO not neccesary to calculate team stats
      val leagueUnitTeamStats = leagueUnitCalculatorService.calculate(leagueFixture, tillRound)

      val details = WebLeagueUnitDetails(leagueInfo = leagueInfoService.leagueInfo(leagueDetails.getLeagueId),
        currentRound = leagueInfoService.leagueInfo.currentRound(leagueDetails.getLeagueId),
        divisionLevel = leagueDetails.getLeagueLevel,
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

          Ok(views.html.leagueunit.playerStats(viewData)(messages)).withCookies(cookies: _*)
        })
    })
  }

  def teamState(leagueUnitId: Long, statisticsParametersOpt: Option[StatisticsParameters]) = Action.async{ implicit request =>
    val leagueDetailsFuture = Future(hattrick.api.leagueDetails().leagueLevelUnitId(leagueUnitId).execute())

    leagueDetailsFuture.flatMap ( leagueDetails => {
      val currentRound = leagueInfoService.leagueInfo.currentRound(leagueDetails.getLeagueId)

      val (statisticsParameters, cookies) = defaultService.statisticsParameters(statisticsParametersOpt, 
          leagueId = leagueDetails.getLeagueId,
          statsType = Round(currentRound),
          "rating")

      val func: StatisticsParameters => Call = sp => routes.LeagueUnitController.teamState(leagueUnitId, Some(sp))

      val leagueFixture = hattrick.api.leagueFixtures().season(leagueInfoService.getAbsoluteSeasonFromRelative(statisticsParameters.season, leagueDetails.getLeagueId))
        .leagueLevelUnitId(leagueUnitId).execute()

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
              leagueInfo = leagueInfoService.leagueInfo(leagueDetails.getLeagueId),
              currentRound = leagueInfoService.leagueInfo.currentRound(leagueDetails.getLeagueId),
              divisionLevel = leagueDetails.getLeagueLevel,
              leagueUnitName =  leagueDetails.getLeagueLevelUnitName,
              leagueUnitId = leagueUnitId,
              teamLinks = teamLinks(leagueUnitTeamStats))

            val viewData = viewDataFactory.create(details = details,
              func = func,
              statisticsType = OnlyRound,
              statisticsParameters = statisticsParameters,
              statisticsCHRequest = StatisticsCHRequest.teamStateRequest,
              entities = teamState)

            Ok(views.html.leagueunit.teamState(viewData)(messages)).withCookies(cookies: _*)
          })
    })
  }


  def playerState(leagueUnitId: Long, statisticsParametersOpt: Option[StatisticsParameters]) = Action.async { implicit request =>
    val leagueDetailsFuture = Future(hattrick.api.leagueDetails().leagueLevelUnitId(leagueUnitId).execute())

    leagueDetailsFuture.flatMap ( leagueDetails => {
      val currentRound = leagueInfoService.leagueInfo.currentRound(leagueDetails.getLeagueId)
   
      val (statisticsParameters, cookies) = defaultService.statisticsParameters(statisticsParametersOpt,
          leagueId = leagueDetails.getLeagueId(),
          statsType = Round(currentRound),
          sortColumn = "rating")

      val func: StatisticsParameters => Call = sp => routes.LeagueUnitController.playerState(leagueUnitId, Some(sp))

      val leagueFixture = hattrick.api.leagueFixtures().season(leagueInfoService.getAbsoluteSeasonFromRelative(statisticsParameters.season, leagueDetails.getLeagueId))
        .leagueLevelUnitId(leagueUnitId).execute()

      val tillRound = statisticsParameters.statsType match {
        case Round(round) => Some(round)
        case _ => None
      }

      val leagueUnitTeamStats = leagueUnitCalculatorService.calculate(leagueFixture, tillRound)

     StatisticsCHRequest.playerStateRequest.execute(leagueId = Some(leagueDetails.getLeagueId),
        divisionLevel = Some(leagueDetails.getLeagueLevel),
        leagueUnitId = Some(leagueUnitId),
        statisticsParameters = statisticsParameters)
          .map(playerStates => {
            val details = WebLeagueUnitDetails(
              leagueInfo = leagueInfoService.leagueInfo(leagueDetails.getLeagueId),
              currentRound = leagueInfoService.leagueInfo.currentRound(leagueDetails.getLeagueId),
              divisionLevel = leagueDetails.getLeagueLevel,
              leagueUnitName  = leagueDetails.getLeagueLevelUnitName,
              leagueUnitId = leagueUnitId,
              teamLinks = teamLinks(leagueUnitTeamStats))

            viewDataFactory.create(details = details,
              func = func,
              statisticsType = OnlyRound,
              statisticsParameters = statisticsParameters,
              statisticsCHRequest = StatisticsCHRequest.playerStateRequest,
              entities = playerStates)
          }).map(viewData => Ok(views.html.leagueunit.playerState(viewData)(messages)))
    } )
  }


  private def teamLinks(leagueTeamStats: Seq[LeagueUnitTeamStat]): Seq[(String, String)] = {
    leagueTeamStats.map(stat => stat.teamName -> routes.TeamController.teamOverview(stat.teamId).url)
  }
}
