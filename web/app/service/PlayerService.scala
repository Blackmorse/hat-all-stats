package service

import chpp.commonmodels.MatchType
import databases.requests.model.player.PlayerHistory
import play.api.libs.json.{Json, OWrites}

import javax.inject.{Inject, Singleton}
import scala.collection.mutable

case class PlayerLeagueUnitEntry(season: Int,
                                 round: Int,
                                 fromLeagueId: Int,
                                 fromLeagueUnitId: Int,
                                 fromLeagueUnitName: String,
                                 fromTeamId: Long,
                                 fromTeamName: String,
                                 toLeagueId: Int,
                                 toLeagueUnitId: Int,
                                 toLeagueUnitName: String,
                                 toTeamId: Long,
                                 toTeamName: String,
                                 tsi: Int,
                                 salary: Int,
                                 age: Int)

object PlayerLeagueUnitEntry {
  implicit val writes: OWrites[PlayerLeagueUnitEntry] = Json.writes[PlayerLeagueUnitEntry]
}

case class PlayerSeasonStats(season: Int,
                             leagueGoals: Int,
                             cupGoals: Int,
                             allGoals: Int,
                             yellowCards: Int,
                             redCards: Int,
                             matches: Int,
                             playedMinutes: Int)

object PlayerSeasonStats {
  implicit val writes: OWrites[PlayerSeasonStats] = Json.writes[PlayerSeasonStats]
}

@Singleton
class PlayerService @Inject() ()  {
  def playerPosition(history: List[PlayerHistory]): String = {
    val sortedHistory = history.sortBy(h => (h.season, h.round)).reverse
    val lastTeamId = sortedHistory.head.playerSortingKey.teamId
    sortedHistory.takeWhile(_.playerSortingKey.teamId == lastTeamId).take(10)
      .map(_.role).groupBy(identity).map{case (role, list) => (role, list.size)}
      .toList.sortBy(_._2).reverse
      .head._1
  }

  def playerSeasonStats(history: List[PlayerHistory]): List[PlayerSeasonStats] = {
    history.groupBy(_.season).map{case (season, histories) =>
      val leagueGoals = histories.filter(_.matchType == MatchType.LEAGUE_MATCH).map(_.goals).sum
      val cupGoals = histories.filter(_.matchType != MatchType.LEAGUE_MATCH).map(_.goals).sum
      PlayerSeasonStats(
        season = season,
        leagueGoals = leagueGoals,
        cupGoals = cupGoals,
        allGoals = leagueGoals + cupGoals,
        yellowCards = histories.map(_.yellowCards).sum,
        redCards = histories.map(_.redCards).sum,
        matches = histories.size,
        playedMinutes = histories.map(_.playedMinutes).sum
      )
    }.toList
  }

  def playerLeagueUnitHistory(history: List[PlayerHistory]): List[PlayerLeagueUnitEntry] = {
    val sortedHistory = history.sortBy(h => (h.season, h.round))

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
