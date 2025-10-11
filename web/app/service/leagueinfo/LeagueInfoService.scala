package service.leagueinfo

import chpp.worlddetails.models.League
import controllers.LeagueTime
import databases.dao.RestClickhouseDAO
import databases.requests.matchdetails.HistoryInfoRequest
import hattid.CommonData
import models.clickhouse.HistoryInfo
import models.web.{HattidError, HattidInternalError, NotFoundError}
import service.ChppService
import service.leagueinfo.LeagueInfoServiceZIO.{DivisionLevel, LeagueId, Round, Season}
import zio.concurrent.ConcurrentMap
import zio.*

import java.util.Date

case class LeagueState(league: League,
                       loadingInfo: LoadingInfo,
                       seasonRoundInfo: List[(Season, List[Round])],
                       idToCountryName: List[(Int, String)])

extension [T](map: ConcurrentMap[Int, T])
  def maxKey: UIO[Option[Int]] = map.fold(None: Option[Int]){ case (s, (k, _)) =>
    s.map(sv => Math.max(sv, k)).orElse(Some(k))
  }

class LeagueInfoServiceZIO(private val leagueInfoMap: ConcurrentMap[LeagueId, LeagueInfoZIO]) {
  def leagueState(leagueId: LeagueId): IO[NotFoundError, LeagueState] =
    for {
      league            <- leagueData(leagueId)
      seasonRoundInfo   <- seasonRoundInfo(leagueId)
      loadingStatus     <- getLoadingStatus(leagueId)
      idToStringCountry <- idToStringCountryMap
    } yield LeagueState(league = league,
      loadingInfo = loadingStatus,
      seasonRoundInfo = seasonRoundInfo,
      idToCountryName = idToStringCountry)

  private def leagueInfo(leagueId: LeagueId): IO[NotFoundError, LeagueInfoZIO] = {
    (for {
      leagueInfoOpt <- leagueInfoMap.get(leagueId)
      leagueInfo <- ZIO.fromOption(leagueInfoOpt)
    } yield leagueInfo)
      .mapError(_ => NotFoundError(entityType = "LEAGUE",
        entityId = leagueId.toString,
        description = s"Not found league info for leagueId=$leagueId")
      )
  }
  
  def leagueData(leagueId: LeagueId): IO[NotFoundError, League] =
    leagueInfo(leagueId).map(_.league)

  def leagueRoundForSeason(leagueId: LeagueId, season: Season): IO[HattidError, Round] = {
    for {
      lInfo      <- leagueInfo(leagueId)
      seasonInfo <- lInfo.seasonInfo(season)
      round      <- seasonInfo.size
    } yield round
  }

  def seasonRoundInfo(leagueId: Int): IO[NotFoundError, List[(Season, List[Round])]] = {
    for {
      leagueInfo <- leagueInfo(leagueId)
      result <- leagueInfo.seasonsRounds
    } yield result

  }
  
  def setLoadingStatus(leagueId: LeagueId, info: LoadingInfo): IO[NotFoundError, Unit] = {
    for {
      leagueInfo <- leagueInfo(leagueId)
      _          <- leagueInfo.setLoadingInfo(info)
    } yield ()
  }

  def finishAll(): UIO[List[Unit]] = {
    for {
      list <- leagueInfoMap.toList
      zios = list.map(_._2).map(leagueInfo => leagueInfo.setLoadingInfo(Finished))
      r <- ZIO.collectAll(zios)
    } yield r
  }
  
  def addAnotherRound(leagueId: LeagueId, 
                      season: Season, 
                      round: Round, 
                      roundInfos: List[HistoryInfo]): ZIO[ChppService, HattidInternalError, Unit] = {
    for {
      chppService   <- ZIO.service[ChppService]
      leagueInfoOpt <- leagueInfoMap.get(leagueId)
      leagueInfo    <- leagueInfoOpt.map(ZIO.succeed).getOrElse(
                          (for {
                            worldDetails <- chppService.getWorldDetails()
                            league       <- ZIO.fromOption(worldDetails.leagueList.find(_.leagueId == leagueId))
                            leagueInfo   <- LeagueInfoZIO.make(Nil, league)
                          } yield leagueInfo)
                            .mapError(_ => HattidInternalError(s"Unable to update league $leagueId"))
                        )
      _              <- leagueInfo.addAnotherRound(season, round, roundInfos)
      _              <- leagueInfoMap.put(leagueId, leagueInfo)
    } yield ()
  }

  def updateStatuses(scheduleInfo: Seq[LeagueTime]): Unit = {
    val zios = scheduleInfo.map { leagueTime =>
      val zio = for {
        leagueInfoOpt <- leagueInfoMap.get(leagueTime.leagueId)
        leagueInfo    <- ZIO.fromOption(leagueInfoOpt)
        z             <- leagueInfo.setLoadingInfo(Scheduled(leagueTime.time))
      } yield z
      leagueTime.leagueId -> zio.either
    }.map {case (leagueId, zio) => 
      zio.map { _ => leagueId }
    }
    
    val r = ZIO.collectAll(zios)
  }
  
  def getLoadingStatus(leagueId: LeagueId): IO[NotFoundError, LoadingInfo] = {
    for {
      leagueInfo <- leagueInfo(leagueId)
      status     <- leagueInfo.getLoadingStatus
    } yield status
  }
  
