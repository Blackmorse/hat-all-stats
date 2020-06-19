package controllers

import com.blackmorse.hattrick.api.worlddetails.model.{Country, League}
import databases.ClickhouseDAO
import databases.clickhouse._
import javax.inject.{Inject, Singleton}
import models.clickhouse._
import models.web
import models.web._
import play.api.i18n.{I18nSupport, Messages}
import play.api.mvc._
import service.{DefaultService, LeagueInfo}
import utils.Romans

import scala.concurrent.ExecutionContext.Implicits.global

case class DivisionLevelForm(divisionLevel: Int)

case class WebLeagueDetails(leagueInfo: LeagueInfo,
                            divisionLevelsLinks: Seq[(String, String)]) extends AbstractWebDetails

@Singleton
class LeagueController @Inject() (val controllerComponents: ControllerComponents,
                                  implicit val clickhouseDAO: ClickhouseDAO,
                                  val defaultService: DefaultService,
                                  val viewDataFactory: ViewDataFactory) extends BaseController with I18nSupport with MessageSupport {

  private def stats[T](leagueId: Int,
                       statisticsParametersOpt: Option[StatisticsParameters],
                       sortColumn: String,
                       statisticsType: StatisticsType,
                       func: StatisticsParameters => Call,
                       statisticsCHRequest: StatisticsCHRequest[T],
                       viewFunc: ViewData[T, WebLeagueDetails] => Messages => play.twirl.api.HtmlFormat.Appendable,
                       selectedId: Option[Long] = None) = Action.async { implicit request =>
    val statsType = statisticsType match {
      case AvgMax => Avg
      case Accumulated => Accumulate
      case OnlyRound =>
        val currentRound = defaultService.leagueInfo.currentRound(leagueId)
        Round(currentRound)
    }

    val statisticsParameters =
      statisticsParametersOpt.getOrElse(StatisticsParameters(defaultService.leagueInfo.currentSeason(leagueId), 0, statsType, sortColumn, DefaultService.PAGE_SIZE, Desc))

    val details = WebLeagueDetails(leagueInfo = defaultService.leagueInfo(leagueId),
      divisionLevelsLinks = divisionLevels(leagueId))

    request.session.data.contains("lang")

    statisticsCHRequest.execute(leagueId = Some(leagueId),
      statisticsParameters = statisticsParameters)
      .map(entities => viewDataFactory.create(details = details,
        func = func,
        statisticsType = statisticsType,
        statisticsParameters = statisticsParameters,
        statisticsCHRequest = statisticsCHRequest,
        entities = entities,
        selectedId = selectedId))
      .map(viewData => Ok(viewFunc(viewData).apply(messages)))
  }

  def bestTeams(leagueId: Int,
                statisticsParametersOpt: Option[StatisticsParameters],
                selectedTeamId: Option[Long]) = {
    stats(leagueId = leagueId,
      statisticsParametersOpt = statisticsParametersOpt,
      sortColumn = "hatstats",
      statisticsType = AvgMax,
      func = sp => routes.LeagueController.bestTeams(leagueId, Some(sp)),
      statisticsCHRequest = StatisticsCHRequest.bestHatstatsTeamRequest,
      viewFunc = { viewData: web.ViewData[TeamRating, WebLeagueDetails] => messages => views.html.league.bestTeams(viewData)(messages) },
      selectedId = selectedTeamId
    )
  }

  def bestLeagueUnits(leagueId: Int, statisticsParametersOpt: Option[StatisticsParameters]) =
    stats(leagueId = leagueId,
      statisticsParametersOpt = statisticsParametersOpt,
      sortColumn = "hatstats",
      statisticsType = AvgMax,
      func = sp => routes.LeagueController.bestLeagueUnits(leagueId, Some(sp)),
      statisticsCHRequest = StatisticsCHRequest.bestHatstatsLeagueRequest,
      viewFunc = {viewData: web.ViewData[LeagueUnitRating, WebLeagueDetails] => messages => views.html.league.bestLeagueUnits(viewData)(messages)}
    )

  def playerStats(leagueId: Int, statisticsParametersOpt: Option[StatisticsParameters]) =
    stats(leagueId = leagueId,
      statisticsParametersOpt = statisticsParametersOpt,
      sortColumn = "scored",
      statisticsType = Accumulated,
      func = sp =>  routes.LeagueController.playerStats(leagueId, Some(sp)),
      statisticsCHRequest = StatisticsCHRequest.playerStatsRequest,
      viewFunc = {viewData: web.ViewData[PlayerStats, WebLeagueDetails] => messages => views.html.league.playerStats(viewData)(messages)}
    )

  def teamState(leagueId: Int,
                statisticsParametersOpt: Option[StatisticsParameters],
                selectedTeam: Option[Long] = None) =
    stats(leagueId = leagueId,
      statisticsParametersOpt =  statisticsParametersOpt,
      sortColumn = "rating",
      statisticsType = OnlyRound,
      func = sp => routes.LeagueController.teamState(leagueId, Some(sp)),
      statisticsCHRequest = StatisticsCHRequest.teamStateRequest,
      viewFunc = {viewData: web.ViewData[TeamState, WebLeagueDetails] => messages => views.html.league.teamState(viewData)(messages)},
      selectedId = selectedTeam)

  def playerState(leagueId: Int, statisticsParametersOpt: Option[StatisticsParameters]) =
    stats(leagueId = leagueId,
      statisticsParametersOpt = statisticsParametersOpt,
      sortColumn = "rating",
      statisticsType = OnlyRound,
      func = sp => routes.LeagueController.playerState(leagueId, Some(sp)),
      statisticsCHRequest = StatisticsCHRequest.playerStateRequest,
      viewFunc = {viewData: web.ViewData[PlayersState, WebLeagueDetails] => messages =>views.html.league.playerState(viewData)(messages)}
    )

  def formalTeamStats(leagueId: Int, statisticsParametersOpt: Option[StatisticsParameters]) =
    stats(leagueId = leagueId,
      statisticsParametersOpt = statisticsParametersOpt,
      sortColumn = "points",
      statisticsType = OnlyRound,
      func = sp => routes.LeagueController.formalTeamStats(leagueId, Some(sp)),
      statisticsCHRequest = StatisticsCHRequest.formalTeamStats,
      viewFunc = {viewData: web.ViewData[FormalTeamStats, WebLeagueDetails] => messages => views.html.league.formalTeamStats(viewData)(messages)}
    )
  private def divisionLevels(leagueId: Int): Seq[(String, String)] = {
    val maxLevels = defaultService.leagueInfo(leagueId).league.getNumberOfLevels
    (1 to maxLevels)
      .map(i => Romans(i) -> routes.DivisionLevelController.bestTeams(leagueId, i).url )
  }
}