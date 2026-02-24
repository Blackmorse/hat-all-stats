package com.blackmorse.hattid.web.service

import com.blackmorse.hattid.web.databases.requests.model.`match`.*
import com.blackmorse.hattid.web.databases.requests.model.overview.*
import com.blackmorse.hattid.web.models.web.{HattidError, HattidInternalError}
import com.blackmorse.hattid.web.service.cache.CacheKey.EntityType
import com.blackmorse.hattid.web.service.cache.{CacheKey, OverviewCache}
import com.blackmorse.hattid.web.databases.ClickhousePool.ClickhousePool
import zio.ZIO
import zio.cache.Cache

import scala.language.postfixOps
import scala.reflect.ClassTag

object RestOverviewStatsService {
  private def fetchFromCache[T: ClassTag](entityType: EntityType,
                                          season: Int,
                                          round: Int,
                                          leagueId: Option[Int],
                                          divisionLevel: Option[Int]): ZIO[ClickhousePool & OverviewCache.CacheType, HattidError, List[T]] = {
    val key = CacheKey(entityType, season, round, leagueId, divisionLevel)

    for {
      zioCache <- ZIO.service[OverviewCache.CacheType]
      result <- zioCache.get(key)
      typed <- result.headOption match {
        case Some(head: T) => ZIO.succeed(result.asInstanceOf[List[T]])
        case None => ZIO.succeed(Nil)
        case _ => ZIO.fail(HattidInternalError("Cache type error"))
      }
    } yield typed
  }

  def numberOverview(season: Int, round: Int,
                     leagueId: Option[Int], divisionLevel: Option[Int]):  ZIO[ClickhousePool & OverviewCache.CacheType, HattidError, NumberOverview] = {
    for {
      list <- fetchFromCache[NumberOverview]("numberOverview", season, round, leagueId, divisionLevel)
      head <- ZIO.fromOption(list.headOption)
        .mapError(_ => HattidInternalError("No data in cache") )
    } yield head
  }


  def formations(season: Int, round: Int,
                 leagueId: Option[Int], divisionLevel: Option[Int]): ZIO[ClickhousePool & OverviewCache.CacheType, HattidError, List[FormationsOverview]] =
    fetchFromCache[FormationsOverview]("formations", season, round, leagueId, divisionLevel)


  def averageOverview(season: Int, round: Int,
                      leagueId: Option[Int], divisionLevel: Option[Int]): ZIO[ClickhousePool & OverviewCache.CacheType, HattidError, AveragesOverview] = {
    for {
      list <- fetchFromCache[AveragesOverview]("averageOverview", season, round, leagueId, divisionLevel)
      head <- ZIO.fromOption(list.headOption)
        .mapError(_ => HattidInternalError("No data in cache") )
    } yield head
  }

  def surprisingMatches(season: Int, round: Int,
                        leagueId: Option[Int], divisionLevel: Option[Int]): ZIO[ClickhousePool & OverviewCache.CacheType, HattidError, List[MatchTopHatstats]] =
    fetchFromCache[MatchTopHatstats]("surprisingMatches", season, round, leagueId, divisionLevel)

  def topHatstatsTeams(season: Int, round: Int,
                       leagueId: Option[Int], divisionLevel: Option[Int]): ZIO[ClickhousePool & OverviewCache.CacheType, HattidError, List[TeamStatOverview]] =
    fetchFromCache[TeamStatOverview]("topHatstatsTeams", season, round, leagueId, divisionLevel)

  def topSalaryTeams(season: Int, round: Int,
                     leagueId: Option[Int], divisionLevel: Option[Int]): ZIO[ClickhousePool & OverviewCache.CacheType, HattidError, List[TeamStatOverview]] =
    fetchFromCache[TeamStatOverview]("topSalaryTeams", season, round, leagueId, divisionLevel)

