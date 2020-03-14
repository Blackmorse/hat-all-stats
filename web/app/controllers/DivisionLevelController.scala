package controllers

import com.blackmorse.hattrick.api.worlddetails.model.League
import databases.ClickhouseDAO
import databases.clickhouse._
import javax.inject.{Inject, Singleton}
import models.clickhouse._
import models.web._
import play.api.i18n.Messages
import play.api.mvc.{BaseController, Call, ControllerComponents}
import service.DefaultService
import utils.Romans

import scala.concurrent.ExecutionContext.Implicits.global

case class WebDivisionLevelDetails(league: League, divisionLevel: Int, divisionLevelRoman: String,
                                   leagueUnitLinks: Seq[(String, String)]) extends AbstractWebDetails

@Singleton
class DivisionLevelController@Inject() (val controllerComponents: ControllerComponents,
                                        implicit val clickhouseDAO: ClickhouseDAO,
                                        val defaultService: DefaultService,
                                        val viewDataFactory: ViewDataFactory) extends BaseController with MessageSupport {

  private def stats[T](leagueId: Int,
            divisionLevel: Int,
            statisticsParametersOpt: Option[StatisticsParameters],
            sortColumn: String,
            statisticsType: StatisticsType,
            func: StatisticsParameters => Call,
            statisticsCHRequest: StatisticsCHRequest[T],
            viewFunc: ViewData[T, WebDivisionLevelDetails] => Messages => play.twirl.api.HtmlFormat.Appendable) = Action.async { implicit request =>

    val statsType = statisticsType match {
      case AvgMax => Avg
      case Accumulated => Accumulate
      case OnlyRound =>
        val currentRound = defaultService.currentRound(leagueId)
        Round(currentRound)
    }
    val statisticsParameters =
      statisticsParametersOpt.getOrElse(StatisticsParameters(defaultService.currentSeason, 0, statsType, sortColumn, DefaultService.PAGE_SIZE))

    val leagueUnitIdFuture = defaultService.firstIdOfDivisionLeagueUnit(leagueId, divisionLevel)

    statisticsCHRequest.execute(leagueId = Some(leagueId),
      divisionLevel = Some(divisionLevel),
      statisticsParameters = statisticsParameters)
      .zipWith(leagueUnitIdFuture){case(entities, leagueUnitId) =>
        val details = WebDivisionLevelDetails(league = defaultService.leagueIdToCountryNameMap(leagueId),
          divisionLevel = divisionLevel,
          divisionLevelRoman = Romans(divisionLevel),
          leagueUnitLinks = leagueUnitNumbers(statisticsParameters.season, divisionLevel, leagueUnitId))

        viewDataFactory.create(details = details,
          func = func,
          statisticsType = statisticsType,
          statisticsParameters = statisticsParameters,
          statisticsCHRequest = statisticsCHRequest,
          entities = entities)
      }.map(viewData => Ok(viewFunc(viewData).apply(messages)))
  }

  def bestTeams(leagueId: Int,divisionLevel: Int, statisticsParametersOpt: Option[StatisticsParameters]) =
    stats(leagueId = leagueId,
      divisionLevel = divisionLevel,
      statisticsParametersOpt = statisticsParametersOpt,
      sortColumn = "hatstats",
      statisticsType = AvgMax,
      func = sp => routes.DivisionLevelController.bestTeams(leagueId, divisionLevel, Some(sp)),
      statisticsCHRequest = StatisticsCHRequest.bestHatstatsTeamRequest,
      viewFunc = {viewData: ViewData[TeamRating, WebDivisionLevelDetails] => messages => views.html.divisionlevel.bestTeams(viewData)(messages)}
    )

  def bestLeagueUnits(leagueId: Int, divisionLevel: Int, statisticsParametersOpt: Option[StatisticsParameters]) =
    stats(leagueId = leagueId,
      divisionLevel = divisionLevel,
      statisticsParametersOpt = statisticsParametersOpt,
      sortColumn = "hatstats",
      statisticsType = AvgMax,
      func = sp => routes.DivisionLevelController.bestLeagueUnits(leagueId, divisionLevel, Some(sp)),
      statisticsCHRequest = StatisticsCHRequest.bestHatstatsLeagueRequest,
      viewFunc = {viewData: ViewData[LeagueUnitRating, WebDivisionLevelDetails] => messages => views.html.divisionlevel.bestLeagueUnits(viewData)(messages)}
    )


  def playerStats(leagueId: Int, divisionLevel: Int, statisticsParametersOpt: Option[StatisticsParameters]) =
    stats(leagueId = leagueId,
      divisionLevel = divisionLevel,
      statisticsParametersOpt = statisticsParametersOpt,
      sortColumn = "scored",
      statisticsType = Accumulated,
      func = sp => routes.DivisionLevelController.playerStats(leagueId, divisionLevel, Some(sp)),
      statisticsCHRequest = StatisticsCHRequest.playerStatsRequest,
      viewFunc = {viewData: ViewData[PlayerStats, WebDivisionLevelDetails] => messages => views.html.divisionlevel.playerStats(viewData)(messages)}
    )


  def teamState(leagueId: Int, divisionLevel: Int, statisticsParametersOpt: Option[StatisticsParameters]) =
    stats(leagueId = leagueId,
      divisionLevel = divisionLevel,
      statisticsParametersOpt = statisticsParametersOpt,
      sortColumn = "rating",
      statisticsType = OnlyRound,
      func = sp => routes.DivisionLevelController.teamState(leagueId, divisionLevel, Some(sp)),
      statisticsCHRequest = StatisticsCHRequest.teamStateRequest,
      viewFunc = {viewData: ViewData[TeamState, WebDivisionLevelDetails] => messages => views.html.divisionlevel.teamState(viewData)(messages)}
    )


  def playerState(leagueId: Int, divisionLevel: Int, statisticsParametersOpt: Option[StatisticsParameters]) =
    stats(leagueId = leagueId,
      divisionLevel = divisionLevel,
      statisticsParametersOpt = statisticsParametersOpt,
      sortColumn = "rating",
      statisticsType = OnlyRound,
      func = sp => routes.DivisionLevelController.playerState(leagueId, divisionLevel, Some(sp)),
      statisticsCHRequest = StatisticsCHRequest.playerStateRequest,
      viewFunc = {viewData: ViewData[PlayersState, WebDivisionLevelDetails] => messages => views.html.divisionlevel.playerState(viewData)(messages)}
    )


  def formalTeamStats(leagueId: Int, divisionLevel: Int, statisticsParametersOpt: Option[StatisticsParameters]) =
    stats(leagueId = leagueId,
      divisionLevel = divisionLevel,
      statisticsParametersOpt = statisticsParametersOpt,
      sortColumn = "points",
      statisticsType = OnlyRound,
      func = sp => routes.DivisionLevelController.formalTeamStats(leagueId, divisionLevel, Some(sp)),
      statisticsCHRequest = StatisticsCHRequest.formalTeamStats,
      viewFunc = {viewData: ViewData[FormalTeamStats, WebDivisionLevelDetails] => messages => views.html.divisionlevel.formalTeamStats(viewData)(messages)}
    )


  def leagueUnitNumbers(season: Int, divisionLevel: Int, baseLeagueUnitId: Long): Seq[(String, String)] =
    defaultService.leagueNumbersMap(divisionLevel).map(number =>
      number.toString -> routes.LeagueUnitController.bestTeams(baseLeagueUnitId + number - 1, None).url)
}