  def idToStringCountryMap: UIO[List[(LeagueId, String)]] =
    for {
      list   <- leagueInfoMap.toList
      result = list.map{ case (leagueId, leagueInfo) => leagueId -> leagueInfo.league.englishName }
        .sortBy(_._2)
    } yield result

  def currentSeason(leagueId: LeagueId): IO[NotFoundError, Season] = {
    for {
      leagueInfo <- leagueInfo(leagueId)
      season     <- leagueInfo.currentSeason
    } yield season
  }

  def lastRound(leagueId: LeagueId, season: Season): IO[NotFoundError, RuntimeFlags] = {
    for {
      leagueInfo <- leagueInfo(leagueId)
      round      <- leagueInfo.lastRound(season)
    } yield round
  }

  def getRelativeSeasonFromAbsolute(season: Int, leagueId: Int): IO[NotFoundError, Season] =
    leagueInfo(leagueId).map(_.getRelativeSeasonFromAbsolute(season))

  def getProcessedCountriesNumber: UIO[Int] =
    for {
      list <- leagueInfoMap.toList
      x <- ZIO.foreach(list)(_._2.getLoadingStatus)
      processedCountriesNumber = list.zip(x).count { case ((_, leagueInfo), status) => status == Scheduled }
    } yield processedCountriesNumber

  def getNextAndCurrentCountry: UIO[(Option[(LeagueId, String, Date)], Option[(LeagueId, String)])] = {
    for {
      list <- leagueInfoMap.toList
      x <- ZIO.foreach(list)(_._2.getLoadingStatus)
      zipped = list.zip(x)
      nextCountry = zipped.filter(_._2.isInstanceOf[Scheduled])
        .sortBy(_._2.asInstanceOf[Scheduled].date.getTime)
        .headOption
        .map{ case ((leagueId, leagueInfoZio), loadingInfo) => (leagueId, leagueInfoZio.league.englishName, loadingInfo.asInstanceOf[Scheduled].date) }
      currentCountry = zipped.find(_._2 == Loading)
        .map{ case ((leagueId, leagueInfoZio), _) => leagueId -> leagueInfoZio.league.englishName }
    } yield (nextCountry, currentCountry)
  }

  def countriesNumber: UIO[Int] = leagueInfoMap.toList.map(_.size)

  def leagueExists(leagueId: LeagueId): UIO[Boolean] = leagueInfoMap.get(leagueId)
    .map(_.isDefined)

  def lastFullRound(): IO[NotFoundError, Int] = {
    for {
      //Salvador is the last league to load
      leagueInfo <- leagueInfo(CommonData.LAST_SERIES_LEAGUE_ID)
      lastSeason <- lastFullSeason()
      lastRound  <- leagueInfo.lastRound(lastSeason)
    } yield lastRound
  }

  def divisionLevelExists(leagueId: LeagueId, season: Season, round: Round, divisionLevel: DivisionLevel): UIO[Boolean] =
    for {
      leagueInfo <- leagueInfoMap.get(leagueId)
      exists     <- leagueInfo.map(_.divisionLevelExists(season, round, divisionLevel)).getOrElse(ZIO.succeed(false))
    } yield exists

  def lastFullSeason(): IO[NotFoundError, Int] =
    leagueInfo(CommonData.LAST_SERIES_LEAGUE_ID).flatMap(_.currentSeason)
    
  def numberOfTeamsForLeaguePerRound(leagueId: LeagueId, divisionLevel: Option[DivisionLevel], season: Season): IO[NotFoundError, List[(Round, Long)]] =
    for {
      leagueInfo <- leagueInfo(leagueId)
      list       <- leagueInfo.numberOfTeamsForLeaguePerRound(season, divisionLevel)
    } yield list
}

object LeagueInfoServiceZIO {
  type LeagueId = Int
  type Season = Int
  type Round = Int
  type DivisionLevel = Int

  lazy val leagueNumbersMap: Map[Int, Seq[Int]] = Map(1 -> Seq(1),
    2 -> (1 to 4),
    3 -> (1 to 16),
    4 -> (1 to 64),
    5 -> (1 to 256),
    6 -> (1 to 1024),
    7 -> (1 to 1024),
    8 -> (1 to 2048),
    9 -> (1 to 2048)
  )

  lazy val layer: ZLayer[RestClickhouseDAO & ChppService, HattidError, LeagueInfoServiceZIO] = {
    ZLayer {
      for {
        chppService <- ZIO.service[ChppService]
        worldDetails <- chppService.getWorldDetails()
        leagueIdToCountryNameMap = worldDetails.leagueList.map(league => league.leagueId -> league)
        history <- HistoryInfoRequest.execute(None, None, None)
        leagueHistoryInfos = history.groupBy(_.leagueId)
        map = leagueIdToCountryNameMap.map((leagueId, league) => leagueId -> LeagueInfoZIO.make(leagueHistoryInfos(leagueId), league))
        sequenced <- ZIO.foreach(map){ case (k, vZio) => vZio.map(v => k -> v) }
        zioMap    <- ConcurrentMap.fromIterable(sequenced)
      } yield new LeagueInfoServiceZIO(zioMap)
    }
  }
}
