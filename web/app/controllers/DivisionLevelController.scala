package controllers

import databases.ClickhouseDAO
import databases.clickhouse.StatisticsCHRequest
import hattrick.Hattrick
import javax.inject.{Inject, Singleton}
import models.web._
import play.api.mvc.{BaseController, ControllerComponents}
import service.DefaultService
import utils.Romans

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class WebDivisionLevelDetails(leagueName: String, leagueId: Int, seasonInfo: SeasonInfo, divisionLevel: Int, divisionLevelRoman: String,
                                   leagueUnitLinks: Seq[(String, String)],
                                   statTypeLinks: StatTypeLinks,
                                   sortByLinks: SortByLinks) extends AbstractWebDetails

@Singleton
class DivisionLevelController@Inject() (val controllerComponents: ControllerComponents,
                                        implicit val clickhouseDAO: ClickhouseDAO,
                                        val defaultService: DefaultService,
                                        val hattrick: Hattrick) extends BaseController {
  def bestTeams(leagueId: Int, season: Int, divisionLevel: Int, page: Int, statsType: StatsType, sortBy: String) = Action.async{ implicit request =>
    val leagueName = defaultService.leagueIdToCountryNameMap(leagueId).getEnglishName

    val seasonFunction: Int => String = s => routes.DivisionLevelController.bestTeams(leagueId, s, divisionLevel, 0).url
    val seasonInfo = SeasonInfo(season, defaultService.seasonsWithLinks(leagueId, seasonFunction))

    val pageUrlFunc: Int => String = p => routes.DivisionLevelController.bestTeams(leagueId, season, divisionLevel, p, statsType, sortBy).url
    val statsTypeFunc: StatsType => String = st => routes.DivisionLevelController.bestTeams(leagueId, season,divisionLevel, page, st, sortBy).url
    val sortByFunc: String => String = sb => routes.DivisionLevelController.bestTeams(leagueId, season, divisionLevel, page, statsType, sb).url

    val leagueUnitIdFuture = Future(hattrick.api.search().searchLeagueId(leagueId).searchType(3).searchString(Romans(divisionLevel) + "." + "1")
      .execute().getSearchResults.get(0).getResultId)

    val currentRound = defaultService.currentRound(leagueId)

    StatisticsCHRequest.bestHatstatsTeamRequest.execute(leagueId = Some(leagueId), season = Some(season), divisionLevel = Some(divisionLevel), page = page, statsType = statsType, sortBy = sortBy)
      .zipWith(leagueUnitIdFuture){case (bestTeams, leagueUnitId) =>
        val details = WebDivisionLevelDetails(leagueName = leagueName,
          leagueId = leagueId,
          seasonInfo = seasonInfo,
          divisionLevel = divisionLevel,
          divisionLevelRoman = Romans(divisionLevel),
          leagueUnitLinks = leagueUnitNumbers(season, divisionLevel, leagueUnitId),
          statTypeLinks = StatTypeLinks.withAverages(statsTypeFunc, currentRound, statsType),
          sortByLinks = SortByLinks(sortByFunc, StatisticsCHRequest.bestHatstatsTeamRequest.sortingColumns, sortBy))

        Ok(views.html.divisionlevel.bestTeams(details,
          WebPagedEntities(bestTeams, page, pageUrlFunc)))
      }
  }

  def bestLeagueUnits(leagueId: Int, season: Int, divisionLevel: Int, page: Int, statsType: StatsType, sortBy: String) = Action.async{  implicit request =>
    val leagueName = defaultService.leagueIdToCountryNameMap(leagueId).getEnglishName

    val seasonFunction: Int => String = s => routes.DivisionLevelController.bestLeagueUnits(leagueId, s, divisionLevel, 0).url
    val seasonInfo = SeasonInfo(season, defaultService.seasonsWithLinks(leagueId, seasonFunction))

    val pageUrlFunc: Int => String = p => routes.DivisionLevelController.bestLeagueUnits(leagueId, season, divisionLevel, p, statsType, sortBy).url
    val statsTypeFunc: StatsType => String = st => routes.DivisionLevelController.bestLeagueUnits(leagueId, season, divisionLevel, page, st, sortBy).url
    val sortByFunc: String => String = sb => routes.DivisionLevelController.bestLeagueUnits(leagueId, season, divisionLevel, page, statsType, sb).url

    val leagueUnitIdFuture = Future(hattrick.api.search().searchLeagueId(leagueId).searchType(3).searchString(Romans(divisionLevel) + "." + "1")
      .execute().getSearchResults.get(0).getResultId)

    val currentRound = defaultService.currentRound(leagueId)

    StatisticsCHRequest.bestHatstatsLeagueRequest.execute(leagueId = Some(leagueId), season = Some(season), divisionLevel = Some(divisionLevel), page = page, statsType = statsType, sortBy = sortBy)
      .zipWith(leagueUnitIdFuture){case (bestLeagueUnits, leagueUnitId) =>

        val details = WebDivisionLevelDetails(leagueName = leagueName, leagueId = leagueId,
          seasonInfo = seasonInfo,
          divisionLevel = divisionLevel,
          divisionLevelRoman = Romans(divisionLevel),
          leagueUnitLinks = leagueUnitNumbers(season, divisionLevel, leagueUnitId),
          statTypeLinks = StatTypeLinks.withAverages(statsTypeFunc, currentRound, statsType),
          sortByLinks = SortByLinks(sortByFunc, StatisticsCHRequest.bestHatstatsLeagueRequest.sortingColumns, sortBy))

        Ok(views.html.divisionlevel.bestLeagueUnits(details,
          WebPagedEntities(bestLeagueUnits, page, pageUrlFunc)))
      }
  }

  def playerStats(leagueId: Int, season: Int, divisionLevel: Int, page: Int, statsType: StatsType, sortBy: String) = Action.async { implicit request =>
    val leagueName = defaultService.leagueIdToCountryNameMap(leagueId).getEnglishName

    val seasonFunction: Int => String = s => routes.DivisionLevelController.playerStats(leagueId, s, divisionLevel, 0).url
    val seasonInfo = SeasonInfo(season, defaultService.seasonsWithLinks(leagueId, seasonFunction))

    val pageUrlFunc: Int => String = p => routes.DivisionLevelController.playerStats(leagueId, season, divisionLevel, p, statsType, sortBy).url
    val statsTypeFunc: StatsType => String = st => routes.DivisionLevelController.playerStats(leagueId, season, divisionLevel, page, st, sortBy).url
    val sortByFunc: String => String = sb => routes.DivisionLevelController.playerStats(leagueId, season, divisionLevel, page, statsType, sb).url

    val leagueUnitIdFuture = Future(hattrick.api.search().searchLeagueId(leagueId).searchType(3).searchString(Romans(divisionLevel) + "." + "1")
      .execute().getSearchResults.get(0).getResultId)

    val currentRound = defaultService.currentRound(leagueId)

    StatisticsCHRequest.playerStatsRequest.execute(leagueId = Some(leagueId), season = Some(season), divisionLevel = Some(divisionLevel), page = page, statsType = statsType, sortBy = sortBy)
      .zipWith(leagueUnitIdFuture){case (playerStats, leagueUnitId) =>

        val details = WebDivisionLevelDetails(leagueName = leagueName, leagueId = leagueId,
          seasonInfo = seasonInfo,
          divisionLevel = divisionLevel,
          divisionLevelRoman = Romans(divisionLevel),
          leagueUnitLinks = leagueUnitNumbers(season, divisionLevel, leagueUnitId),
          statTypeLinks = StatTypeLinks.withoutAverages(statsTypeFunc, currentRound, statsType),
          sortByLinks = SortByLinks(sortByFunc, StatisticsCHRequest.playerStatsRequest.sortingColumns, sortBy))

        Ok(views.html.divisionlevel.playerStats(details,
          WebPagedEntities(playerStats, page, pageUrlFunc)))
      }
  }

  def leagueUnitNumbers(season: Int, divisionLevel: Int, baseLeagueUnitId: Long): Seq[(String, String)] =
    defaultService.leagueNumbersMap(divisionLevel).map(number =>
      number.toString -> routes.LeagueUnitController.bestTeams(baseLeagueUnitId + number - 1, season, 0).url)
}
