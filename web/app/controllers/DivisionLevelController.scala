package controllers

import databases.ClickhouseDAO
import databases.clickhouse.{Accumulated, AvgMax, OnlyRound, StatisticsCHRequest}
import hattrick.Hattrick
import javax.inject.{Inject, Singleton}
import models.web._
import play.api.mvc.{BaseController, Call, ControllerComponents}
import service.DefaultService
import utils.Romans

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class WebDivisionLevelDetails(leagueName: String, leagueId: Int, divisionLevel: Int, divisionLevelRoman: String,
                                   leagueUnitLinks: Seq[(String, String)]) extends AbstractWebDetails

@Singleton
class DivisionLevelController@Inject() (val controllerComponents: ControllerComponents,
                                        implicit val clickhouseDAO: ClickhouseDAO,
                                        val defaultService: DefaultService,
                                        val hattrick: Hattrick,
                                        val viewDataFactory: ViewDataFactory) extends BaseController {

  def bestTeams(leagueId: Int,divisionLevel: Int, statisticsParametersOpt: Option[StatisticsParameters]) = Action.async{ implicit request =>
    val statisticsParameters = statisticsParametersOpt.getOrElse(StatisticsParameters(defaultService.currentSeason, 0, Avg, "hatstats"))

    val leagueName = defaultService.leagueIdToCountryNameMap(leagueId).getEnglishName

    val func: StatisticsParameters => Call = sp => routes.DivisionLevelController.bestTeams(leagueId, divisionLevel, Some(sp))

    val leagueUnitIdFuture = Future(hattrick.api.search().searchLeagueId(leagueId).searchType(3).searchString(Romans(divisionLevel) + "." + "1")
      .execute().getSearchResults.get(0).getResultId)

    StatisticsCHRequest.bestHatstatsTeamRequest.execute(leagueId = Some(leagueId),
      divisionLevel = Some(divisionLevel),
      statisticsParameters = statisticsParameters)
      .zipWith(leagueUnitIdFuture){case (bestTeams, leagueUnitId) =>
        val details = WebDivisionLevelDetails(leagueName = leagueName,
          leagueId = leagueId,
          divisionLevel = divisionLevel,
          divisionLevelRoman = Romans(divisionLevel),
          leagueUnitLinks = leagueUnitNumbers(statisticsParameters.season, divisionLevel, leagueUnitId))

        val viewData = viewDataFactory.create(details = details,
          func = func,
          statisticsType = AvgMax,
          statisticsParameters = statisticsParameters,
          statisticsCHRequest = StatisticsCHRequest.bestHatstatsTeamRequest,
          entities = bestTeams)
        Ok(views.html.divisionlevel.bestTeams(viewData))
      }
  }

  def bestLeagueUnits(leagueId: Int, divisionLevel: Int, statisticsParametersOpt: Option[StatisticsParameters]) = Action.async{  implicit request =>
    val statisticsParameters = statisticsParametersOpt.getOrElse(StatisticsParameters(defaultService.currentSeason, 0, Avg, "hatstats"))

    val leagueName = defaultService.leagueIdToCountryNameMap(leagueId).getEnglishName

    val func: StatisticsParameters => Call = sp => routes.DivisionLevelController.bestLeagueUnits(leagueId, divisionLevel, Some(sp))

    val leagueUnitIdFuture = Future(hattrick.api.search().searchLeagueId(leagueId).searchType(3).searchString(Romans(divisionLevel) + "." + "1")
      .execute().getSearchResults.get(0).getResultId)

    val currentRound = defaultService.currentRound(leagueId)

    StatisticsCHRequest.bestHatstatsLeagueRequest.execute(leagueId = Some(leagueId),
      divisionLevel = Some(divisionLevel),
      statisticsParameters = statisticsParameters)
      .zipWith(leagueUnitIdFuture){case (bestLeagueUnits, leagueUnitId) =>

        val details = WebDivisionLevelDetails(leagueName = leagueName,
          leagueId = leagueId,
          divisionLevel = divisionLevel,
          divisionLevelRoman = Romans(divisionLevel),
          leagueUnitLinks = leagueUnitNumbers(statisticsParameters.season, divisionLevel, leagueUnitId))

        val viewData = viewDataFactory.create(details = details,
          func = func,
          statisticsType = AvgMax,
          statisticsParameters = statisticsParameters,
          statisticsCHRequest = StatisticsCHRequest.bestHatstatsLeagueRequest,
          entities = bestLeagueUnits)

        Ok(views.html.divisionlevel.bestLeagueUnits(viewData))
      }
  }

  def playerStats(leagueId: Int, divisionLevel: Int, statisticsParametersOpt: Option[StatisticsParameters]) = Action.async { implicit request =>
    val statisticsParameters = statisticsParametersOpt.getOrElse(StatisticsParameters(defaultService.currentSeason, 0, Accumulate, "scored"))

    val leagueName = defaultService.leagueIdToCountryNameMap(leagueId).getEnglishName

    val func: StatisticsParameters => Call = sp => routes.DivisionLevelController.playerStats(leagueId, divisionLevel, Some(sp))

    val leagueUnitIdFuture = Future(hattrick.api.search().searchLeagueId(leagueId).searchType(3).searchString(Romans(divisionLevel) + "." + "1")
      .execute().getSearchResults.get(0).getResultId)

    StatisticsCHRequest.playerStatsRequest.execute(leagueId = Some(leagueId),
      divisionLevel = Some(divisionLevel),
      statisticsParameters = statisticsParameters)
      .zipWith(leagueUnitIdFuture){case (playerStats, leagueUnitId) =>

        val details = WebDivisionLevelDetails(leagueName = leagueName, leagueId = leagueId,
          divisionLevel = divisionLevel,
          divisionLevelRoman = Romans(divisionLevel),
          leagueUnitLinks = leagueUnitNumbers(statisticsParameters.season, divisionLevel, leagueUnitId))

        val viewData = viewDataFactory.create(details = details,
          func = func,
          statisticsType = Accumulated,
          statisticsParameters = statisticsParameters,
          statisticsCHRequest =StatisticsCHRequest.playerStatsRequest,
          entities = playerStats)

        Ok(views.html.divisionlevel.playerStats(viewData))
      }
  }

  def teamState(leagueId: Int, divisionLevel: Int, statisticsParametersOpt: Option[StatisticsParameters]) = Action.async { implicit request =>
    val currentRound = defaultService.currentRound(leagueId)
    val statisticsParameters = statisticsParametersOpt.getOrElse(StatisticsParameters(defaultService.currentSeason, 0, Round(currentRound), "rating"))

    val func: StatisticsParameters => Call = sp => routes.DivisionLevelController.teamState(leagueId, divisionLevel, Some(sp))

    val leagueName = defaultService.leagueIdToCountryNameMap(leagueId).getEnglishName

    val leagueUnitIdFuture = Future(hattrick.api.search().searchLeagueId(leagueId).searchType(3).searchString(Romans(divisionLevel) + "." + "1")
      .execute().getSearchResults.get(0).getResultId)

    StatisticsCHRequest.teamStateRequest.execute(leagueId = Some(leagueId),
      divisionLevel = Some(divisionLevel),
      statisticsParameters = statisticsParameters)
    .zipWith(leagueUnitIdFuture){case (teamState, leagueUnitId) =>
      val details = WebDivisionLevelDetails(leagueName = leagueName,
        leagueId = leagueId,
        divisionLevel = divisionLevel,
        divisionLevelRoman = Romans(divisionLevel),
        leagueUnitLinks = leagueUnitNumbers(statisticsParameters.season, divisionLevel, leagueUnitId))

      val viewData = viewDataFactory.create(details = details,
        func = func,
        statisticsType = OnlyRound,
        statisticsParameters = statisticsParameters,
        statisticsCHRequest = StatisticsCHRequest.teamStateRequest,
        entities = teamState)

        Ok(views.html.divisionlevel.teamState(viewData))
    }
  }

  def leagueUnitNumbers(season: Int, divisionLevel: Int, baseLeagueUnitId: Long): Seq[(String, String)] =
    defaultService.leagueNumbersMap(divisionLevel).map(number =>
      number.toString -> routes.LeagueUnitController.bestTeams(baseLeagueUnitId + number - 1, None).url)
}
