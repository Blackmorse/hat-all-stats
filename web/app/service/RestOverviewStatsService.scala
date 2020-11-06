package service

import databases.RestClickhouseDAO
import databases.requests.model.`match`.MatchTopHatstats
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

  private def cacheName(name: String, season: Int, round: Int, leagueId: Option[Int]) =
    s"$name.season=$season.round=$round.league=${leagueId.map(_.toString).getOrElse("world")}"

  def numberOverview(season: Int, round: Int, leagueId: Option[Int]): Future[NumberOverview] = {
    val name = cacheName("numberOverview", season, round, leagueId)
    cache.getOrElseUpdate(name, 28 days)(NumberOverviewRequest.execute(season, round, leagueId).map(_.head))
  }

  def formations(season: Int, round: Int, leagueId: Option[Int]): Future[List[FormationsOverview]] = {
    val name = cacheName("formations", season, round, leagueId)
    cache.getOrElseUpdate(name, 28 days)(FormationsOverviewRequest.execute(season, round, leagueId))
  }

  def averageOverview(season: Int, round: Int, leagueId: Option[Int]): Future[AveragesOverview] = {
    val name = cacheName("averageOverview", season, round, leagueId)
    cache.getOrElseUpdate(name, 28 days){
      OverviewMatchAveragesRequest.execute(season, round, leagueId)
        .zipWith(OverviewTeamPlayerAveragesRequest.execute(season, round, leagueId))
        {case(matchAverages, teamPlayerAverages) => AveragesOverview(matchAverages.head, teamPlayerAverages.head)}
    }
  }

  def surprisingMatches(season: Int, round: Int, leagueId: Option[Int]): Future[List[MatchTopHatstatsOverview]] = {
    val name = cacheName("surprisingMatches", season, round, leagueId)
    cache.getOrElseUpdate(name, 28 days)(SurprisingMatchesOverviewRequest.execute(season, round, leagueId))
  }

  def topHatstatsTeams(season: Int, round: Int, leagueId: Option[Int]): Future[List[TeamStatOverview]] = {
    val name = cacheName("topHatstatsTeams", season, round, leagueId)
    cache.getOrElseUpdate(name, 28 days)(TopHatstatsTeamOverviewRequest.execute(season, round, leagueId))
  }

  def topSalaryTeams(season: Int, round: Int, leagueId: Option[Int]): Future[List[TeamStatOverview]] = {
    val name = cacheName("topSalaryTeams", season, round, leagueId)
    cache.getOrElseUpdate(name, 28 days)(TopSalaryTeamOverviewRequest.execute(season, round, leagueId))
  }

  def topMatches(season: Int, round: Int, leagueId: Option[Int]): Future[List[MatchTopHatstatsOverview]] = {
    val name = cacheName("topMatches", season, round, leagueId)
    cache.getOrElseUpdate(name, 28 days)(TopMatchesOverviewRequest.execute(season, round, leagueId))
  }

  def topSalaryPlayers(season: Int, round: Int, leagueId: Option[Int]): Future[List[PlayerStatOverview]] = {
    val name = cacheName("topSalaryPlayers", season, round, leagueId)
    cache.getOrElseUpdate(name, 28 days)(TopSalaryPlayerOverviewRequest.execute(season, round, leagueId))
  }

  def topRatingPlayers(season: Int, round: Int, leagueId: Option[Int]): Future[List[PlayerStatOverview]] = {
    val name = cacheName("topRatingPlayers", season, round, leagueId)
    cache.getOrElseUpdate(name, 28 days)(TopRatingPlayerOverviewRequest.execute(season, round, leagueId))
  }

  def totalOverview(season: Int, round: Int, leagueId: Option[Int]): Future[TotalOverview] = {
    for(numberOverviewData <- numberOverview(season, round, leagueId);
        formationsData <- formations(season, round, leagueId);
        averageOverviewData <- averageOverview(season, round, leagueId);
        surprisingMatchesData <- surprisingMatches(season, round, leagueId);
        topHatstatsTeamsData <- topHatstatsTeams(season, round, leagueId);
        topSalaryTeamsData <- topSalaryTeams(season, round, leagueId);
        topMatchesData <- topMatches(season, round, leagueId);
        topSalaryPlayersData <- topSalaryPlayers(season, round, leagueId);
        topRatingPlayersData <- topRatingPlayers(season, round, leagueId)) yield

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
