package controllers

import chpp.playerdetails.PlayerDetailsRequest
import chpp.playerdetails.models.PlayerDetails
import databases.dao.RestClickhouseDAO
import databases.requests.playerstats.player.PlayerHistoryRequest
import models.web.player.{CurrentPlayerCharacteristics, RestPlayerData, RestPlayerDetails}
import play.api.libs.json.Json
import play.api.mvc
import play.api.mvc.{AnyContent, ControllerComponents}
import service.leagueinfo.LeagueInfoService
import service.{ChppService, PlayerService, TranslationsService}
import utils.{CurrencyUtils, Romans}
import webclients.ChppClient

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

  def getPlayerData(playerId: Long): mvc.Action[AnyContent] = Action.async { implicit request =>
    for {
      playerDetails <- chppService.playerDetails(playerId)
      teamDetailsEither <- chppService.getTeamById(playerDetails.player.owningTeam.teamId)
    } yield {
      val leagueId = playerDetails.player.owningTeam.leagueId
      val league = leagueInfoService.leagueInfo(leagueId).league
      val teamDetails = teamDetailsEither match {
        //Can't be Left!! I hope...
        case Right(teamDetails) => teamDetails
        case Left(teamDetails) => teamDetails
      }
      Ok(Json.toJson(RestPlayerData(
        playerId = playerId,
        firstName = playerDetails.player.firstName,
        lastName = playerDetails.player.lastName,
        leagueId = leagueId,
        leagueName = league.englishName,
        divisionLevel = teamDetails.leagueLevelUnit.leagueLevel,
        divisionLevelName = Romans(teamDetails.leagueLevelUnit.leagueLevel),
        leagueUnitId = teamDetails.leagueLevelUnit.leagueLevelUnitId,
        leagueUnitName = teamDetails.leagueLevelUnit.leagueLevelUnitName,
        teamId = playerDetails.player.owningTeam.teamId,
        teamName = playerDetails.player.owningTeam.teamName,
        seasonOffset = league.seasonOffset,
        seasonRoundInfo = leagueInfoService.leagueInfo.seasonRoundInfo(leagueId),
        currency = CurrencyUtils.currencyName(league.country),
        currencyRate = CurrencyUtils.currencyRate(league.country),
        countries = leagueInfoService.idToStringCountryMap,
        loadingInfo = leagueInfoService.leagueInfo(leagueId).loadingInfo,
        translations = translationsService.translationsMap
      )))
    }
  }

  def getPlayerHistory(playerId: Long): mvc.Action[AnyContent] = Action.async { implicit request =>
    val playerDetailsFuture = chppClient.execute[PlayerDetails, PlayerDetailsRequest](PlayerDetailsRequest(playerId = playerId))
    val playerHistoryFuture = PlayerHistoryRequest.execute(playerId)

   for {
     (playerDetails, playerHistoryList) <- playerDetailsFuture.zip(playerHistoryFuture)
     avatarParts <- chppService.getPlayerAvatar(playerDetails.player.owningTeam.teamId.toInt, playerId)
   } yield {
     val restPlayerDetails = RestPlayerDetails(
       playerId = playerId,
       firstName = playerDetails.player.firstName,
       lastName = playerDetails.player.lastName,
       currentPlayerCharacteristics = CurrentPlayerCharacteristics(
         position = playerService.playerPosition(playerHistoryList),
         salary = playerDetails.player.salary,
         tsi = playerDetails.player.tsi,
         age = playerDetails.player.age * 112 + playerDetails.player.age,
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

     Ok(Json.toJson(restPlayerDetails))
   }
  }
}