package controllers

import com.blackmorse.hattrick.api.worlddetails.model.League
import databases.ClickhouseDAO
import databases.clickhouse._
import javax.inject.{Inject, Singleton}
import models.clickhouse._
import models.web._
import play.api.i18n.Messages
import play.api.mvc.{BaseController, Call, ControllerComponents}
import service.{DefaultService, LeagueInfo, LeagueInfoService}
import utils.Romans
import com.blackmorse.hattrick.common.CommonData.higherLeagueMap

import scala.concurrent.ExecutionContext.Implicits.global

case class WebDivisionLevelDetails(leagueInfo: LeagueInfo, divisionLevel: Int, divisionLevelRoman: String,
                                   leagueUnitLinks: Seq[(String, String)]) extends AbstractWebDetails

@Singleton
class DivisionLevelController@Inject() (val controllerComponents: ControllerComponents,
                                        implicit val clickhouseDAO: ClickhouseDAO,
                                        val leagueInfoService: LeagueInfoService,
                                        val defaultService: DefaultService,
                                        val viewDataFactory: ViewDataFactory) extends BaseController with MessageSupport {

  private def stats[T](leagueId: Int,
            divisionLevel: Int,
            statisticsParametersOpt: Option[StatisticsParameters],
            sortColumn: String,
            statisticsType: StatisticsType,
            func: StatisticsParameters => Call,
            statisticsCHRequest: StatisticsCHRequest[T],
            viewFunc: ViewData[T, WebDivisionLevelDetails] => Messages => play.twirl.api.HtmlFormat.Appendable,
            selectedId: Option[Long] = None) = Action.async { implicit request =>

    val statsType = statisticsType match {
      case AvgMax => Avg
      case Accumulated => Accumulate
      case OnlyRound =>
        val currentRound = leagueInfoService.leagueInfo.currentRound(leagueId)
        Round(currentRound)
    }
   
    val (statisticsParameters, cookies) = defaultService.statisticsParameters(statisticsParametersOpt, 
        leagueId = leagueId,
        statsType = statsType,
        sortColumn = sortColumn)

    statisticsCHRequest.execute(leagueId = Some(leagueId),
      divisionLevel = Some(divisionLevel),
      statisticsParameters = statisticsParameters)
      .map{ entities =>
        val details = WebDivisionLevelDetails(leagueInfo = leagueInfoService.leagueInfo(leagueId),
          divisionLevel = divisionLevel,
          divisionLevelRoman = Romans(divisionLevel),
          leagueUnitLinks = leagueUnitLinks(leagueId, divisionLevel))

        viewDataFactory.create(details = details,
          func = func,
          statisticsType = statisticsType,
          statisticsParameters = statisticsParameters,
          statisticsCHRequest = statisticsCHRequest,
          entities = entities,
          selectedId = selectedId)
      }.map(viewData => Ok(viewFunc(viewData).apply(messages)).withCookies(cookies: _*))
  }

  def bestTeams(leagueId: Int,
                divisionLevel: Int,
                statisticsParametersOpt: Option[StatisticsParameters],
                selectedTeamId: Option[Long] = None) =
    stats(leagueId = leagueId,
      divisionLevel = divisionLevel,
      statisticsParametersOpt = statisticsParametersOpt,
      sortColumn = "hatstats",
      statisticsType = AvgMax,
      func = sp => routes.DivisionLevelController.bestTeams(leagueId, divisionLevel, Some(sp)),
      statisticsCHRequest = StatisticsCHRequest.bestHatstatsTeamRequest,
      viewFunc = {viewData: ViewData[TeamRating, WebDivisionLevelDetails] => messages => views.html.divisionlevel.bestTeams(viewData)(messages)},
      selectedId = selectedTeamId
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


  def teamState(leagueId: Int, divisionLevel: Int,
                statisticsParametersOpt: Option[StatisticsParameters],
                selectedTeamId: Option[Long] = None) =
    stats(leagueId = leagueId,
      divisionLevel = divisionLevel,
      statisticsParametersOpt = statisticsParametersOpt,
      sortColumn = "rating",
      statisticsType = OnlyRound,
      func = sp => routes.DivisionLevelController.teamState(leagueId, divisionLevel, Some(sp)),
      statisticsCHRequest = StatisticsCHRequest.teamStateRequest,
      viewFunc = {viewData: ViewData[TeamState, WebDivisionLevelDetails] => messages => views.html.divisionlevel.teamState(viewData)(messages)},
      selectedId = selectedTeamId
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

  def leagueUnitLinks(leagueId: Int, divisionLevel: Int): Seq[(String, String)] = {
    if (divisionLevel == 1) {
      Seq("1" -> routes.LeagueUnitController.bestTeams(higherLeagueMap.get(leagueId), None).url)
    } else {
      leagueInfoService.leagueNumbersMap(divisionLevel).map(number =>
        number
          .toString -> routes.LeagueUnitController.bestTeamsByName(s"${Romans.apply(divisionLevel)}.$number", leagueId, None).url)
    }
  }
}
