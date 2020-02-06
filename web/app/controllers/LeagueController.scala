package controllers

import databases.ClickhouseDAO
import databases.clickhouse.{Accumulated, AvgMax, OnlyRound, StatisticsCHRequest}
import javax.inject.{Inject, Singleton}
import models.web._
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.{BaseController, Call, ControllerComponents}
import service.DefaultService
import utils.Romans

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._

case class DivisionLevelForm(divisionLevel: Int)

case class WebLeagueDetails(leagueName: String,
                            leagueId: Int,
                            form: Form[DivisionLevelForm],
                            divisionLevelsLinks: Seq[(String, String)]) extends AbstractWebDetails

@Singleton
class LeagueController @Inject() (val controllerComponents: ControllerComponents,
                                  implicit val clickhouseDAO: ClickhouseDAO,
                                  val defaultService: DefaultService,
                                  val viewDataFactory: ViewDataFactory) extends BaseController with play.api.i18n.I18nSupport {

  val form: Form[DivisionLevelForm] = Form(mapping(
    "division_level" -> number
    )(DivisionLevelForm.apply)(DivisionLevelForm.unapply))


  def bestTeams(leagueId: Int, statisticsParametersOpt: Option[StatisticsParameters]) = Action.async { implicit request =>
    val statisticsParameters = statisticsParametersOpt.getOrElse(StatisticsParameters(defaultService.currentSeason, 0, Avg, "hatstats"))

    val leagueName = defaultService.leagueIdToCountryNameMap(leagueId).getEnglishName

    val func: StatisticsParameters => Call = sp => routes.LeagueController.bestTeams(leagueId, Some(sp))

    val details = WebLeagueDetails(leagueName = leagueName,
      leagueId = leagueId,
      form = form,
      divisionLevelsLinks = divisionLevels(leagueId))

      StatisticsCHRequest.bestHatstatsTeamRequest.execute(leagueId = Some(leagueId),
        statisticsParameters = statisticsParameters)
        .map(bestTeams => {
          val viewData = viewDataFactory.create(details = details,
            func = func,
            statisticsType = AvgMax,
            statisticsParameters = statisticsParameters,
            statisticsCHRequest = StatisticsCHRequest.bestHatstatsTeamRequest,
            entities = bestTeams
          )

          Ok(views.html.league.bestTeams(viewData))
        })
  }


  def bestLeagueUnits(leagueId: Int, statisticsParametersOpt: Option[StatisticsParameters]) = Action.async {implicit request =>
    val statisticsParameters = statisticsParametersOpt.getOrElse(StatisticsParameters(defaultService.currentSeason, 0, Avg, "hatstats"))

    val leagueName = defaultService.leagueIdToCountryNameMap(leagueId).getEnglishName

    val func: StatisticsParameters => Call = sp => routes.LeagueController.bestLeagueUnits(leagueId, Some(sp))

    val details = WebLeagueDetails(leagueName = leagueName,
      leagueId = leagueId,
      form = form,
      divisionLevelsLinks = divisionLevels(leagueId))

      StatisticsCHRequest.bestHatstatsLeagueRequest.execute(leagueId = Some(leagueId),
        statisticsParameters = statisticsParameters)
        .map(bestLeagueUnits => viewDataFactory.create(details = details,
          func = func,
          statisticsType = AvgMax,
          statisticsParameters = statisticsParameters,
          statisticsCHRequest = StatisticsCHRequest.bestHatstatsLeagueRequest,
          entities = bestLeagueUnits
        )).map(viewData => Ok(views.html.league.bestLeagueUnits(viewData)))
  }

  def playerStats(leagueId: Int, statisticsParametersOpt: Option[StatisticsParameters]) = Action.async {implicit request =>
    val statisticsParameters = statisticsParametersOpt.getOrElse(StatisticsParameters(defaultService.currentSeason, 0, Accumulate, "scored"))

    val leagueName = defaultService.leagueIdToCountryNameMap(leagueId).getEnglishName

    val func: StatisticsParameters => Call = sp =>  routes.LeagueController.playerStats(leagueId, Some(sp))

    val details = WebLeagueDetails(leagueName = leagueName,
      leagueId = leagueId,
      form = form,
      divisionLevelsLinks = divisionLevels(leagueId))

    StatisticsCHRequest.playerStatsRequest.execute(leagueId = Some(leagueId),
      statisticsParameters = statisticsParameters)
        .map(playerStats => {
          viewDataFactory.create(details = details,
            func = func,
            statisticsType = Accumulated,
            statisticsParameters = statisticsParameters,
            statisticsCHRequest = StatisticsCHRequest.playerStatsRequest,
            entities = playerStats)
        }).map(viewData => Ok(views.html.league.playerStats(viewData)))
  }

  def teamState(leagueId: Int, statisticsParametersOpt: Option[StatisticsParameters]) = Action.async { implicit request =>
    val currentRound = defaultService.currentRound(leagueId)

    val statisticsParameters = statisticsParametersOpt.getOrElse(StatisticsParameters(defaultService.currentSeason, 0, Round(currentRound), "rating"))

    val leagueName = defaultService.leagueIdToCountryNameMap(leagueId).getEnglishName

    val func: StatisticsParameters => Call = sp => routes.LeagueController.teamState(leagueId, Some(sp))

    val details = WebLeagueDetails(leagueName = leagueName,
      leagueId = leagueId,
      form = form,
      divisionLevelsLinks = divisionLevels(leagueId))

    StatisticsCHRequest.teamStateRequest.execute(leagueId = Some(leagueId),
      statisticsParameters = statisticsParameters)
    .map(teamStates =>
      viewDataFactory.create(details = details,
        func = func,
        statisticsType = OnlyRound,
        statisticsParameters = statisticsParameters,
        statisticsCHRequest = StatisticsCHRequest.teamStateRequest,
        entities = teamStates))
    .map(viewData => Ok(views.html.league.teamState(viewData)))
  }


  private def divisionLevels(leagueId: Int): Seq[(String, String)] = {
    val maxLevels = defaultService.leagueIdToCountryNameMap(leagueId).getNumberOfLevels
    (1 to maxLevels)
      .map(i => Romans(i) -> routes.DivisionLevelController.bestTeams(leagueId, i).url )
  }
}


