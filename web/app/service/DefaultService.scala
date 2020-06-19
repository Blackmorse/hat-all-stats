package service

import com.blackmorse.hattrick.api.worlddetails.model.League
import databases.ClickhouseDAO
import hattrick.Hattrick
import javax.inject.{Inject, Singleton}
import models.clickhouse.HistoryInfo
import play.api.Configuration

import collection.JavaConverters._
import scala.collection.mutable

@Singleton
class DefaultService @Inject() (val hattrick: Hattrick,
                                val clickhouseDAO: ClickhouseDAO,
                                val configuration: Configuration) {
  lazy val defaultLeagueId: Int = configuration.get[Int]("hattrick.defaultLeagueId")

  lazy val leagueNumbersMap = Map(1 -> Seq(1),
    2 -> (1 to 4),
    3 -> (1 to 16),
    4 -> (1 to 64),
    5 -> (1 to 256),
    6 -> (1 to 1024),
    7 -> (1 to 1024),
    8 -> (1 to 2048),
    9 -> (1 to 2048)
  )

  def getAbsoluteSeasonFromRelative(season: Int, leagueId: Int) = leagueInfo(leagueId).league.getSeasonOffset + season

  val leagueInfo: LeaguesInfo = {
    val leagueIdToCountryNameMap = hattrick.api.worldDetails().execute()
      .getLeagueList.asScala.map(league => league.getLeagueId -> league)
      .toMap
    val leagueHistoryInfos = clickhouseDAO.historyInfo(None, None, None).groupBy(_.leagueId)

    val seq = for ((lId, league) <- leagueIdToCountryNameMap) yield {
      val leagueId  = lId.toInt
      leagueHistoryInfos.get(leagueId).map(historyInfos =>
        leagueId -> LeagueInfo(leagueId, historyInfos.groupBy(_.season).map{case(season, historyInfos) =>
          season -> SeasonInfo(season, historyInfos.groupBy(_.round).map{case(round, historyInfos) =>
            round -> RoundInfo(round, historyInfos.groupBy(_.divisionLevel).map{case(divisionLevel, historyInfos) =>
              divisionLevel -> DivisionLevelInfo(divisionLevel, historyInfos.head.count)}
            )}
          )}
          , league))
        .getOrElse(leagueId -> LeagueInfo(leagueId, mutable.Map(), league))
    }

    LeaguesInfo(seq)
  }

  implicit def toMutable[T](map: Map[Int, T]): mutable.Map[Int, T] = {
    mutable.Map(map.toSeq: _*)
  }
}

case class LeaguesInfo(leagueInfo: mutable.Map[Int, LeagueInfo]) {
  def apply(leagueId: Int) = leagueInfo(leagueId)

  def seasons(leagueId: Int): Seq[Int] = {
    leagueInfo(leagueId).seasonInfo.keys.toSeq
  }

  def currentSeason(leagueId: Int): Int = {
    leagueInfo(leagueId).seasonInfo.maxBy(_._1)._1
  }

  def currentRound(leagueId: Int): Int = {
    leagueInfo(leagueId).seasonInfo.maxBy(_._1)._1
  }

  def rounds(leagueId: Int, season: Int): Seq[Int] = {
    leagueInfo(leagueId).seasonInfo(season).roundInfo.keys.toSeq.sorted
  }

  /**
   *
   * @param historyInfos - only for one round, for one league!
   * @return
   */
  def add(historyInfos: List[HistoryInfo]) = {
    val historyInfo = historyInfos.head
    leagueInfo(historyInfo.leagueId).seasonInfo
      .getOrElseUpdate(historyInfo.season, SeasonInfo(historyInfo.season, mutable.Map()))
      .roundInfo.getOrElseUpdate(historyInfo.round, RoundInfo(historyInfo.round, mutable.Map(
          historyInfos.map(histInfo => histInfo.divisionLevel -> DivisionLevelInfo(histInfo.divisionLevel, histInfo.count)): _*
    )))
  }
}

case class LeagueInfo(leagueId: Int, seasonInfo: mutable.Map[Int, SeasonInfo], league: League)

case class SeasonInfo(season: Int, roundInfo: mutable.Map[Int, RoundInfo])

case class RoundInfo(round: Int, divisionLevelInfo: mutable.Map[Int, DivisionLevelInfo])

case class DivisionLevelInfo(divisionLevel: Int, count: Int)

object DefaultService {
  val PAGE_SIZE = 16
}
