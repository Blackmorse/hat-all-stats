package controllers

import databases.ClickhouseDAO
import databases.clickhouse._
import javax.inject.{Inject, Singleton}
import models.clickhouse._
import models.web
import models.web._
import play.api.i18n.Messages
import play.api.mvc._
import service.DefaultService
import utils.Romans

import scala.concurrent.ExecutionContext.Implicits.global

case class DivisionLevelForm(divisionLevel: Int)

case class WebLeagueDetails(leagueName: String,
                            leagueId: Int,
                            divisionLevelsLinks: Seq[(String, String)]) extends AbstractWebDetails

@Singleton
class LeagueController @Inject() (val controllerComponents: ControllerComponents,
                                  implicit val clickhouseDAO: ClickhouseDAO,
                                  val defaultService: DefaultService,
                                  val viewDataFactory: ViewDataFactory) extends BaseController {

  private def stats[T](leagueId: Int,
            statisticsParametersOpt: Option[StatisticsParameters],
            sortColumn: String,
            statisticsType: StatisticsType,
            func: StatisticsParameters => Call,
            statisticsCHRequest: StatisticsCHRequest[T],
            viewFunc: ViewData[T, WebLeagueDetails] => play.twirl.api.HtmlFormat.Appendable) = Action.async { implicit request =>
    val statsType = statisticsType match {
      case AvgMax => Avg
      case Accumulated => Accumulate
      case OnlyRound =>
        val currentRound = defaultService.currentRound(leagueId)
        Round(currentRound)
    }

    val statisticsParameters = statisticsParametersOpt.getOrElse(StatisticsParameters(defaultService.currentSeason, 0, statsType, sortColumn))
    val leagueName = defaultService.leagueIdToCountryNameMap(leagueId).getEnglishName

    val details = WebLeagueDetails(leagueName = leagueName,
      leagueId = leagueId,
      divisionLevelsLinks = divisionLevels(leagueId))

    statisticsCHRequest.execute(leagueId = Some(leagueId),
      statisticsParameters = statisticsParameters)
        .map(entities => viewDataFactory.create(details = details,
          func = func,
          statisticsType = statisticsType,
          statisticsParameters = statisticsParameters,
          statisticsCHRequest = statisticsCHRequest,
          entities = entities))
      .map(viewData => Ok(viewFunc(viewData)))
  }

  def bestTeams(leagueId: Int, statisticsParametersOpt: Option[StatisticsParameters]) =
    stats(leagueId = leagueId,
      statisticsParametersOpt = statisticsParametersOpt,
      sortColumn = "hatstats",
      statisticsType = AvgMax,
      func = sp => routes.LeagueController.bestTeams(leagueId, Some(sp)),
      statisticsCHRequest = StatisticsCHRequest.bestHatstatsTeamRequest,
      viewFunc = {viewData: web.ViewData[TeamRating, WebLeagueDetails] => views.html.league.bestTeams(viewData)}
    )

  def bestLeagueUnits(leagueId: Int, statisticsParametersOpt: Option[StatisticsParameters]) =
    stats(leagueId = leagueId,
      statisticsParametersOpt = statisticsParametersOpt,
      sortColumn = "hatstats",
      statisticsType = AvgMax,
      func = sp => routes.LeagueController.bestLeagueUnits(leagueId, Some(sp)),
      statisticsCHRequest = StatisticsCHRequest.bestHatstatsLeagueRequest,
      viewFunc = {viewData: web.ViewData[LeagueUnitRating, WebLeagueDetails] => views.html.league.bestLeagueUnits(viewData)}
    )

  def playerStats(leagueId: Int, statisticsParametersOpt: Option[StatisticsParameters]) =
    stats(leagueId = leagueId,
      statisticsParametersOpt = statisticsParametersOpt,
      sortColumn = "scored",
      statisticsType = Accumulated,
      func = sp =>  routes.LeagueController.playerStats(leagueId, Some(sp)),
      statisticsCHRequest = StatisticsCHRequest.playerStatsRequest,
      viewFunc = {viewData: web.ViewData[PlayerStats, WebLeagueDetails] => views.html.league.playerStats(viewData)}
    )

  def teamState(leagueId: Int, statisticsParametersOpt: Option[StatisticsParameters]) =
    stats(leagueId = leagueId,
      statisticsParametersOpt =  statisticsParametersOpt,
      sortColumn = "rating",
      statisticsType = OnlyRound,
      func = sp => routes.LeagueController.teamState(leagueId, Some(sp)),
      statisticsCHRequest = StatisticsCHRequest.teamStateRequest,
      viewFunc = {viewData: web.ViewData[TeamState, WebLeagueDetails] => views.html.league.teamState(viewData)})

  def playerState(leagueId: Int, statisticsParametersOpt: Option[StatisticsParameters]) =
    stats(leagueId = leagueId,
      statisticsParametersOpt = statisticsParametersOpt,
      sortColumn = "rating",
      statisticsType = OnlyRound,
      func = sp => routes.LeagueController.playerState(leagueId, Some(sp)),
      statisticsCHRequest = StatisticsCHRequest.playerStateRequest,
      viewFunc = {viewData: web.ViewData[PlayersState, WebLeagueDetails] =>views.html.league.playerState(viewData)}
    )

  def formalTeamStats(leagueId: Int, statisticsParametersOpt: Option[StatisticsParameters]) =
    stats(leagueId = leagueId,
      statisticsParametersOpt = statisticsParametersOpt,
      sortColumn = "points",
      statisticsType = OnlyRound,
      func = sp => routes.LeagueController.formalTeamStats(leagueId, Some(sp)),
      statisticsCHRequest = StatisticsCHRequest.formalTeamStats,
      viewFunc = {viewData: web.ViewData[FormalTeamStats, WebLeagueDetails] => views.html.league.formalTeamStats(viewData)}
    )
  private def divisionLevels(leagueId: Int): Seq[(String, String)] = {
    val maxLevels = defaultService.leagueIdToCountryNameMap(leagueId).getNumberOfLevels
    (1 to maxLevels)
      .map(i => Romans(i) -> routes.DivisionLevelController.bestTeams(leagueId, i).url )
  }
}