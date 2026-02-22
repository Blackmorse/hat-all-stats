package com.blackmorse.hattid.web.routes

import chpp.commonmodels.MatchType
import chpp.playerdetails.models.PlayerDetails
import com.blackmorse.hattid.web.zios.{CHPPServices, DBServices, HattidEnv}
import com.blackmorse.hattid.web.databases.requests.model.player.PlayerHistory
import com.blackmorse.hattid.web.databases.requests.playerstats.player.PlayerHistoryRequest
import com.blackmorse.hattid.web.models.web.HattidError
import com.blackmorse.hattid.web.models.web.player.*
import com.blackmorse.hattid.web.service.leagueinfo.LeagueInfoServiceZIO
import com.blackmorse.hattid.web.service.{ChppService, TranslationsService}
import com.blackmorse.hattid.web.utils.{CurrencyUtils, Romans}
import zio.*
import zio.http.*
import zio.json.*

import scala.collection.mutable

object PlayerRoutes {
  private val GetPlayer = Method.GET / "api" / "player" / long("playerId")

  val routes: Seq[Route[HattidEnv, HattidError]] = Seq(
    GetPlayer -> playerDataHandler,
    GetPlayer / "playerDetails" -> playerDetailsHandler,
  )
  
  private def playerDetailsHandler = handler { (playerId: Long, req: Request) =>
    for {
      chppService <- ZIO.service[ChppService]
      playerDetails <- chppService.playerDetails(playerId)
      restPlayerDetails <- getRestPlayerDetails(playerDetails)
    } yield Response.json(restPlayerDetails.toJson)
  }

  private def getRestPlayerDetails(playerDetails: PlayerDetails): ZIO[CHPPServices & DBServices, HattidError, RestPlayerDetails] = {
    for {
      chppService <- ZIO.service[ChppService]
      playerHistoryList <- PlayerHistoryRequest.execute(playerDetails.player.playerId)
      avatarParts <- chppService.getPlayerAvatar(playerDetails.player.owningTeam.teamId.toInt, playerDetails.player.playerId)
    } yield RestPlayerDetails(
      playerId = playerDetails.player.playerId,
      firstName = playerDetails.player.firstName,
      lastName = playerDetails.player.lastName,
      currentPlayerCharacteristics = CurrentPlayerCharacteristics(
        position = playerPosition(playerHistoryList),
        salary = playerDetails.player.salary,
        tsi = playerDetails.player.tsi,
        age = playerDetails.player.age * 112 + playerDetails.player.ageDays,
        form = playerDetails.player.playerForm,
        injuryLevel = playerDetails.player.injuryLevel,
        experience = playerDetails.player.experience,
        leaderShip = playerDetails.player.leaderShip,
        speciality = playerDetails.player.specialty
      ),
      nativeLeagueId = playerDetails.player.nativeLeagueId,
      playerLeagueUnitHistory = playerLeagueUnitHistory(playerHistoryList),
      avatar = avatarParts,
      playerSeasonStats = playerSeasonStats(playerHistoryList),
      playerCharts = playerCharts(playerHistoryList)
    )
  }

  private def playerDataHandler = handler { (playerId: Long, req: Request) =>
    for {
      chppService    <- ZIO.service[ChppService]
      playerDetails  <- chppService.playerDetails(playerId)
      restPlayerData <- getRestPlayerData(playerDetails)
    } yield Response.json(restPlayerData.toJson)
  }

  private def getRestPlayerData(playerDetails: PlayerDetails): ZIO[CHPPServices & LeagueInfoServiceZIO & TranslationsService, HattidError, RestPlayerData] = {
    val leagueId = playerDetails.player.owningTeam.leagueId
    for {
      leagueInfoService <- ZIO.service[LeagueInfoServiceZIO]
      chppService <- ZIO.service[ChppService]
      translationsService <- ZIO.service[TranslationsService]
      leagueState <- leagueInfoService.leagueState(leagueId)
      (team, _) <- chppService.getTeamById(playerDetails.player.owningTeam.teamId)
    } yield RestPlayerData(
      playerId = playerDetails.player.playerId,
      firstName = playerDetails.player.firstName,
      lastName = playerDetails.player.lastName,
      leagueId = leagueId,
      leagueName = leagueState.league.englishName,
      divisionLevel = team.leagueLevelUnit.leagueLevel,
      divisionLevelName = Romans(team.leagueLevelUnit.leagueLevel),
      leagueUnitId = team.leagueLevelUnit.leagueLevelUnitId,
      leagueUnitName = team.leagueLevelUnit.leagueLevelUnitName,
      teamId = playerDetails.player.owningTeam.teamId,
      teamName = playerDetails.player.owningTeam.teamName,
      seasonOffset = leagueState.league.seasonOffset,
      seasonRoundInfo = leagueState.seasonRoundInfo,
      currency = CurrencyUtils.currencyName(leagueState.league.country),
      currencyRate = CurrencyUtils.currencyRate(leagueState.league.country),
      countries = leagueState.idToCountryName,
      loadingInfo = leagueState.loadingInfo,
      translations = translationsService.translations
    )
  }

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
      .flatMap(lastTeamId =>
        sortedHistory
          .filter(_.role != "none")
          .takeWhile(_.playerSortingKey.teamId == lastTeamId)
          .take(10)
          .map(_.role)
          .groupBy(identity)
          .map { case (role, list) => (role, list.size) }
          .toList
          .sortBy(_._2)
          .reverse
          .headOption
          .map(_._1))
      .getOrElse("")
  }

  private def playerSeasonStats(history: List[PlayerHistory]): PlayerSeasonStats = {
    val entries = history.groupBy(_.season).map { case (season, histories) =>
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

  private def playerLeagueUnitHistory(history: List[PlayerHistory]): List[PlayerLeagueUnitEntry] = {
    val sortedHistory = history.sortBy(h => (h.season, h.round, h.age))

    val result = mutable.Buffer[PlayerLeagueUnitEntry]()

    for (i <- sortedHistory.indices) {
      if (i != 0 &&
        sortedHistory(i).playerSortingKey.teamId != sortedHistory(i - 1).playerSortingKey.teamId) {
        result.append(PlayerLeagueUnitEntry(
          season = sortedHistory(i).season,
          round = sortedHistory(i).round,
          fromLeagueId = sortedHistory(i - 1).playerSortingKey.leagueId,
          fromLeagueUnitId = sortedHistory(i - 1).playerSortingKey.leagueUnitId.toInt,
          fromLeagueUnitName = sortedHistory(i - 1).playerSortingKey.leagueUnitName,
          fromTeamId = sortedHistory(i - 1).playerSortingKey.teamId,
          fromTeamName = sortedHistory(i - 1).playerSortingKey.teamName,
          toLeagueId = sortedHistory(i).playerSortingKey.leagueId,
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