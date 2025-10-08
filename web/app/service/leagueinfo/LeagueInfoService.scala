package service.leagueinfo

import chpp.worlddetails.WorldDetailsRequest
import chpp.worlddetails.models.{League, WorldDetails}
import databases.dao.RestClickhouseDAO
import databases.requests.matchdetails.HistoryInfoRequest
import hattid.CommonData
import models.clickhouse.HistoryInfo
import models.web.{HattidError, NotFoundError}
import webclients.ChppClient
import service.ChppService
import service.leagueinfo.LeagueInfoServiceZIO.{DivisionLevel, LeagueId, Round, Season}
import zio.{Unsafe, ZIO, ZLayer}

import javax.inject.{Inject, Singleton}
import scala.collection.mutable
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt
import zio.*
import zio.concurrent.ConcurrentMap


class LeagueInfoServiceZIO(private val leagueInfoMap: ConcurrentMap[LeagueId, LeagueInfoZio]) {
  def leagueInfo(leagueId: LeagueId): IO[NotFoundError, LeagueInfoZio] = {
    (for {
      leagueInfoOpt <- leagueInfoMap.get(leagueId)
      leagueInfo <- ZIO.fromOption(leagueInfoOpt)
    } yield leagueInfo)
      .mapError(_ => NotFoundError(entityType = "LEAGUE",
        entityId = leagueId.toString,
        description = s"Not found league info for leagueId=$leagueId")
      )
  }

  def leagueRoundForSeason(leagueId: LeagueId, season: Int): IO[HattidError, Round] = {
    for {
      lInfo      <- leagueInfo(leagueId)
      seasonInfo <- lInfo.seasonInfo(season)
      round      <- seasonInfo.size
    } yield round
  }
}

object LeagueInfoServiceZIO {
  type LeagueId = Int
  type Season = Int
  type Round = Int
  type DivisionLevel = Int

  lazy val layer: ZLayer[RestClickhouseDAO & ChppService, HattidError, LeagueInfoServiceZIO] = {
    ZLayer {
      for {
        chppService <- ZIO.service[ChppService]
        leagueInfoMap <- ConcurrentMap.make[LeagueId, LeagueInfo]()
        worldDetails <- chppService.getWorldDetails()
        leagueIdToCountryNameMap = worldDetails.leagueList.map(league => league.leagueId -> league)
        history <- HistoryInfoRequest.execute(None, None, None)
        leagueHistoryInfos = history.groupBy(_.leagueId)
        map = leagueIdToCountryNameMap.map((leagueId, league) => leagueId -> LeagueInfoZio.make(leagueHistoryInfos(leagueId), league))
        sequenced <- ZIO.foreach(map){ case (k, vZio) => vZio.map(v => k -> v) }
        zioMap    <- ConcurrentMap.fromIterable(sequenced)
      } yield new LeagueInfoServiceZIO(zioMap)
    }
  }
}


class LeagueInfoZio(private val leagueId: Int,
                    private val seasonInfoMap: ConcurrentMap[Season, SeasonInfoZIO],
                    private val loadingInfo: Ref[LoadingInfo]) {
  def seasonInfo(season: Season): IO[NotFoundError, SeasonInfoZIO] = {
    (for {
      seasonInfoOpt <- seasonInfoMap.get(season)
      seasonInfo <- ZIO.fromOption(seasonInfoOpt)
    } yield seasonInfo)
      .mapError(_ => NotFoundError(entityType = "SEASON",
        entityId = s"$leagueId-$season",
        description = s"Not found season info for leagueId=$leagueId, season=$season")
      )
  }


}

object LeagueInfoZio {
  def make(historyInfos: List[HistoryInfo], league: League): UIO[LeagueInfoZio] = {
    val seasonInfoMap = historyInfos.groupBy(_.season).map { case (season, historyInfos) =>
      season -> SeasonInfoZIO.make(season, historyInfos)
    }

    for {
      sequenced <- ZIO.foreach(seasonInfoMap){ case (k, vZio) => vZio.map(v => k -> v) }
      concurrentMap <- ConcurrentMap.fromIterable(sequenced)
      ref <- Ref.make[LoadingInfo](Finished)
    } yield new LeagueInfoZio(league.leagueId, concurrentMap, ref)

  }
}

class SeasonInfoZIO(private val season: Season, private val roundInfoMap: ConcurrentMap[Round, RoundInfoZio]) {
  def size: UIO[Int] = roundInfoMap.toList.map(_.length)
}

object SeasonInfoZIO {
  def make(season: Int, historyInfos: List[HistoryInfo]): UIO[SeasonInfoZIO] = {
    val map = historyInfos.groupBy(_.round).map { case (round, historyInfos) =>
      round -> RoundInfoZio.make(round, historyInfos)
    }

    for {
      sequenced <- ZIO.foreach(map){ case (k, vZio) => vZio.map(v => k -> v) }
      concurrentMap <- ConcurrentMap.fromIterable(sequenced)
    } yield new SeasonInfoZIO(season, concurrentMap)
  }
}

class RoundInfoZio(round: Int, private val divisionLevelInfoMap: ConcurrentMap[DivisionLevel, DivisionLevelInfo])

