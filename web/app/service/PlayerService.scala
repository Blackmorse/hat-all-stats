package service

import chpp.commonmodels.MatchType
import databases.requests.model.player.PlayerHistory
import models.web.player.{PlayerChartEntry, PlayerLeagueUnitEntry, PlayerSeasonStats, PlayerSeasonStatsEntry}

import javax.inject.{Inject, Singleton}
import scala.collection.mutable

@Singleton
class PlayerService @Inject() ()  {
  def playerCharts(history: List[PlayerHistory]): List[PlayerChartEntry] = {
    history.sortBy(h => (h.season, h.round, h.age))
      .map(playerHistory =>
        PlayerChartEntry(age = playerHistory.age,
          salary = playerHistory.salary,
          tsi = playerHistory.tsi,
          rating = playerHistory.rating,
          ratingEndOfMatch = playerHistory.ratingEndOfMatch
        )
      )
  }

  def playerPosition(history: List[PlayerHistory]): String = {
    val sortedHistory = history.sortBy(h => (h.season, h.round)).reverse
    sortedHistory.headOption.map(_.playerSortingKey.teamId)
      .map(lastTeamId =>
        sortedHistory.filter(_.role != "none").takeWhile(_.playerSortingKey.teamId == lastTeamId).take(10)
          .map(_.role).groupBy(identity).map{case (role, list) => (role, list.size)}
          .toList.sortBy(_._2).reverse
          .head._1).getOrElse("")
  }

  def playerSeasonStats(history: List[PlayerHistory]): PlayerSeasonStats = {
    val entries = history.groupBy(_.season).map{case (season, histories) =>
      val leagueGoals = histories.filter(_.matchType == MatchType.LEAGUE_MATCH).map(_.goals).sum
      val cupGoals = histories.filter(_.matchType != MatchType.LEAGUE_MATCH).map(_.goals).sum
      PlayerSeasonStatsEntry(
        season = season,
        leagueGoals = leagueGoals,
        cupGoals = cupGoals,
        allGoals = leagueGoals + cupGoals,
        yellowCards = histories.map(_.yellowCards).sum,
        redCards = histories.map(_.redCards).sum,
        matches = histories.size,
        playedMinutes = histories.map(_.playedMinutes).sum
      )
    }.toList.sortBy(_.season)

    PlayerSeasonStats(
      entries = entries,
      totalLeagueGoals = entries.map(_.leagueGoals).sum,
      totalCupGoals = entries.map(_.cupGoals).sum,
      totalAllGoals = entries.map(_.allGoals).sum,
      totalYellowCards = entries.map(_.yellowCards).sum,
      totalRedCard = entries.map(_.redCards).sum,
      totalMatches = entries.map(_.matches).sum,
      totalPlayedMinutes = entries.map(_.playedMinutes).sum
    )
  }

  def playerLeagueUnitHistory(history: List[PlayerHistory]): List[PlayerLeagueUnitEntry] = {
    val sortedHistory = history.sortBy(h => (h.season, h.round, h.age))

    val result = mutable.Buffer[PlayerLeagueUnitEntry]()

    for (i <- sortedHistory.indices) {
      if (i != 0 &&
        sortedHistory(i).playerSortingKey.teamId != sortedHistory(i - 1).playerSortingKey.teamId) {
        result.append(PlayerLeagueUnitEntry(
          season = sortedHistory(i).season,
          round = sortedHistory(i).round,
          fromLeagueId = sortedHistory(i - 1).playerSortingKey.teamLeagueId,
          fromLeagueUnitId = sortedHistory(i - 1).playerSortingKey.leagueUnitId.toInt,
          fromLeagueUnitName = sortedHistory(i - 1).playerSortingKey.leagueUnitName,
          fromTeamId = sortedHistory(i - 1).playerSortingKey.teamId,
          fromTeamName = sortedHistory(i - 1).playerSortingKey.teamName,
          toLeagueId = sortedHistory(i).playerSortingKey.teamLeagueId,
          toLeagueUnitId = sortedHistory(i).playerSortingKey.leagueUnitId.toInt,
          toLeagueUnitName = sortedHistory(i).playerSortingKey.leagueUnitName,
          toTeamId = sortedHistory(i).playerSortingKey.teamId,
          toTeamName = sortedHistory(i).playerSortingKey.teamName,
          tsi = sortedHistory(i - 1).tsi,
          salary = sortedHistory(i - 1).salary,
          age = sortedHistory(i).age
        ))
      }
    }

    result.toList.reverse
  }
}
