package service

import databases.OverviewClickhouseDAO
import javax.inject.{Inject, Singleton}
import models.clickhouse.overview.{AvgMatchDetailsModel, AvgTeamPlayersStats, FormationsModel, MatchOverviewModel, PlayerOverviewModel, TeamOverviewModel}
import play.api.cache.AsyncCacheApi

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

  def overviewStatistics(): Future[OverviewStatistics] =
    cache.getOrElseUpdate[OverviewStatistics]("overview.world") (fetchOverviewStatistics())

  private def fetchOverviewStatistics() = Future {
    val currentRound = leagueInfoService.lastFullRound()
    val currentSeason = leagueInfoService.lastFullSeason()

    val numberOfTeams = overviewClickhouseDAO.numberOfTeams(currentRound, currentSeason)
    val overviewPlayerState = overviewClickhouseDAO.overviewPlayerState(currentRound, currentSeason)

    val numberOverview = SummaryOverview(numberOfTeams.count, overviewPlayerState.count,
      overviewPlayerState.goals, overviewPlayerState.injuried, overviewPlayerState.yellowCards, overviewPlayerState.redCards)

    val avgMatchDetails = overviewClickhouseDAO.avgMatchDetails(currentRound, currentSeason)
    val avgTeamPlayers = overviewClickhouseDAO.avgTeamPlayers(currentRound, currentSeason)

    val averageOverview = AverageOverview(avgMatchDetails, avgTeamPlayers)

    val formations = overviewClickhouseDAO.formations(currentRound, currentSeason)

    val topSalaryTeams = overviewClickhouseDAO.topSalaryTeams(currentRound, currentSeason)
    val topHatstatsTeams = overviewClickhouseDAO.topHatstatsTeams(currentRound, currentSeason)

    val topHatstatsMatches = overviewClickhouseDAO.topHatstatsMatches(currentRound, currentSeason)
    val topRandomMatches = overviewClickhouseDAO.topRandomMatches(currentRound, currentSeason)

    val topSalaryPlayers = overviewClickhouseDAO.topSalaryPlayers(currentRound, currentSeason)
    val topRatingPlayers = overviewClickhouseDAO.topRatingPlayers(currentRound, currentSeason)

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