object RoundInfoZio {
  def make(round: Int, historyInfos: List[HistoryInfo]): UIO[RoundInfoZio] = {
    val map = historyInfos.groupBy(_.divisionLevel).map { case (divisionLevel, historyInfos) =>
      divisionLevel -> DivisionLevelInfo(divisionLevel, historyInfos.head.count)
    }

    for {
      sequenced <- ZIO.foreach(map){ case (k, v) => ZIO.succeed(k -> v) }
      concurrentMap <- ConcurrentMap.fromIterable(sequenced)
    } yield new RoundInfoZio(round, concurrentMap)
  }
}


@Singleton
class LeagueInfoService @Inject() (val chppClient: ChppClient,
                                   implicit val restClickhouseDAO: RestClickhouseDAO,
                                  ) {
  private val runtime = zio.Runtime.default

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

  def lastFullRound(): Int = {
    //Salvador has last league matches
    leagueInfo.currentRound(CommonData.LAST_SERIES_LEAGUE_ID)
  }

  def lastFullSeason(): Int = {
    leagueInfo.currentSeason(CommonData.LAST_SERIES_LEAGUE_ID)
  }

  def getRelativeSeasonFromAbsolute(season: Int, leagueId: Int): Int = leagueInfo(leagueId).league.seasonOffset + season

//  val leagueInfoZio: ZIO[RestClickhouseDAO & ChppService, HattidError, LeaguesInfo] = {
//    for {
//      chppService <- ZIO.service[ChppService]
//      worldDetails <- chppService.getWorldDetails()
//      leagueIdToCountryNameMap = worldDetails.leagueList.map(league => league.leagueId -> league)
//      history <- HistoryInfoRequest.execute(None, None, None)
//      leagueHistoryInfos = history.groupBy(_.leagueId)
//
//    } yield LeaguesInfo(leagueInfoFromMap(leagueHistoryInfos, leagueIdToCountryNameMap).toMap)
//  }

  private def leagueInfoFromMap(leagueHistoryInfos: Map[Int, List[HistoryInfo]], leagueIdToCountryNameMap: Seq[(Int, League)]) = {
    for ((lId, league) <- leagueIdToCountryNameMap) yield {
      val leagueId = lId.toInt
      leagueHistoryInfos.get(leagueId).map(historyInfos =>
          leagueId -> LeagueInfo(leagueId, historyInfos.groupBy(_.season).map { case (season, historyInfos) =>
            season -> SeasonInfo(season, historyInfos.groupBy(_.round).map { case (round, historyInfos) =>
              round -> RoundInfo(round, historyInfos.groupBy(_.divisionLevel).map { case (divisionLevel, historyInfos) =>
                divisionLevel -> DivisionLevelInfo(divisionLevel, historyInfos.head.count)
              }
              )
            }
            )
          }
            , league, Finished))
        .getOrElse(leagueId -> LeagueInfo(leagueId, Map(), league, Finished))
    }
  }

  val leagueInfo: LeaguesInfo = {

    val leagueIdToCountryNameMap = Await.result(chppClient.executeUnsafe[WorldDetails, WorldDetailsRequest](WorldDetailsRequest())
      .map(_.leagueList.map(league => league.leagueId -> league)), 60.seconds)




//    val leagueHistoryInfos = Await.result(HistoryInfoRequest.execute(None, None, None), 1.minute)
//      .groupBy(_.leagueId)

    val zioHistory = HistoryInfoRequest.execute(None, None, None)
      .provide(ZLayer.succeed(restClickhouseDAO))

    // TODO ZIO!
    val future = Unsafe.unsafe { implicit unsafe =>
      runtime.unsafe.runToFuture(
        zioHistory.catchAll(e => zio.ZIO.fail(new Exception("Failed to get league history info " + e.toString)))
      )
    }

    val leagueHistoryInfos = Await.result(future, 1.minute)
      .groupBy(_.leagueId)

    val seq = for ((lId, league) <- leagueIdToCountryNameMap) yield {
      val leagueId  = lId.toInt
      leagueHistoryInfos.get(leagueId).map(historyInfos =>
        leagueId -> LeagueInfo(leagueId, historyInfos.groupBy(_.season).map{case(season, historyInfos) =>
          season -> SeasonInfo(season, historyInfos.groupBy(_.round).map{case(round, historyInfos) =>
            round -> RoundInfo(round, historyInfos.groupBy(_.divisionLevel).map{case(divisionLevel, historyInfos) =>
              divisionLevel -> DivisionLevelInfo(divisionLevel, historyInfos.head.count)}
            )}
          )}
          , league, Finished))
        .getOrElse(leagueId -> LeagueInfo(leagueId, Map(), league, Finished))
    }

    LeaguesInfo(seq.toMap)
  }

  val idToStringCountryMap: Seq[(Int, String)] = leagueInfo.leagueInfo
    .toSeq
    .map{case(leagueId, leagueInfo) => (leagueId, leagueInfo.league.englishName)}
    .sortBy(_._2)

  implicit def toMutable[T](map: Map[Int, T]): mutable.Map[Int, T] = {
    mutable.Map(map.toSeq*)
  }
}