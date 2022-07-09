package controllers

import chpp.playerdetails.PlayerDetailsRequest
import chpp.playerdetails.models.PlayerDetails
import chpp.teamdetails.TeamDetailsRequest
import chpp.teamdetails.models.TeamDetails
import databases.dao.RestClickhouseDAO
import databases.requests.model.player.PlayerHistory
import databases.requests.playerstats.player.PlayerHistoryRequest
import models.web.rest.CountryLevelData
import models.web.rest.LevelData.Rounds
import play.api.libs.json.{Json, OWrites}
import play.api.mvc
import play.api.mvc.{AnyContent, ControllerComponents}
import service.ChppService
import service.leagueinfo.{LeagueInfoService, LoadingInfo}
import utils.{CurrencyUtils, Romans}
import webclients.ChppClient

//TODO execution context!
import scala.concurrent.ExecutionContext.Implicits.global

import javax.inject.{Inject, Singleton}

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
                               salary: Long,
                               tsi: Int,
                              historyList: List[PlayerHistory]
                              )

  object RestPlayerDetails {
    implicit val writes: OWrites[RestPlayerDetails] = Json.writes[RestPlayerDetails]
  }

  def getPlayerHistory(playerId: Long): mvc.Action[AnyContent] = Action.async { implicit request =>
    val playerDetailsFuture = chppClient.execute[PlayerDetails, PlayerDetailsRequest](PlayerDetailsRequest(playerId = playerId))
    val playerHistoryFuture = PlayerHistoryRequest.execute(playerId)
    playerDetailsFuture.zipWith(playerHistoryFuture){ case(playerDetails, playerHistoryList) =>
      val restPlayerDetails = RestPlayerDetails(
        playerId = playerId,
        firstName = playerDetails.player.firstName,
        lastName = playerDetails.player.lastName,
        salary = playerDetails.player.salary,
        tsi = playerDetails.player.tsi,
        historyList = playerHistoryList
      )

      Ok(Json.toJson(restPlayerDetails))
    }
  }
}