  def topMatches(season: Int, round: Int,
                 leagueId: Option[Int], divisionLevel: Option[Int]): ZIO[ClickhousePool & OverviewCache.CacheType, HattidError, List[MatchTopHatstats]] =
    fetchFromCache[MatchTopHatstats]("topMatches", season, round, leagueId, divisionLevel)

  def topSalaryPlayers(season: Int, round: Int,
                       leagueId: Option[Int], divisionLevel: Option[Int]): ZIO[ClickhousePool & OverviewCache.CacheType, HattidError, List[PlayerStatOverview]] =
    fetchFromCache[PlayerStatOverview]("topSalaryPlayers", season, round, leagueId, divisionLevel)

  def topRatingPlayers(season: Int, round: Int,
                       leagueId: Option[Int], divisionLevel: Option[Int]): ZIO[ClickhousePool & OverviewCache.CacheType, HattidError, List[PlayerStatOverview]] =
    fetchFromCache[PlayerStatOverview]("topRatingPlayers", season, round, leagueId, divisionLevel)

  def topMatchAttendance(season: Int, round: Int,
                         leagueId: Option[Int], divisionLevel: Option[Int]): ZIO[ClickhousePool & OverviewCache.CacheType, HattidError, List[MatchAttendanceOverview]] =
    fetchFromCache[MatchAttendanceOverview]("topMatchAttendance", season, round, leagueId, divisionLevel)

  def topTeamVictories(season: Int, round: Int,
                       leagueId: Option[Int], divisionLevel: Option[Int]): ZIO[ClickhousePool & OverviewCache.CacheType, HattidError, List[TeamStatOverview]] =
    fetchFromCache[TeamStatOverview]("topTeamVictories", season, round, leagueId, divisionLevel)

  def topSeasonScorers(season: Int, round: Int,
                       leagueId: Option[Int], divisionLevel: Option[Int]): ZIO[ClickhousePool & OverviewCache.CacheType, HattidError, List[PlayerStatOverview]] =
    fetchFromCache[PlayerStatOverview]("topSeasonScorers", season, round, leagueId, divisionLevel)

  def totalOverview(season: Int, round: Int,
                    leagueId: Option[Int], divisionLevel: Option[Int]): ZIO[ClickhousePool & OverviewCache.CacheType, HattidError, TotalOverview] = {
    for {
      numberOverviewData     <- numberOverview(season, round, leagueId, divisionLevel);
      formationsData         <- formations(season, round, leagueId, divisionLevel);
      averageOverviewData    <- averageOverview(season, round, leagueId, divisionLevel);
      surprisingMatchesData  <- surprisingMatches(season, round, leagueId, divisionLevel);
      topHatstatsTeamsData   <- topHatstatsTeams(season, round, leagueId, divisionLevel);
      topSalaryTeamsData     <- topSalaryTeams(season, round, leagueId, divisionLevel);
      topMatchesData         <- topMatches(season, round, leagueId, divisionLevel);
      topSalaryPlayersData   <- topSalaryPlayers(season, round, leagueId, divisionLevel);
      topRatingPlayersData   <- topRatingPlayers(season, round, leagueId, divisionLevel);
      topMatchAttendanceData <- topMatchAttendance(season, round, leagueId, divisionLevel);
      topTeamVictoriesData   <- topTeamVictories(season, round, leagueId, divisionLevel);
      topSeasonScorersData   <- topSeasonScorers(season, round, leagueId, divisionLevel)
    } yield
      TotalOverview(numberOverview = numberOverviewData,
        formations = formationsData,
        averageOverview = averageOverviewData,
        surprisingMatches = surprisingMatchesData,
        topHatstatsTeams = topHatstatsTeamsData,
        topSalaryTeams = topSalaryTeamsData,
        topMatches = topMatchesData,
        topSalaryPlayers = topSalaryPlayersData,
        topRatingPlayers = topRatingPlayersData,
        topMatchAttendance = topMatchAttendanceData,
        topTeamVictories = topTeamVictoriesData,
        topSeasonScorers = topSeasonScorersData)
  }
}
