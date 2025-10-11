package service.leagueinfo

import models.clickhouse.HistoryInfo
import models.web.NotFoundError
import service.leagueinfo.LeagueInfoServiceZIO.DivisionLevel
import zio.{IO, UIO, ZIO}
import zio.concurrent.ConcurrentMap


class RoundInfoZIO(round: Int, private val divisionLevelInfoMap: ConcurrentMap[DivisionLevel, DivisionLevelInfo]) {
  def updateDivisionLevel(divisionLevel: DivisionLevel, roundInfo: HistoryInfo): UIO[Option[DivisionLevelInfo]] =
    divisionLevelInfoMap.put(divisionLevel, DivisionLevelInfo(divisionLevel, roundInfo.count))

  def divisionLevelExists(divisionLevel: DivisionLevel): UIO[Boolean] =
    divisionLevelInfoMap.get(divisionLevel).map(_.isDefined)

  def sumAllDivisionLevelTeamsNumber(divisionLevel: Option[DivisionLevel]): IO[NotFoundError, Long] = {
    divisionLevel match {
      case None => divisionLevelInfoMap.fold(0L) { case (sum, (_, divisionLevelInfo)) =>
        sum + divisionLevelInfo.count
      }
      case Some(divisionLevel) =>
        for {
          divisionLevelInfoOpt <- divisionLevelInfoMap.get(divisionLevel)
          divisionLevelInfo    <- ZIO.fromOption(divisionLevelInfoOpt)
            .mapError(_ => NotFoundError(entityType = "DIVISION_LEVEL",
              entityId = s"$divisionLevel",
              description = s"Not found division level info for round=$round, divisionLevel=$divisionLevel"))
        } yield divisionLevelInfo.count
    }
  }
}

object RoundInfoZIO {
  def make(round: Int, historyInfos: List[HistoryInfo]): UIO[RoundInfoZIO] = {
    val map = historyInfos.groupBy(_.divisionLevel).map { case (divisionLevel, historyInfos) =>
      divisionLevel -> DivisionLevelInfo(divisionLevel, historyInfos.head.count)
    }

    for {
      sequenced <- ZIO.foreach(map){ case (k, v) => ZIO.succeed(k -> v) }
      concurrentMap <- ConcurrentMap.fromIterable(sequenced)
    } yield new RoundInfoZIO(round, concurrentMap)
  }
}
