package controllers

import databases.ClickhouseDAO
import databases.clickhouse._
import hattrick.Hattrick
import javax.inject.{Inject, Singleton}
import models.clickhouse._
import models.web._
import play.api.i18n.Messages
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

  private def stats[T](leagueId: Int,
            divisionLevel: Int,
            statisticsParametersOpt: Option[StatisticsParameters],
            sortColumn: String,
            statisticsType: StatisticsType,
            func: StatisticsParameters => Call,
            statisticsCHRequest: StatisticsCHRequest[T],
            viewFunc: ViewData[T, WebDivisionLevelDetails] => play.twirl.api.HtmlFormat.Appendable) = Action.async { implicit request =>

    val statsType = statisticsType match {
      case AvgMax => Avg
      case Accumulated => Accumulate
      case OnlyRound =>
        val currentRound = defaultService.currentRound(leagueId)
        Round(currentRound)
    }
    val statisticsParameters = statisticsParametersOpt.getOrElse(StatisticsParameters(defaultService.currentSeason, 0, statsType, sortColumn))

    val leagueName = defaultService.leagueIdToCountryNameMap(leagueId).getEnglishName

    val leagueUnitIdFuture = Future(hattrick.api.search().searchLeagueId(leagueId).searchType(3).searchString(Romans(divisionLevel) + "." + "1")
      .execute().getSearchResults.get(0).getResultId)

    statisticsCHRequest.execute(leagueId = Some(leagueId),
      divisionLevel = Some(divisionLevel),
      statisticsParameters = statisticsParameters)
      .zipWith(leagueUnitIdFuture){case(entities, leagueUnitId) =>
        val details = WebDivisionLevelDetails(leagueName = leagueName,
          leagueId = leagueId,
          divisionLevel = divisionLevel,
          divisionLevelRoman = Romans(divisionLevel),
          leagueUnitLinks = leagueUnitNumbers(statisticsParameters.season, divisionLevel, leagueUnitId))

        viewDataFactory.create(details = details,
          func = func,
          statisticsType = statisticsType,
          statisticsParameters = statisticsParameters,
          statisticsCHRequest = statisticsCHRequest,
          entities = entities)
      }.map(viewData => Ok(viewFunc(viewData)))
  }

  def bestTeams(leagueId: Int,divisionLevel: Int, statisticsParametersOpt: Option[StatisticsParameters]) =
    stats(leagueId = leagueId,
      divisionLevel = divisionLevel,
      statisticsParametersOpt = statisticsParametersOpt,
      sortColumn = "hatstats",
      statisticsType = AvgMax,
      func = sp => routes.DivisionLevelController.bestTeams(leagueId, divisionLevel, Some(sp)),
      statisticsCHRequest = StatisticsCHRequest.bestHatstatsTeamRequest,
      viewFunc = {viewData: ViewData[TeamRating, WebDivisionLevelDetails] => views.html.divisionlevel.bestTeams(viewData)}
    )

  def bestLeagueUnits(leagueId: Int, divisionLevel: Int, statisticsParametersOpt: Option[StatisticsParameters]) =
    stats(leagueId = leagueId,
      divisionLevel = divisionLevel,
      statisticsParametersOpt = statisticsParametersOpt,
      sortColumn = "hatstats",
      statisticsType = AvgMax,
      func = sp => routes.DivisionLevelController.bestLeagueUnits(leagueId, divisionLevel, Some(sp)),
      statisticsCHRequest = StatisticsCHRequest.bestHatstatsLeagueRequest,
      viewFunc = {viewData: ViewData[LeagueUnitRating, WebDivisionLevelDetails] => views.html.divisionlevel.bestLeagueUnits(viewData)}
    )


  def playerStats(leagueId: Int, divisionLevel: Int, statisticsParametersOpt: Option[StatisticsParameters]) =
    stats(leagueId = leagueId,
      divisionLevel = divisionLevel,
      statisticsParametersOpt = statisticsParametersOpt,
      sortColumn = "scored",
      statisticsType = Accumulated,
      func = sp => routes.DivisionLevelController.playerStats(leagueId, divisionLevel, Some(sp)),
      statisticsCHRequest = StatisticsCHRequest.playerStatsRequest,
      viewFunc = {viewData: ViewData[PlayerStats, WebDivisionLevelDetails] => views.html.divisionlevel.playerStats(viewData)}
    )


  def teamState(leagueId: Int, divisionLevel: Int, statisticsParametersOpt: Option[StatisticsParameters]) =
    stats(leagueId = leagueId,
      divisionLevel = divisionLevel,
      statisticsParametersOpt = statisticsParametersOpt,
      sortColumn = "rating",
      statisticsType = OnlyRound,
      func = sp => routes.DivisionLevelController.teamState(leagueId, divisionLevel, Some(sp)),
      statisticsCHRequest = StatisticsCHRequest.teamStateRequest,
      viewFunc = {viewData: ViewData[TeamState, WebDivisionLevelDetails] => views.html.divisionlevel.teamState(viewData)}
    )


  def playerState(leagueId: Int, divisionLevel: Int, statisticsParametersOpt: Option[StatisticsParameters]) =
    stats(leagueId = leagueId,
      divisionLevel = divisionLevel,
      statisticsParametersOpt = statisticsParametersOpt,
      sortColumn = "rating",
      statisticsType = OnlyRound,
      func = sp => routes.DivisionLevelController.playerState(leagueId, divisionLevel, Some(sp)),
      statisticsCHRequest = StatisticsCHRequest.playerStateRequest,
      viewFunc = {viewData: ViewData[PlayersState, WebDivisionLevelDetails] => views.html.divisionlevel.playerState(viewData)}
    )


  def formalTeamStats(leagueId: Int, divisionLevel: Int, statisticsParametersOpt: Option[StatisticsParameters]) =
    stats(leagueId = leagueId,
      divisionLevel = divisionLevel,
      statisticsParametersOpt = statisticsParametersOpt,
      sortColumn = "points",
      statisticsType = OnlyRound,
      func = sp => routes.DivisionLevelController.formalTeamStats(leagueId, divisionLevel, Some(sp)),
      statisticsCHRequest = StatisticsCHRequest.formalTeamStats,
      viewFunc = {viewData: ViewData[FormalTeamStats, WebDivisionLevelDetails] => views.html.divisionlevel.formalTeamStats(viewData)}
    )


  def leagueUnitNumbers(season: Int, divisionLevel: Int, baseLeagueUnitId: Long): Seq[(String, String)] =
    defaultService.leagueNumbersMap(divisionLevel).map(number =>
      number.toString -> routes.LeagueUnitController.bestTeams(baseLeagueUnitId + number - 1, None).url)
}
