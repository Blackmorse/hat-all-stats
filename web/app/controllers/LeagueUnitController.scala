package controllers

import databases.ClickhouseDAO
import databases.clickhouse.StatisticsCHRequest
import hattrick.Hattrick
import javax.inject.{Inject, Singleton}
import models.web._
import play.api.mvc.{BaseController, ControllerComponents}
import service.{DefaultService, LeagueUnitCalculatorService, LeagueUnitTeamStat}
import utils.LeagueNameParser

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class WebLeagueUnitDetails(leagueName: String, leagueId: Int, seasonInfo: SeasonInfo, divisionLevel: Int,
                                leagueUnitNumber: Int, leagueUnitName: String, leagueUnitId: Long,
                                teamLinks: Seq[(String, String)], statTypeLinks: StatTypeLinks,
                                sortByLinks: SortByLinks)
    extends AbstractWebDetails

@Singleton
class LeagueUnitController @Inject()(val controllerComponents: ControllerComponents,
                                     val hattrick: Hattrick,
                                     val defaultService: DefaultService,
                                     implicit val clickhouseDAO: ClickhouseDAO,
                                     val leagueUnitCalculatorService: LeagueUnitCalculatorService) extends BaseController {

  def bestTeams(leagueUnitId: Long, season: Int, page: Int, statsType: StatsType, sortBy: String) = Action.async{
    val leagueDetailsFuture = Future(hattrick.api.leagueDetails().leagueLevelUnitId(leagueUnitId).execute())

    leagueDetailsFuture.flatMap ( leagueDetails => {
      val leagueFixture = hattrick.api.leagueFixtures().season(defaultService.seasonForLeagueId(season, leagueDetails.getLeagueId))
        .leagueLevelUnitId(leagueUnitId).execute()

      val leagueName = defaultService.leagueIdToCountryNameMap(leagueDetails.getLeagueId).getEnglishName
      val leagueUnitNumber = LeagueNameParser.getLeagueUnitNumberByName(leagueDetails.getLeagueLevelUnitName)

      val seasonInfoFunc: Int => String = s => routes.LeagueUnitController.bestTeams(leagueUnitId, s, 0).url
      val seasonInfo = SeasonInfo(season, defaultService.seasonsWithLinks(leagueDetails.getLeagueId, seasonInfoFunc))

      val tillRound = statsType match {
        case Round(round) => Some(round)
        case _ => None
      }

      val leagueUnitTeamStats = leagueUnitCalculatorService.calculate(leagueFixture, tillRound)

      val pageUrlFunc: Int => String = p => routes.LeagueUnitController.bestTeams(leagueDetails.getLeagueLevelUnitId, season, p, statsType, sortBy).url
      val statsTypeFunc: StatsType => String = st => routes.LeagueUnitController.bestTeams(leagueDetails.getLeagueLevelUnitId, season, page, st, sortBy).url
      val sortByFunc: String => String = sb => routes.LeagueUnitController.bestTeams(leagueDetails.getLeagueLevelUnitId, season, page, statsType, sb).url

      val currentRound = defaultService.currentRound(leagueDetails.getLeagueId)

      val details = WebLeagueUnitDetails(leagueName = leagueName,
        leagueId = leagueDetails.getLeagueId,
        seasonInfo = seasonInfo,
        divisionLevel = leagueDetails.getLeagueLevel,
        leagueUnitNumber = leagueUnitNumber,
        leagueUnitName = leagueDetails.getLeagueLevelUnitName,
        leagueUnitId = leagueDetails.getLeagueLevelUnitId,
        teamLinks = teamLinks(leagueUnitTeamStats),
        statTypeLinks = StatTypeLinks.withAverages(statsTypeFunc, currentRound, statsType),
        sortByLinks = SortByLinks(sortByFunc, StatisticsCHRequest.bestHatstatsTeamRequest.sortingColumns, sortBy))

      StatisticsCHRequest.bestHatstatsTeamRequest.execute(leagueId = Some(leagueDetails.getLeagueId),
        season = Some(season), divisionLevel = Some(leagueDetails.getLeagueLevel),
        leagueUnitId = Some(leagueDetails.getLeagueLevelUnitId), page = page, statsType = statsType, sortBy = sortBy)
        .map(bestTeams =>
          Ok(views.html.leagueunit.bestTeams(details, leagueUnitTeamStats,
                        WebPagedEntities(bestTeams, page, pageUrlFunc))))
    } )
  }

  def playerStats(leagueUnitId: Long, season: Int, page: Int, statsType: StatsType, sortBy: String) = Action.async {implicit  request =>
    val leagueDetailsFuture = Future(hattrick.api.leagueDetails().leagueLevelUnitId(leagueUnitId).execute())

    leagueDetailsFuture.flatMap ( leagueDetails => {
      val leagueFixture = hattrick.api.leagueFixtures().season(defaultService.seasonForLeagueId(season, leagueDetails.getLeagueId))
        .leagueLevelUnitId(leagueUnitId).execute()

      val leagueName = defaultService.leagueIdToCountryNameMap(leagueDetails.getLeagueId).getEnglishName
      val leagueUnitNumber = LeagueNameParser.getLeagueUnitNumberByName(leagueDetails.getLeagueLevelUnitName)

      val seasonInfoFunc: Int => String = s => routes.LeagueUnitController.playerStats(leagueUnitId, s, 0).url
      val seasonInfo = SeasonInfo(season, defaultService.seasonsWithLinks(leagueDetails.getLeagueId, seasonInfoFunc))

      val pageUrlFunc: Int => String = p => routes.LeagueUnitController.playerStats(leagueDetails.getLeagueLevelUnitId, season, p, statsType, sortBy).url
      val statsTypeFunc: StatsType => String = st => routes.LeagueUnitController.playerStats(leagueDetails.getLeagueLevelUnitId, season, page, st, sortBy).url
      val sortByFunc: String => String = sb => routes.LeagueUnitController.playerStats(leagueDetails.getLeagueLevelUnitId, season, page, statsType, sb).url

      val currentRound = defaultService.currentRound(leagueDetails.getLeagueId)

      val tillRound = statsType match {
        case Round(round) => Some(round)
        case _ => None
      }

      val leagueUnitTeamStats = leagueUnitCalculatorService.calculate(leagueFixture, tillRound)

      val details = WebLeagueUnitDetails(leagueName = leagueName,
        leagueId = leagueDetails.getLeagueId,
        seasonInfo = seasonInfo,
        divisionLevel = leagueDetails.getLeagueLevel,
        leagueUnitNumber = leagueUnitNumber,
        leagueUnitName = leagueDetails.getLeagueLevelUnitName,
        leagueUnitId = leagueDetails.getLeagueLevelUnitId,
        teamLinks = teamLinks(leagueUnitTeamStats),
        statTypeLinks = StatTypeLinks.withAccumulator(statsTypeFunc, currentRound, statsType),
        sortByLinks = SortByLinks(sortByFunc, StatisticsCHRequest.playerStatsRequest.sortingColumns, sortBy))


      StatisticsCHRequest.playerStatsRequest.execute(leagueId = Some(leagueDetails.getLeagueId),
        season = Some(season), divisionLevel = Some(leagueDetails.getLeagueLevel),
        leagueUnitId = Some(leagueDetails.getLeagueLevelUnitId), page = page, statsType = statsType, sortBy = sortBy)
        .map(playerStats =>
          Ok(views.html.leagueunit.playerStats(details,
            WebPagedEntities(playerStats, page, pageUrlFunc))))

    })
  }

  def teamState(leagueUnitId: Long, season: Int, page: Int, statsTypeOpt: Option[StatsType], sortBy: String) = Action.async{ implicit request =>
    val leagueDetailsFuture = Future(hattrick.api.leagueDetails().leagueLevelUnitId(leagueUnitId).execute())

    leagueDetailsFuture.flatMap ( leagueDetails => {

      val currentRound = defaultService.currentRound(leagueDetails.getLeagueId)

      val statsType = statsTypeOpt.getOrElse(Round(currentRound))

      val leagueFixture = hattrick.api.leagueFixtures().season(defaultService.seasonForLeagueId(season, leagueDetails.getLeagueId))
        .leagueLevelUnitId(leagueUnitId).execute()

      val leagueName = defaultService.leagueIdToCountryNameMap(leagueDetails.getLeagueId).getEnglishName
      val leagueUnitNumber = LeagueNameParser.getLeagueUnitNumberByName(leagueDetails.getLeagueLevelUnitName)

      val seasonInfoFunc: Int => String = s => routes.LeagueUnitController.teamState(leagueUnitId, s, 0).url
      val seasonInfo = SeasonInfo(season, defaultService.seasonsWithLinks(leagueDetails.getLeagueId, seasonInfoFunc))

      val pageUrlFunc: Int => String = p => routes.LeagueUnitController.teamState(leagueDetails.getLeagueLevelUnitId, season, p, Some(statsType), sortBy).url
      val statsTypeFunc: StatsType => String = st => routes.LeagueUnitController.teamState(leagueDetails.getLeagueLevelUnitId, season, page, Some(st), sortBy).url
      val sortByFunc: String => String = sb => routes.LeagueUnitController.teamState(leagueDetails.getLeagueLevelUnitId, season, page, Some(statsType), sb).url

      val tillRound = statsType match {
        case Round(round) => Some(round)
        case _ => None
      }

      val leagueUnitTeamStats = leagueUnitCalculatorService.calculate(leagueFixture, tillRound)

      StatisticsCHRequest.teamStateRequest.execute(leagueId = Some(leagueDetails.getLeagueId),
        season = Some(season),
        divisionLevel = Some(leagueDetails.getLeagueLevel),
        leagueUnitId = Some(leagueUnitId),
        page = page,
        statsType = statsType,
        sortBy = sortBy)
          .map(teamState => {
            val details = WebLeagueUnitDetails(
              leagueName = leagueName,
              leagueId = leagueDetails.getLeagueId,
              seasonInfo = seasonInfo,
              divisionLevel = leagueDetails.getLeagueLevel,
              leagueUnitNumber = leagueUnitNumber,
              leagueUnitName =  leagueDetails.getLeagueLevelUnitName,
              leagueUnitId = leagueUnitId,
              teamLinks = teamLinks(leagueUnitTeamStats),
              statTypeLinks = StatTypeLinks.onlyRounds(statsTypeFunc, currentRound, statsType),
              sortByLinks = SortByLinks(sortByFunc, StatisticsCHRequest.teamStateRequest.sortingColumns, sortBy)
            )

            Ok(views.html.leagueunit.teamState(details,
              WebPagedEntities(teamState, page, pageUrlFunc)))
          })
    })
  }

  private def teamLinks(leagueTeamStats: Seq[LeagueUnitTeamStat]): Seq[(String, String)] = {
    leagueTeamStats.map(stat => stat.teamName -> routes.TeamController.matches(stat.teamId).url)
  }
}
