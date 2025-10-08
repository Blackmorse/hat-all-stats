package service

import databases.dao.RestClickhouseDAO
import databases.requests.model.`match`.MatchTopHatstats
import databases.requests.model.overview.*
import databases.requests.overview.*
import models.web.{HattidError, HattidInternalError}
import play.api.cache.AsyncCacheApi
import service.CacheKey.EntityType
import zio.cache.{Cache, Lookup}
import zio.{IO, URIO, ZIO, ZLayer}

import javax.inject.{Inject, Singleton}
import scala.concurrent.duration.*
import scala.language.postfixOps
import scala.reflect.ClassTag

object CacheKey {
  type EntityType = "numberOverview" | "formations" | "averageOverview" | "surprisingMatches" |
    "topHatstatsTeams" | "topSalaryTeams" | "topMatches" | "topSalaryPlayers" |
    "topRatingPlayers" | "topMatchAttendance" | "topTeamVictories" | "topSeasonScorers"
}

case class CacheKey(
                   entityType: EntityType,
                   season: Int,
                   round: Int,
                   leagueId: Option[Int],
                   divisionLevel: Option[Int],
                   )

@Singleton
class RestOverviewStatsService @Inject()
            (val restClickhouseDAO: RestClickhouseDAO,
             cache: AsyncCacheApi) {
  private val zioCache: URIO[RestClickhouseDAO, Cache[CacheKey, HattidError, List[Any]]] = Cache.make(
    capacity = 50000,
    timeToLive = zio.Duration.fromScala(28.days),
    lookup = Lookup({ (key: CacheKey) =>
      key.entityType match {
        case "numberOverview" =>
          for {
            numbers <- NumberOverviewRequest.executeZio(key.season, key.round, key.leagueId, key.divisionLevel).map(_.head)
            newTeams <- NewTeamsOverviewRequest.executeZio(key.season, key.round, key.leagueId, key.divisionLevel).map(_.head)
          } yield NumberOverview(numbers, newTeams) :: Nil
        case "formations" =>
          FormationsOverviewRequest.executeZio(key.season, key.round, key.leagueId, key.divisionLevel)
        case "averageOverview" =>
          for {
            matchAverages <- OverviewMatchAveragesRequest.executeZio(key.season, key.round, key.leagueId, key.divisionLevel)
            teamPlayerAverages <- OverviewTeamPlayerAveragesRequest.executeZio(key.season, key.round, key.leagueId, key.divisionLevel)
          } yield AveragesOverview(matchAverages.head, teamPlayerAverages.head) :: Nil
        case "surprisingMatches" =>
          SurprisingMatchesOverviewRequest.executeZio(key.season, key.round, key.leagueId, key.divisionLevel)
        case "topHatstatsTeams" =>
          TopHatstatsTeamOverviewRequest.executeZio(key.season, key.round, key.leagueId, key.divisionLevel)
        case "topSalaryTeams" =>
          TopSalaryTeamOverviewRequest.executeZio(key.season, key.round, key.leagueId, key.divisionLevel)
        case "topMatches" =>
          TopMatchesOverviewRequest.executeZio(key.season, key.round, key.leagueId, key.divisionLevel)
        case "topSalaryPlayers" =>
          TopSalaryPlayerOverviewRequest.executeZio(key.season, key.round, key.leagueId, key.divisionLevel)
        case "topRatingPlayers" =>
          TopRatingPlayerOverviewRequest.executeZio(key.season, key.round, key.leagueId, key.divisionLevel)
        case "topMatchAttendance" =>
          TopAttendanceMatchesOverviewRequest.executeZio(key.season, key.round, key.leagueId, key.divisionLevel)
        case "topTeamVictories" =>
          TopVictoriesTeamsOverviewRequest.executeZio(key.season, key.round, key.leagueId, key.divisionLevel)
        case "topSeasonScorers" =>
          TopSeasonScorersOverviewRequest.executeZio(key.season, key.round, key.leagueId, key.divisionLevel)
      }
    })
  )

  private def fetchFromCache[T: ClassTag](entityType: EntityType,
                                          season: Int,
                                          round: Int,
                                          leagueId: Option[Int],
                                          divisionLevel: Option[Int]): IO[HattidError, List[T]] = {
    val key = CacheKey(entityType, season, round, leagueId, divisionLevel)

    (for {
      cache <- zioCache
      result <- cache.get(key)
      typed <- result.headOption match {
        case Some(head: T) => ZIO.succeed(result.asInstanceOf[List[T]])
        case None => ZIO.succeed(Nil)
        case _ => ZIO.fail(models.web.HattidInternalError("Cache type error"))
      }
    } yield typed)
      .provide(ZLayer.succeed(restClickhouseDAO))
  }

  def numberOverview(season: Int, round: Int,
                     leagueId: Option[Int], divisionLevel: Option[Int]): IO[HattidError, NumberOverview] = {
    for {
      list <- fetchFromCache[NumberOverview]("numberOverview", season, round, leagueId, divisionLevel)
      head <- ZIO.fromOption(list.headOption)
        .mapError(_ => HattidInternalError("No data in cache") )
    } yield head
  }


  def formations(season: Int, round: Int,
                 leagueId: Option[Int], divisionLevel: Option[Int]): IO[HattidError, List[FormationsOverview]] =
    fetchFromCache[FormationsOverview]("formations", season, round, leagueId, divisionLevel)


  def averageOverview(season: Int, round: Int,
                      leagueId: Option[Int], divisionLevel: Option[Int]): IO[HattidError, AveragesOverview] = {
    for {
      list <- fetchFromCache[AveragesOverview]("averageOverview", season, round, leagueId, divisionLevel)
      head <- ZIO.fromOption(list.headOption)
        .mapError(_ => HattidInternalError("No data in cache") )
    } yield head
  }

  def surprisingMatches(season: Int, round: Int,
                        leagueId: Option[Int], divisionLevel: Option[Int]): IO[HattidError, List[MatchTopHatstats]] =
    fetchFromCache[MatchTopHatstats]("surprisingMatches", season, round, leagueId, divisionLevel)

  def topHatstatsTeams(season: Int, round: Int,
                       leagueId: Option[Int], divisionLevel: Option[Int]): IO[HattidError, List[TeamStatOverview]] =
    fetchFromCache[TeamStatOverview]("topHatstatsTeams", season, round, leagueId, divisionLevel)

  def topSalaryTeams(season: Int, round: Int,
                     leagueId: Option[Int], divisionLevel: Option[Int]): IO[HattidError, List[TeamStatOverview]] =
    fetchFromCache[TeamStatOverview]("topSalaryTeams", season, round, leagueId, divisionLevel)

  def topMatches(season: Int, round: Int,
                 leagueId: Option[Int], divisionLevel: Option[Int]): IO[HattidError, List[MatchTopHatstats]] =
    fetchFromCache[MatchTopHatstats]("topMatches", season, round, leagueId, divisionLevel)

  def topSalaryPlayers(season: Int, round: Int,
                       leagueId: Option[Int], divisionLevel: Option[Int]): IO[HattidError, List[PlayerStatOverview]] =
    fetchFromCache[PlayerStatOverview]("topSalaryPlayers", season, round, leagueId, divisionLevel)

  def topRatingPlayers(season: Int, round: Int,
                       leagueId: Option[Int], divisionLevel: Option[Int]): IO[HattidError, List[PlayerStatOverview]] =
    fetchFromCache[PlayerStatOverview]("topRatingPlayers", season, round, leagueId, divisionLevel)

  def topMatchAttendance(season: Int, round: Int,
                         leagueId: Option[Int], divisionLevel: Option[Int]): IO[HattidError, List[MatchAttendanceOverview]] =
    fetchFromCache[MatchAttendanceOverview]("topMatchAttendance", season, round, leagueId, divisionLevel)

  def topTeamVictories(season: Int, round: Int,
                       leagueId: Option[Int], divisionLevel: Option[Int]): IO[HattidError, List[TeamStatOverview]] =
    fetchFromCache[TeamStatOverview]("topTeamVictories", season, round, leagueId, divisionLevel)

  def topSeasonScorers(season: Int, round: Int,
                       leagueId: Option[Int], divisionLevel: Option[Int]): IO[HattidError, List[PlayerStatOverview]] =
    fetchFromCache[PlayerStatOverview]("topSeasonScorers", season, round, leagueId, divisionLevel)

  def totalOverview(season: Int, round: Int,
                    leagueId: Option[Int], divisionLevel: Option[Int]): IO[HattidError, TotalOverview] = {
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
