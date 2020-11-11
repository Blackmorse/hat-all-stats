package service

import databases.RestClickhouseDAO
import databases.requests.overview.{FormationsOverviewRequest, NumberOverviewRequest, OverviewMatchAveragesRequest, OverviewTeamPlayerAveragesRequest, SurprisingMatchesOverviewRequest, TopHatstatsTeamOverviewRequest, TopMatchesOverviewRequest, TopRatingPlayerOverviewRequest, TopSalaryPlayerOverviewRequest, TopSalaryTeamOverviewRequest}
import databases.requests.overview.model.{AveragesOverview, FormationsOverview, MatchTopHatstatsOverview, NumberOverview, PlayerStatOverview, TeamStatOverview, TotalOverview}
import javax.inject.{Inject, Singleton}
import play.api.cache.AsyncCacheApi

import scala.concurrent.duration._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global


@Singleton
class RestOverviewStatsService @Inject()
            (implicit val restClickhouseDAO: RestClickhouseDAO,
             cache: AsyncCacheApi) {

  private def cacheName(name: String, season: Int, round: Int,
                        leagueId: Option[Int], divisionLevel: Option[Int]) =
    s"$name.season=$season.round=$round.league=${leagueId.map(_.toString).getOrElse("world")}${divisionLevel.map(level => s"divisionLevel=$level").getOrElse("")}"

  def numberOverview(season: Int, round: Int,
                     leagueId: Option[Int], divisionLevel: Option[Int]): Future[NumberOverview] = {
    val name = cacheName("numberOverview", season, round, leagueId, divisionLevel)
    cache.getOrElseUpdate(name, 28 days)(NumberOverviewRequest.execute(season, round, leagueId, divisionLevel).map(_.head))
  }

  def formations(season: Int, round: Int,
                 leagueId: Option[Int], divisionLevel: Option[Int]): Future[List[FormationsOverview]] = {
    val name = cacheName("formations", season, round, leagueId, divisionLevel)
    cache.getOrElseUpdate(name, 28 days)(FormationsOverviewRequest.execute(season, round, leagueId, divisionLevel))
  }

  def averageOverview(season: Int, round: Int,
                      leagueId: Option[Int], divisionLevel: Option[Int]): Future[AveragesOverview] = {
    val name = cacheName("averageOverview", season, round, leagueId, divisionLevel)
    cache.getOrElseUpdate(name, 28 days){
      OverviewMatchAveragesRequest.execute(season, round, leagueId, divisionLevel)
        .zipWith(OverviewTeamPlayerAveragesRequest.execute(season, round, leagueId, divisionLevel))
        {case(matchAverages, teamPlayerAverages) => AveragesOverview(matchAverages.head, teamPlayerAverages.head)}
    }
  }

  def surprisingMatches(season: Int, round: Int,
                        leagueId: Option[Int], divisionLevel: Option[Int]): Future[List[MatchTopHatstatsOverview]] = {
    val name = cacheName("surprisingMatches", season, round, leagueId, divisionLevel)
    cache.getOrElseUpdate(name, 28 days)(SurprisingMatchesOverviewRequest.execute(season, round, leagueId, divisionLevel))
  }

  def topHatstatsTeams(season: Int, round: Int,
                       leagueId: Option[Int], divisionLevel: Option[Int]): Future[List[TeamStatOverview]] = {
    val name = cacheName("topHatstatsTeams", season, round, leagueId, divisionLevel)
    cache.getOrElseUpdate(name, 28 days)(TopHatstatsTeamOverviewRequest.execute(season, round, leagueId, divisionLevel))
  }

  def topSalaryTeams(season: Int, round: Int,
                     leagueId: Option[Int], divisionLevel: Option[Int]): Future[List[TeamStatOverview]] = {
    val name = cacheName("topSalaryTeams", season, round, leagueId, divisionLevel)
    cache.getOrElseUpdate(name, 28 days)(TopSalaryTeamOverviewRequest.execute(season, round, leagueId, divisionLevel))
  }

  def topMatches(season: Int, round: Int,
                 leagueId: Option[Int], divisionLevel: Option[Int]): Future[List[MatchTopHatstatsOverview]] = {
    val name = cacheName("topMatches", season, round, leagueId, divisionLevel)
    cache.getOrElseUpdate(name, 28 days)(TopMatchesOverviewRequest.execute(season, round, leagueId, divisionLevel))
  }

  def topSalaryPlayers(season: Int, round: Int,
                       leagueId: Option[Int], divisionLevel: Option[Int]): Future[List[PlayerStatOverview]] = {
    val name = cacheName("topSalaryPlayers", season, round, leagueId, divisionLevel)
    cache.getOrElseUpdate(name, 28 days)(TopSalaryPlayerOverviewRequest.execute(season, round, leagueId, divisionLevel))
  }

  def topRatingPlayers(season: Int, round: Int,
                       leagueId: Option[Int], divisionLevel: Option[Int]): Future[List[PlayerStatOverview]] = {
    val name = cacheName("topRatingPlayers", season, round, leagueId, divisionLevel)
    cache.getOrElseUpdate(name, 28 days)(TopRatingPlayerOverviewRequest.execute(season, round, leagueId, divisionLevel))
  }

  def totalOverview(season: Int, round: Int,
                    leagueId: Option[Int], divisionLevel: Option[Int]): Future[TotalOverview] = {
    for(numberOverviewData <- numberOverview(season, round, leagueId, divisionLevel);
        formationsData <- formations(season, round, leagueId, divisionLevel);
        averageOverviewData <- averageOverview(season, round, leagueId, divisionLevel);
        surprisingMatchesData <- surprisingMatches(season, round, leagueId, divisionLevel);
        topHatstatsTeamsData <- topHatstatsTeams(season, round, leagueId, divisionLevel);
        topSalaryTeamsData <- topSalaryTeams(season, round, leagueId, divisionLevel);
        topMatchesData <- topMatches(season, round, leagueId, divisionLevel);
        topSalaryPlayersData <- topSalaryPlayers(season, round, leagueId, divisionLevel);
        topRatingPlayersData <- topRatingPlayers(season, round, leagueId, divisionLevel)) yield

      TotalOverview(numberOverview = numberOverviewData,
        formations = formationsData,
        averageOverview = averageOverviewData,
        surprisingMatches = surprisingMatchesData,
        topHatstatsTeams = topHatstatsTeamsData,
        topSalaryTeams = topSalaryTeamsData,
        topMatches = topMatchesData,
        topSalaryPlayers = topSalaryPlayersData,
        topRatingPlayers = topRatingPlayersData)
  }


}