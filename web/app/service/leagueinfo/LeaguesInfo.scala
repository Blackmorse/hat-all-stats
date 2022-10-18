package service.leagueinfo

import chpp.worlddetails.models.League
import models.clickhouse.HistoryInfo

import scala.collection.mutable

case class LeaguesInfo(leagueInfo: Map[Int, LeagueInfo]) {
  def apply(leagueId: Int): LeagueInfo = leagueInfo(leagueId)
  def get(leagueId: Int): Option[LeagueInfo] = leagueInfo.get(leagueId)

  def seasonRoundInfo(leagueId: Int): Seq[(Int, Seq[Int])] = {
    leagueInfo(leagueId).seasonInfo
      .map{case(season, seasonInfo) => (season, seasonInfo.roundInfo.keys.toSeq.sorted)}
      .toSeq
      .sortBy(_._1)
  }

  def seasons(leagueId: Int): Seq[Int] = {
    leagueInfo(leagueId).seasonInfo.keys.toSeq
  }

  def currentSeason(leagueId: Int): Int = {
    leagueInfo(leagueId).currentSeason()
  }

  def currentRound(leagueId: Int): Int = {
    leagueInfo(leagueId).currentRound()
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

case class LeagueInfo(leagueId: Int, seasonInfo: mutable.Map[Int, SeasonInfo], league: League, var loadingInfo: LoadingInfo) {
  def currentRound(): Int =
    seasonInfo.maxBy(_._1)._2.roundInfo.maxBy(_._1)._1

  def currentSeason(): Int = seasonInfo.maxBy(_._1)._1
}

case class SeasonInfo(season: Int, roundInfo: mutable.Map[Int, RoundInfo])

case class RoundInfo(round: Int, divisionLevelInfo: mutable.Map[Int, DivisionLevelInfo])

case class DivisionLevelInfo(divisionLevel: Int, count: Int)
