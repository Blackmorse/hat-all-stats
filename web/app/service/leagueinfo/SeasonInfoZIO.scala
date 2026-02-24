package service.leagueinfo

import models.clickhouse.HistoryInfo
import models.web.NotFoundError
import service.leagueinfo.LeagueInfoServiceZIO.{DivisionLevel, Round, Season}
import service.leagueinfo.RoundInfoZIO
import zio.concurrent.ConcurrentMap
import zio.{IO, RuntimeFlags, UIO, ZIO}

class SeasonInfoZIO(private val season: Season, private val roundInfoMap: ConcurrentMap[Round, RoundInfoZIO]) {
  def size: UIO[Int] = roundInfoMap.toList.map(_.length)

  def numberOfTeamsPerRound(divisionLevel: Option[Int]): IO[NotFoundError, List[(Round, Long)]] =
    for {
      list       <- roundInfoMap.fold(List.empty[(Round, IO[NotFoundError, Long])]){ case (list, (round, roundInfo)) =>
        (round -> roundInfo.sumAllDivisionLevelTeamsNumber(divisionLevel)) :: list
      }
      roundToCount <- ZIO.foreach(list)(el => el._2.map(count => el._1 -> count))
    } yield roundToCount

  def updateRound(round: Round, roundInfos: List[HistoryInfo]): UIO[Option[RoundInfoZIO]] = {
    for {
      newRoundInfo <- RoundInfoZIO.make(round, roundInfos)
      put          <- roundInfoMap.put(round, newRoundInfo)
    } yield put
  }

  def rounds: UIO[List[Round]] = {
    for {
      list   <- roundInfoMap.toList
      rounds = list.map(_._1).sorted
    } yield rounds
  }

  def divisionLevelExists(round: Round, divisionLevel: DivisionLevel): UIO[Boolean] =
    for {
      roundInfo <- roundInfoMap.get(round)
      exists    <- roundInfo.map(_.divisionLevelExists(divisionLevel)).getOrElse(ZIO.succeed(false))
    } yield exists

  def lastRound: IO[NotFoundError, RuntimeFlags] = roundInfoMap.maxKey
    .flatMap(ZIO.fromOption)
    .mapError(_ => NotFoundError(entityType = "SEASON",
      entityId = s"$season",
      description = s"Not found last round for season=$season"))
}

object SeasonInfoZIO {
  def make(season: Int, historyInfos: List[HistoryInfo]): UIO[SeasonInfoZIO] = {
    val map = historyInfos.groupBy(_.round).map { case (round, historyInfos) =>
      round -> RoundInfoZIO.make(round, historyInfos)
    }

    for {
      sequenced <- ZIO.foreach(map){ case (k, vZio) => vZio.map(v => k -> v) }
      concurrentMap <- ConcurrentMap.fromIterable(sequenced)
    } yield new SeasonInfoZIO(season, concurrentMap)
  }
}
