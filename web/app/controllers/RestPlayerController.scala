package controllers

import cache.ZioCacheModule.HattidEnv
import chpp.AuthConfig
import chpp.playerdetails.models.PlayerDetails
import databases.dao.RestClickhouseDAO
import databases.requests.playerstats.player.PlayerHistoryRequest
import models.web.HattidError
import models.web.player.{CurrentPlayerCharacteristics, RestPlayerData, RestPlayerDetails}
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import service.leagueinfo.LeagueInfoServiceZIO
import service.{ChppService, PlayerService, TranslationsService}
import utils.{CurrencyUtils, Romans}
import zio.ZIO

//TODO execution context!
import javax.inject.{Inject, Singleton}

@Singleton
class RestPlayerController @Inject() (val controllerComponents: ControllerComponents,
                                      val playerService: PlayerService,
                                      val hattidEnvironment: zio.ZEnvironment[HattidEnv]) extends RestController(hattidEnvironment) {

  private def getRestPlayerData(playerDetails: PlayerDetails): ZIO[AuthConfig & ChppService & LeagueInfoServiceZIO & TranslationsService, HattidError, RestPlayerData] = {
    val leagueId = playerDetails.player.owningTeam.leagueId
    for {
      leagueInfoService   <- ZIO.service[LeagueInfoServiceZIO]
      chppService         <- ZIO.service[ChppService]
      translationsService <- ZIO.service[TranslationsService]
      leagueState         <- leagueInfoService.leagueState(leagueId)
      (team, _)           <- chppService.getTeamById(playerDetails.player.owningTeam.teamId)
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

  def getPlayerData(playerId: Long): Action[AnyContent] = asyncZio {
    for {
      chppService    <- ZIO.service[ChppService]
      playerDetails  <- chppService.playerDetails(playerId)
      restPlayerData <- getRestPlayerData(playerDetails)
    } yield restPlayerData
  }

  private def getRestPlayerDetails(playerDetails: PlayerDetails): ZIO[AuthConfig & RestClickhouseDAO & ChppService, HattidError, RestPlayerDetails] = {
    for {
      chppService       <- ZIO.service[ChppService]
      playerHistoryList <- PlayerHistoryRequest.execute(playerDetails.player.playerId)
      avatarParts       <- chppService.getPlayerAvatar(playerDetails.player.owningTeam.teamId.toInt, playerDetails.player.playerId)
    } yield RestPlayerDetails(
        playerId = playerDetails.player.playerId,
        firstName = playerDetails.player.firstName,
        lastName = playerDetails.player.lastName,
        currentPlayerCharacteristics = CurrentPlayerCharacteristics(
          position = playerService.playerPosition(playerHistoryList),
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
        playerLeagueUnitHistory = playerService.playerLeagueUnitHistory(playerHistoryList),
        avatar = avatarParts,
        playerSeasonStats = playerService.playerSeasonStats(playerHistoryList),
        playerCharts = playerService.playerCharts(playerHistoryList)
      )
  }

  def getPlayerHistory(playerId: Long): Action[AnyContent] = asyncZio {
    for {
      chppService       <- ZIO.service[ChppService]
      playerDetails     <- chppService.playerDetails(playerId)
      restPlayerDetails <- getRestPlayerDetails(playerDetails)
    } yield restPlayerDetails
  }
}