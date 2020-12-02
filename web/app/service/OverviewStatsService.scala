package service

import databases.OverviewClickhouseDAO
import javax.inject.{Inject, Singleton}
import models.clickhouse.overview.{AvgMatchDetailsModel, AvgTeamPlayersStats, FormationsModel, MatchOverviewModel, PlayerOverviewModel, TeamOverviewModel}
import play.api.cache.AsyncCacheApi
import service.leagueinfo.LeagueInfoService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class SummaryOverview(numberOfTeams: Int, numberOfPlayers: Int, scoredGoals: Int,
                           injuriedPlayers: Int, yellowCards: Int, redCards: Int)

case class AverageOverview(averageMatchDetails: AvgMatchDetailsModel, averageTeamPlayers: AvgTeamPlayersStats)

case class OverviewStatistics(summaryOverview: SummaryOverview,
                              averageOverview: AverageOverview,
                              formations: List[FormationsModel],
                              topSalaryTeams: List[TeamOverviewModel],
                              topHatstatsTeams: List[TeamOverviewModel],
                              topHatstatsMatches: List[MatchOverviewModel],
                              topRandomMatches: List[MatchOverviewModel],
                              topSalaryPlayers: List[PlayerOverviewModel],
                              topRatingPlayers: List[PlayerOverviewModel])

@Singleton
class OverviewStatsService @Inject()(leagueInfoService: LeagueInfoService,
                                     overviewClickhouseDAO: OverviewClickhouseDAO,
                                     cache: AsyncCacheApi) {

  def overviewStatistics(leagueId: Option[Int] = None): Future[OverviewStatistics] = {
    val cacheName = leagueId.map(leagueId => s"overview.$leagueId").getOrElse("overview.world")
    cache.getOrElseUpdate[OverviewStatistics](cacheName)(fetchOverviewStatistics(leagueId))
  }


  private def fetchOverviewStatistics(leagueId: Option[Int]) = Future {
    val (currentRound, currentSeason) = leagueId
      .map(leagueId => (leagueInfoService.leagueInfo.currentRound(leagueId), leagueInfoService.leagueInfo.currentSeason(leagueId)))
      .getOrElse(leagueInfoService.lastFullRound(), leagueInfoService.lastFullSeason())

    val numberOfTeams = overviewClickhouseDAO.numberOfTeams(currentRound, currentSeason, leagueId)
    val overviewPlayerState = overviewClickhouseDAO.overviewPlayerState(currentRound, currentSeason, leagueId)

    val numberOverview = SummaryOverview(numberOfTeams.count, overviewPlayerState.count,
      overviewPlayerState.goals, overviewPlayerState.injuried, overviewPlayerState.yellowCards, overviewPlayerState.redCards)

    val avgMatchDetails = overviewClickhouseDAO.avgMatchDetails(currentRound, currentSeason, leagueId)
    val avgTeamPlayers = overviewClickhouseDAO.avgTeamPlayers(currentRound, currentSeason, leagueId)

    val averageOverview = AverageOverview(avgMatchDetails, avgTeamPlayers)

    val formations = overviewClickhouseDAO.formations(currentRound, currentSeason, leagueId)

    val topSalaryTeams = overviewClickhouseDAO.topSalaryTeams(currentRound, currentSeason, leagueId)
    val topHatstatsTeams = overviewClickhouseDAO.topHatstatsTeams(currentRound, currentSeason, leagueId)

    val topHatstatsMatches = overviewClickhouseDAO.topHatstatsMatches(currentRound, currentSeason, leagueId)
    val topRandomMatches = overviewClickhouseDAO.topRandomMatches(currentRound, currentSeason, leagueId)

    val topSalaryPlayers = overviewClickhouseDAO.topSalaryPlayers(currentRound, currentSeason, leagueId)
    val topRatingPlayers = overviewClickhouseDAO.topRatingPlayers(currentRound, currentSeason, leagueId)

    OverviewStatistics(numberOverview,
      averageOverview,
      formations,
      topSalaryTeams,
      topHatstatsTeams,
      topHatstatsMatches,
      topRandomMatches,
      topSalaryPlayers,
      topRatingPlayers)

  }
}
