package controllers

import databases.ClickhouseDAO
import databases.clickhouse.StatisticsCHRequest
import javax.inject.{Inject, Singleton}
import models.web._
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.{BaseController, ControllerComponents}
import service.DefaultService
import utils.Romans

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._

case class DivisionLevelForm(divisionLevel: Int)

case class WebLeagueDetails(leagueName: String,
                            leagueId: Int,
                            form: Form[DivisionLevelForm],
                            divisionLevelsLinks: Seq[(String, String)],
                            seasonInfo: SeasonInfo,
                            statTypeLinks: StatTypeLinks,
                            sortByLinks: SortByLinks) extends AbstractWebDetails

@Singleton
class LeagueController @Inject() (val controllerComponents: ControllerComponents,
                                  implicit val clickhouseDAO: ClickhouseDAO,
                                  val defaultService: DefaultService) extends BaseController with play.api.i18n.I18nSupport {

  val form: Form[DivisionLevelForm] = Form(mapping(
    "division_level" -> number
    )(DivisionLevelForm.apply)(DivisionLevelForm.unapply))


  def bestTeams(leagueId: Int, season: Int, page: Int, statsType: StatsType, sortBy: String) = Action.async { implicit request =>
    val leagueName = defaultService.leagueIdToCountryNameMap(leagueId).getEnglishName

    val seasonFunction: Int => String = s => routes.LeagueController.bestTeams(leagueId ,s ,0).url
    val seasonInfo = SeasonInfo(season, defaultService.seasonsWithLinks(leagueId, seasonFunction))

    val pageUrlFunc: Int => String = p => routes.LeagueController.bestTeams(leagueId, season, p, statsType, sortBy).url
    val statTypeUrlFunc: StatsType => String = st => routes.LeagueController.bestTeams(leagueId, season, page, st, sortBy).url
    val sortByFunc: String => String = sb => routes.LeagueController.bestTeams(leagueId, season, page,statsType, sb).url

    val currentRound = defaultService.currentRound(leagueId)
    val details = WebLeagueDetails(leagueName, leagueId, form, divisionLevels(leagueId, season), seasonInfo,
      StatTypeLinks.withAverages(statTypeUrlFunc, currentRound, statsType),
      SortByLinks(sortByFunc, StatisticsCHRequest.bestHatstatsTeamRequest.sortingColumns, sortBy))

      StatisticsCHRequest.bestHatstatsTeamRequest.execute(leagueId = Some(leagueId), season = Some(season), page = page, statsType = statsType, sortBy = sortBy)
        .map(bestTeams => Ok(views.html.league.bestTeams(details,
          WebPagedEntities(bestTeams, page, pageUrlFunc))))
  }

  def bestLeagueUnits(leagueId: Int, season: Int, page: Int, statsType: StatsType, sortBy: String) = Action.async {implicit request =>
    val leagueName = defaultService.leagueIdToCountryNameMap(leagueId).getEnglishName

    val seasonFunction: Int => String = s => routes.LeagueController.bestLeagueUnits(leagueId ,s ,0).url
    val seasonInfo = SeasonInfo(season, defaultService.seasonsWithLinks(leagueId, seasonFunction))

    val pageUrlFunc: Int => String = p => routes.LeagueController.bestLeagueUnits(leagueId, season, p, statsType, sortBy).url
    val statsTypeFunc: StatsType => String = st => routes.LeagueController.bestLeagueUnits(leagueId, season, page, st, sortBy).url
    val sortByFunc: String => String = sb => routes.LeagueController.bestLeagueUnits(leagueId, season, page, statsType, sb).url

    val currentRound = defaultService.currentRound(leagueId)
    val details = WebLeagueDetails(leagueName = leagueName,
      leagueId = leagueId,
      form = form,
      divisionLevelsLinks = divisionLevels(leagueId, season),
      seasonInfo = seasonInfo,
      statTypeLinks = StatTypeLinks.withAverages(statsTypeFunc, currentRound, statsType),
      sortByLinks = SortByLinks(sortByFunc, StatisticsCHRequest.bestHatstatsLeagueRequest.sortingColumns, sortBy))

      StatisticsCHRequest.bestHatstatsLeagueRequest.execute(leagueId = Some(leagueId), season = Some(season), page = page, statsType = statsType, sortBy = sortBy)
        .map(bestLeagueUnits => Ok(views.html.league.bestLeagueUnits(details,
          WebPagedEntities(bestLeagueUnits, page, pageUrlFunc))))
  }

  def playerStats(leagueId: Int, season: Int, page: Int, statsType: StatsType, sortBy: String) = Action.async {implicit request =>
    val leagueName = defaultService.leagueIdToCountryNameMap(leagueId).getEnglishName

    val seasonFunction: Int => String = s => routes.LeagueController.playerStats(leagueId, s, 0).url
    val seasonInfo = SeasonInfo(season, defaultService.seasonsWithLinks(leagueId, seasonFunction))

    val pageFunc: Int => String = p => routes.LeagueController.playerStats(leagueId, season, p, statsType, sortBy).url
    val statsTypeFunc: StatsType => String = st => routes.LeagueController.playerStats(leagueId, season, page, st, sortBy).url
    val sortByFunc: String => String = sb => routes.LeagueController.playerStats(leagueId, season, page, statsType, sb).url

    val currentRound = defaultService.currentRound(leagueId)

    val details = WebLeagueDetails(leagueName = leagueName,
      leagueId = leagueId,
      form = form,
      divisionLevelsLinks = divisionLevels(leagueId, season),
      seasonInfo = seasonInfo,
      statTypeLinks = StatTypeLinks.withAccumulator(statsTypeFunc, currentRound, statsType),
      sortByLinks = SortByLinks(sortByFunc, StatisticsCHRequest.playerStatsRequest.sortingColumns, sortBy))

    StatisticsCHRequest.playerStatsRequest.execute(leagueId = Some(leagueId), season = Some(season), page = page, statsType = statsType, sortBy = sortBy)
        .map(playerStats => Ok(views.html.league.playerStats(details, WebPagedEntities(playerStats, page, pageFunc))))
  }

  def teamState(leagueId: Int, season: Int, page: Int, statsTypeOpt: Option[StatsType], sortBy: String) = Action.async { implicit request =>
    val currentRound = defaultService.currentRound(leagueId)

    val statsType = statsTypeOpt.getOrElse(Round(currentRound))

    val leagueName = defaultService.leagueIdToCountryNameMap(leagueId).getEnglishName

    val seasonFunction: Int => String = s => routes.LeagueController.teamState(leagueId, s, 0).url
    val seasonInfo = SeasonInfo(season, defaultService.seasonsWithLinks(leagueId, seasonFunction))

    val pageFunc: Int => String = p => routes.LeagueController.teamState(leagueId, season, p, Some(statsType), sortBy).url
    val statsTypeFunc: StatsType => String = st => routes.LeagueController.teamState(leagueId, season, page, Some(st), sortBy).url
    val sortByFunc: String => String = sb => routes.LeagueController.teamState(leagueId, season, page, Some(statsType), sb).url


    val details = WebLeagueDetails(leagueName = leagueName,
      leagueId = leagueId,
      form = form,
      divisionLevelsLinks = divisionLevels(leagueId, season),
      seasonInfo = seasonInfo,
      statTypeLinks = StatTypeLinks.onlyRounds(statsTypeFunc, currentRound, statsType),
      sortByLinks = SortByLinks(sortByFunc, StatisticsCHRequest.teamStateRequest.sortingColumns, sortBy))

    StatisticsCHRequest.teamStateRequest.execute(leagueId = Some(leagueId),
      season = Some(season),
      page = page,
      statsType = statsType,
      sortBy = sortBy)
    .map(teamStates => Ok(views.html.league.teamState(details, WebPagedEntities(teamStates, page, pageFunc))))
  }


  private def divisionLevels(leagueId: Int, season: Int): Seq[(String, String)] = {
    val maxLevels = defaultService.leagueIdToCountryNameMap(leagueId).getNumberOfLevels
    (1 to maxLevels)
      .map(i => Romans(i) -> routes.DivisionLevelController.bestTeams(leagueId, season, i).url )
  }
}


