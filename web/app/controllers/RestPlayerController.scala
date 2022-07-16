package controllers

import chpp.playerdetails.PlayerDetailsRequest
import chpp.playerdetails.models.PlayerDetails
import databases.dao.RestClickhouseDAO
import databases.requests.playerstats.player.PlayerHistoryRequest
import models.web.rest.CountryLevelData
import models.web.rest.LevelData.Rounds
import play.api.libs.json.{Json, OWrites}
import play.api.mvc
import play.api.mvc.{AnyContent, ControllerComponents}
import service.leagueinfo.{LeagueInfoService, LoadingInfo}
import service.{ChppService, PlayerLeagueUnitEntry, PlayerSeasonStats, PlayerService}
import utils.{CurrencyUtils, Romans}
import webclients.ChppClient

//TODO execution context!
import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext.Implicits.global

case class RestPlayerData(playerId: Long,
                          firstName: String,
                          lastName: String,
                          leagueId: Int,
                          leagueName: String,
                          divisionLevel: Int,
                          divisionLevelName: String,
                          leagueUnitId: Int,
                          leagueUnitName: String,
                          teamId: Long,
                          teamName: String,
                          seasonOffset: Int,
                          seasonRoundInfo: Seq[(Int, Rounds)],
                          currency: String,
                          currencyRate: Double,
                          countries: Seq[(Int, String)],
                          loadingInfo: LoadingInfo) extends CountryLevelData

object RestPlayerData {
  implicit val writes: OWrites[RestPlayerData] = Json.writes[RestPlayerData]
}

@Singleton
class RestPlayerController @Inject() (val chppClient: ChppClient,
                                       val controllerComponents: ControllerComponents,
                                     val leagueInfoService: LeagueInfoService,
                                      val chppService: ChppService,
                                      val playerService: PlayerService,
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
        loadingInfo = leagueInfoService.leagueInfo(leagueId).loadingInfo
      )))
    }
  }

  case class RestPlayerDetails(playerId: Long,
                               firstName: String,
                               lastName: String,
                               currentPlayerCharacteristics: CurrentPlayerCharacteristics,
                               nativeLeagueId: Int,
                               playerLeagueUnitHistory: List[PlayerLeagueUnitEntry],
                               avatar: Seq[AvatarPart],
                               playerSeasonStats: List[PlayerSeasonStats]
                              )

  case class CurrentPlayerCharacteristics(position: String,
                                           salary: Long,
                                           tsi: Int,
                                           age: Int,
                                           form: Int,
                                           injuryLevel: Int,
                                           experience: Int,
                                           leaderShip: Int,
                                           speciality: Int)

  object CurrentPlayerCharacteristics {
    implicit val writes: OWrites[CurrentPlayerCharacteristics] = Json.writes[CurrentPlayerCharacteristics]
  }

  object RestPlayerDetails {
    implicit val writes: OWrites[RestPlayerDetails] = Json.writes[RestPlayerDetails]
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
       playerSeasonStats = playerService.playerSeasonStats(playerHistoryList)
     )

     Ok(Json.toJson(restPlayerDetails))
   }
  }
}

case class AvatarPart(url: String, x: Int, y: Int)

object AvatarPart {
  implicit val writes: OWrites[AvatarPart] = Json.writes[AvatarPart]
}