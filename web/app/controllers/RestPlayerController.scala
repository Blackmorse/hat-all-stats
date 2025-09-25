package controllers

import chpp.playerdetails.PlayerDetailsRequest
import chpp.playerdetails.models.PlayerDetails
import databases.dao.RestClickhouseDAO
import databases.requests.playerstats.player.PlayerHistoryRequest
import models.web.player.{CurrentPlayerCharacteristics, RestPlayerData, RestPlayerDetails}
import models.web.{HattidError, NotFoundError}
import play.api.libs.json.{JsValue, Json}
import play.api.mvc
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import service.leagueinfo.LeagueInfoService
import service.{ChppService, PlayerService, TranslationsService}
import utils.{CurrencyUtils, Romans}
import webclients.ChppClient
import zio.IO

import scala.concurrent.Future

//TODO execution context!
import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class RestPlayerController @Inject() (val chppClient: ChppClient,
                                      val controllerComponents: ControllerComponents,
                                      val leagueInfoService: LeagueInfoService,
                                      val chppService: ChppService,
                                      val playerService: PlayerService,
                                      val translationsService: TranslationsService,
                                      implicit val restClickhouseDAO: RestClickhouseDAO) extends RestController {


  private def getRestPlayerData(playerDetails: PlayerDetails): IO[HattidError, RestPlayerData] = {
    chppService.getTeamById(playerDetails.player.owningTeam.teamId)
      .map(team => {
        val leagueId = playerDetails.player.owningTeam.leagueId
        val league = leagueInfoService.leagueInfo(leagueId).league
        RestPlayerData(
          playerId = playerDetails.player.playerId,
          firstName = playerDetails.player.firstName,
          lastName = playerDetails.player.lastName,
          leagueId = leagueId,
          leagueName = league.englishName,
          divisionLevel = team.leagueLevelUnit.leagueLevel,
          divisionLevelName = Romans(team.leagueLevelUnit.leagueLevel),
          leagueUnitId = team.leagueLevelUnit.leagueLevelUnitId,
          leagueUnitName = team.leagueLevelUnit.leagueLevelUnitName,
          teamId = playerDetails.player.owningTeam.teamId,
          teamName = playerDetails.player.owningTeam.teamName,
          seasonOffset = league.seasonOffset,
          seasonRoundInfo = leagueInfoService.leagueInfo.seasonRoundInfo(leagueId),
          currency = CurrencyUtils.currencyName(league.country),
          currencyRate = CurrencyUtils.currencyRate(league.country),
          countries = leagueInfoService.idToStringCountryMap,
          loadingInfo = leagueInfoService.leagueInfo(leagueId).loadingInfo,
          translations = translationsService.translationsMap
        )
      })
  }

  def getPlayerData(playerId: Long): Action[JsValue] = asyncZio {
    for {
      playerDetails <- chppService.playerDetails(playerId)
      restPlayerData <- getRestPlayerData(playerDetails)
    } yield restPlayerData
  }

  private def getRestPlayerDetails(playerDetails: PlayerDetails): Future[RestPlayerDetails] = {
    for {
      playerHistoryList <- PlayerHistoryRequest.execute(playerDetails.player.playerId)
      avatarParts <- chppService.getPlayerAvatar(playerDetails.player.owningTeam.teamId.toInt, playerDetails.player.playerId)
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

  def getPlayerHistory(playerId: Long): mvc.Action[AnyContent] = Action.async {
    val playerDetailsFuture = chppClient.execute[PlayerDetails, PlayerDetailsRequest](PlayerDetailsRequest(playerId = playerId))

    playerDetailsFuture.flatMap{
      case Left(chppError) => Future(NotFound(Json.toJson(NotFoundError(
        entityType = NotFoundError.PLAYER,
        entityId = playerId.toString,
        description = s"Player with id $playerId not found. Cause: ${chppError.error}"
      ))))
      case Right(playerDetails) => getRestPlayerDetails(playerDetails).map(pd => Ok(Json.toJson(pd)))
    }
  }
}