package com.blackmorse.hattid.web.service.leagueinfo

import chpp.worlddetails.models.League
import com.blackmorse.hattid.web.models.clickhouse.HistoryInfo
import com.blackmorse.hattid.web.service.leagueinfo.LeagueInfoServiceZIO.{DivisionLevel, Round, Season}
import zio.concurrent.ConcurrentMap
import zio.{IO, Ref, RuntimeFlags, UIO, ZIO}
import com.blackmorse.hattid.web.models.web.{HattidError, NotFoundError}

case class DivisionLevelInfo(divisionLevel: Int, count: Int)

class LeagueInfoZIO(private val leagueId: Int,
                    private val seasonInfoMap: ConcurrentMap[Season, SeasonInfoZIO],
                    private val loadingStatus: Ref[LoadingInfo],
                    val league: League) {
  def seasonInfo(season: Season): IO[NotFoundError, SeasonInfoZIO] = {
    (for {
      seasonInfoOpt <- seasonInfoMap.get(season)
      seasonInfo    <- ZIO.fromOption(seasonInfoOpt)
    } yield seasonInfo)
      .mapError(_ => NotFoundError(entityType = "SEASON",
        entityId = s"$leagueId-$season",
        description = s"Not found season info for leagueId=$leagueId, season=$season")
      )
  }
  def getRelativeSeasonFromAbsolute(season: Season): Season = league.seasonOffset + season

  def seasonsRounds: UIO[List[(Season, List[Round])]] = {
    for {
      list <- seasonInfoMap.toList
      seasons = list.sortBy(_._1).map { case (season, seasonInfo) =>
        season -> seasonInfo.rounds
      }
      result <- ZIO.foreach(seasons){ case (season, roundsZIO) => roundsZIO.map(rounds => season -> rounds) }
    } yield result
  }

  def setLoadingInfo(info: LoadingInfo): UIO[Unit] = loadingStatus.set(info)

  def addAnotherRound(season: Int, round: Int, roundInfos: List[HistoryInfo]): UIO[Unit] = {
    for {
      seasonInfoOpt <- seasonInfoMap.get(season)
      seasonInfo    <- seasonInfoOpt.map(si => ZIO.succeed(si)).getOrElse(SeasonInfoZIO.make(season, Nil))
      _             <- seasonInfo.updateRound(round, roundInfos)
      _             <- seasonInfoMap.put(season, seasonInfo)
    } yield ()
  }

  def divisionLevelExists(season: Season, round: Round, divisionLevel: DivisionLevel): UIO[Boolean] =
    for {
      seasonInfo <- seasonInfoMap.get(season)
      exists     <- seasonInfo.map(_.divisionLevelExists(round, divisionLevel)).getOrElse(ZIO.succeed(false))
    } yield exists

  def getLoadingStatus: UIO[LoadingInfo] = loadingStatus.get

  def currentSeason: IO[NotFoundError, Int] = seasonInfoMap.maxKey
    .flatMap(ZIO.fromOption)
    .mapError(_ => NotFoundError(entityType = "LEAGUE",
      entityId = leagueId.toString,
      description = s"Not found current season for leagueId=$leagueId")
    )

  def lastRound(season: Season): IO[NotFoundError, RuntimeFlags] = seasonInfo(season).flatMap(_.lastRound)

  def numberOfTeamsForLeaguePerRound(season: Season, divisionLevel: Option[DivisionLevel]): IO[NotFoundError, List[(Round, Long)]] =
    for {
      seasonInfo <- seasonInfo(season)
      list       <- seasonInfo.numberOfTeamsPerRound(divisionLevel)
    } yield list
}

object LeagueInfoZIO {
  def make(historyInfos: List[HistoryInfo], league: League): UIO[LeagueInfoZIO] = {
    val seasonInfoMap = historyInfos.groupBy(_.season).map { case (season, historyInfos) =>
      season -> SeasonInfoZIO.make(season, historyInfos)
    }

    for {
      sequenced <- ZIO.foreach(seasonInfoMap){ case (k, vZio) => vZio.map(v => k -> v) }
      concurrentMap <- ConcurrentMap.fromIterable(sequenced)
      ref <- Ref.make[LoadingInfo](Finished)
    } yield new LeagueInfoZIO(league.leagueId, concurrentMap, ref, league)
  }
}
