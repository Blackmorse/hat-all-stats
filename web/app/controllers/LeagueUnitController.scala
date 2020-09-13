package controllers

import com.blackmorse.hattrick.model.enums.SearchType
import databases.ClickhouseDAO
import databases.clickhouse.{Accumulated, AvgMax, OnlyRound, StatisticsCHRequest, StatisticsType}
import hattrick.Hattrick
import javax.inject.{Inject, Singleton}
import models.clickhouse.{BestMatch, FanclubFlags, PlayerStats, PlayersState, PowerRating, StreakTrophy, SurprisingMatch, TeamRating, TeamState}
import models.web._
import play.api.i18n.Messages
import play.api.mvc.{BaseController, Call, ControllerComponents}
import service.{DefaultService, LeagueInfo, LeagueInfoService, LeagueUnitCalculatorService, LeagueUnitTeamStat}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class WebLeagueUnitDetails(leagueInfo: LeagueInfo, currentRound: Int, divisionLevel: Int,
                                leagueUnitName: String, leagueUnitId: Long,
                                teamLinks: Seq[(String, String)])
    extends AbstractWebDetails

@Singleton
class LeagueUnitController @Inject()(val controllerComponents: ControllerComponents,
                                     val hattrick: Hattrick,
                                     val leagueInfoService: LeagueInfoService,
                                     val defaultService: DefaultService,
                                     implicit val clickhouseDAO: ClickhouseDAO,
                                     val leagueUnitCalculatorService: LeagueUnitCalculatorService,
                                     val viewDataFactory: ViewDataFactory) extends BaseController with MessageSupport {

  def stats[T](leagueUnitId: Long,
            statisticsParametersOpt: Option[StatisticsParameters],
            sortColumn: String,
            statisticsType: StatisticsType,
            func: StatisticsParameters => Call,
            statisticsCHRequest: StatisticsCHRequest[T],
            viewFunc: (ViewData[T, WebLeagueUnitDetails], Seq[LeagueUnitTeamStat]) => Messages => play.twirl.api.HtmlFormat.Appendable) = Action.async { implicit request =>

    val leagueDetailsFuture = Future(hattrick.api.leagueDetails().leagueLevelUnitId(leagueUnitId).execute())

    leagueDetailsFuture.flatMap( leagueDetails => {
      val statsType = statisticsType match {
        case AvgMax => Avg
        case Accumulated => Accumulate
        case OnlyRound =>
          val currentRound = leagueInfoService.leagueInfo.currentRound(leagueDetails.getLeagueId)
          Round(currentRound)
      }

      val (statisticsParameters, cookies) = defaultService.statisticsParameters(statisticsParametersOpt,
        leagueId = leagueDetails.getLeagueId(),
        statsType = statsType,
        sortColumn = sortColumn)

      val leagueFixture = hattrick.api.leagueFixtures().season(leagueInfoService.getAbsoluteSeasonFromRelative(statisticsParameters.season, leagueDetails.getLeagueId))
        .leagueLevelUnitId(leagueUnitId).execute()

      val tillRound = statisticsParameters.statsType match {
        case Round(round) => Some(round)
        case _ => None
      }
      val leagueUnitTeamStats = leagueUnitCalculatorService.calculate(leagueFixture, tillRound)

      val details = WebLeagueUnitDetails(leagueInfo = leagueInfoService.leagueInfo(leagueDetails.getLeagueId),
        currentRound = leagueInfoService.leagueInfo.currentRound(leagueDetails.getLeagueId),
        divisionLevel = leagueDetails.getLeagueLevel,
        leagueUnitName = leagueDetails.getLeagueLevelUnitName,
        leagueUnitId = leagueDetails.getLeagueLevelUnitId,
        teamLinks = teamLinks(leagueUnitTeamStats))

      statisticsCHRequest.execute(leagueId = Some(leagueDetails.getLeagueId),
        divisionLevel = Some(leagueDetails.getLeagueLevel),
        leagueUnitId = Some(leagueDetails.getLeagueLevelUnitId),
        statisticsParameters = statisticsParameters)
        .map(entities => {
          val viewData = viewDataFactory.create(details = details,
            func = func,
            statisticsType = statisticsType,
            statisticsParameters = statisticsParameters,
            statisticsCHRequest =  statisticsCHRequest,
            entities = entities)

          Ok(viewFunc(viewData, leagueUnitTeamStats).apply(messages)).withCookies(cookies: _*)
        })
    })
  }

  def bestTeamsByName(leagueUnitName: String, leagueId: Int, statisticsParametersOpt: Option[StatisticsParameters]) = {
    val search = hattrick.api.search().searchType(SearchType.SERIES).searchLeagueId(leagueId).searchString(leagueUnitName).execute()
    val leagueUnitId = search.getSearchResults.get(0).getResultId

    bestTeams(leagueUnitId, statisticsParametersOpt)
  }

  def bestTeams(leagueUnitId: Long, statisticsParametersOpt: Option[StatisticsParameters]) =
    stats(leagueUnitId = leagueUnitId,
      statisticsParametersOpt = statisticsParametersOpt,
      sortColumn = "hatstats",
      statisticsType = AvgMax,
      func = sp => routes.LeagueUnitController.bestTeams(leagueUnitId, Some(sp)),
      statisticsCHRequest = StatisticsCHRequest.bestHatstatsTeamRequest,
      viewFunc = {(viewData: ViewData[TeamRating, WebLeagueUnitDetails], leagueUnitTeamStats: Seq[LeagueUnitTeamStat]) => messages => views.html.leagueunit.bestTeams(viewData, leagueUnitTeamStats)(messages)})


  def playerStats(leagueUnitId: Long, statisticsParametersOpt: Option[StatisticsParameters]) =
    stats(leagueUnitId = leagueUnitId,
      statisticsParametersOpt = statisticsParametersOpt,
      sortColumn = "scored",
      statisticsType = Accumulated,
      func = sp => routes.LeagueUnitController.playerStats(leagueUnitId, Some(sp)),
      statisticsCHRequest = StatisticsCHRequest.playerStatsRequest,
      viewFunc = {(viewData: ViewData[PlayerStats, WebLeagueUnitDetails], _: Seq[LeagueUnitTeamStat])  => messages => views.html.leagueunit.playerStats(viewData)(messages)})

  def teamState(leagueUnitId: Long, statisticsParametersOpt: Option[StatisticsParameters]) =
    stats(leagueUnitId = leagueUnitId,
      statisticsParametersOpt = statisticsParametersOpt,
      sortColumn = "rating",
      statisticsType = OnlyRound,
      func = sp => routes.LeagueUnitController.teamState(leagueUnitId, Some(sp)),
      statisticsCHRequest = StatisticsCHRequest.teamStateRequest,
      viewFunc = {(viewData: ViewData[TeamState, WebLeagueUnitDetails], _: Seq[LeagueUnitTeamStat]) => messages => views.html.leagueunit.teamState(viewData)(messages)})


  def playerState(leagueUnitId: Long, statisticsParametersOpt: Option[StatisticsParameters]) =
    stats(leagueUnitId = leagueUnitId,
      statisticsParametersOpt = statisticsParametersOpt,
      sortColumn = "rating",
      statisticsType = OnlyRound,
      func = sp => routes.LeagueUnitController.playerState(leagueUnitId, Some(sp)),
      statisticsCHRequest = StatisticsCHRequest.playerStateRequest,
      viewFunc = {(viewData: ViewData[PlayersState, WebLeagueUnitDetails], _: Seq[LeagueUnitTeamStat]) => messages => views.html.leagueunit.playerState(viewData)(messages)})

  def fanclubFlags(leagueUnitId: Long, statisticsParametersOpt: Option[StatisticsParameters]) =
    stats(leagueUnitId = leagueUnitId,
      statisticsParametersOpt = statisticsParametersOpt,
      sortColumn = "fanclub_size",
      statisticsType = OnlyRound,
      func = sp => routes.LeagueUnitController.fanclubFlags(leagueUnitId, Some(sp)),
      statisticsCHRequest = StatisticsCHRequest.fanclubFlagsRequest,
      viewFunc = {(viewData: ViewData[FanclubFlags, WebLeagueUnitDetails], _: Seq[LeagueUnitTeamStat]) => messages => views.html.leagueunit.fanclubFlags(viewData)(messages)}
    )

  def streakTrophies(leagueUnitId: Long, statisticsParametersOpt: Option[StatisticsParameters]) =
    stats(leagueUnitId = leagueUnitId,
      statisticsParametersOpt = statisticsParametersOpt,
      sortColumn = "trophies_number",
      statisticsType = OnlyRound,
      func = sp => routes.LeagueUnitController.streakTrophies(leagueUnitId, Some(sp)),
      statisticsCHRequest = StatisticsCHRequest.streakTrophyRequest,
      viewFunc = {(viewData: ViewData[StreakTrophy, WebLeagueUnitDetails], _: Seq[LeagueUnitTeamStat]) => messages => views.html.leagueunit.streakTrophies(viewData)(messages)}
    )

  def powerRatings(leagueUnitId: Long, statisticsParametersOpt: Option[StatisticsParameters]) =
    stats(leagueUnitId = leagueUnitId,
      statisticsParametersOpt = statisticsParametersOpt,
      sortColumn = "power_rating",
      statisticsType = OnlyRound,
      func = sp => routes.LeagueUnitController.powerRatings(leagueUnitId, Some(sp)),
      statisticsCHRequest = StatisticsCHRequest.powerRatingRequest,
      viewFunc = {(viewData: ViewData[PowerRating, WebLeagueUnitDetails], _: Seq[LeagueUnitTeamStat]) => messages => views.html.leagueunit.powerRatings(viewData)(messages)})

  def bestMatches(leagueUnitId: Long, statisticsParametersOpt: Option[StatisticsParameters]) =
    stats(leagueUnitId = leagueUnitId,
      statisticsParametersOpt = statisticsParametersOpt,
      sortColumn = "sum_hatstats",
      statisticsType = Accumulated,
      func = sp => routes.LeagueUnitController.bestMatches(leagueUnitId, Some(sp)),
      statisticsCHRequest = StatisticsCHRequest.bestMatchesRequest,
      viewFunc = {(viewData: ViewData[BestMatch, WebLeagueUnitDetails], _: Seq[LeagueUnitTeamStat]) => messages => views.html.leagueunit.bestMatches(viewData)(messages)}
    )

  def surprisingMatches(leagueUnitId: Long, statisticsParametersOpt: Option[StatisticsParameters]) =
    stats(leagueUnitId = leagueUnitId,
      statisticsParametersOpt = statisticsParametersOpt,
      sortColumn = "abs_hatstats_difference",
      statisticsType = Accumulated,
      func = sp => routes.LeagueUnitController.surprisingMatches(leagueUnitId, Some(sp)),
      statisticsCHRequest = StatisticsCHRequest.surprisingMatchesRequest,
      viewFunc = {(viewData: ViewData[SurprisingMatch, WebLeagueUnitDetails], _: Seq[LeagueUnitTeamStat]) => messages => views.html.leagueunit.surprisingMatches(viewData)(messages)}
    )

  private def teamLinks(leagueTeamStats: Seq[LeagueUnitTeamStat]): Seq[(String, String)] = {
    leagueTeamStats.map(stat => stat.teamName -> routes.TeamController.teamOverview(stat.teamId).url)
  }
}
