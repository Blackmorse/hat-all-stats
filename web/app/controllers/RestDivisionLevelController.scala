package controllers

import databases.RestClickhouseDAO
import databases.requests.OrderingKeyPath
import databases.requests.matchdetails.{LeagueUnitHatstatsRequest, TeamHatstatsRequest}
import io.swagger.annotations.Api
import javax.inject.{Inject, Singleton}
import models.web.rest.LevelData.Rounds
import models.web.rest.LevelData
import models.web.RestStatisticsParameters
import play.api.libs.json.Json
import play.api.mvc.{BaseController, ControllerComponents}
import service.LeagueInfoService
import utils.Romans

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class RestDivisionLevelData(leagueId: Int,
                                 leagueName: String,
                                 divisionLevel: Int,
                                 divisionLevelName: String,
                                 leagueUnitsNumber: Int,
                                 seasonRoundInfo: Seq[(Int, Rounds)]) extends LevelData

object RestDivisionLevelData {
  implicit val writes = Json.writes[RestDivisionLevelData]
}

@Singleton
@Api(produces = "application/json")
class RestDivisionLevelController @Inject()(val controllerComponents: ControllerComponents,
                                            val leagueInfoService: LeagueInfoService,
                                            val restClickhouseDAO: RestClickhouseDAO) extends BaseController{
  def getDivisionLevelData(leagueId: Int, divisionLevel: Int) = Action.async { implicit request =>
    val leagueName = leagueInfoService.leagueInfo(leagueId).league.getEnglishName
    val leagueUnitsNumber = leagueInfoService.leagueNumbersMap(divisionLevel).max
    val seasonRoundInfo = leagueInfoService.leagueInfo.seasonRoundInfo(leagueId)

    val restDivisionLevelData = RestDivisionLevelData(
      leagueId = leagueId,
      leagueName = leagueName,
      divisionLevel = divisionLevel,
      divisionLevelName = Romans(divisionLevel),
      leagueUnitsNumber = leagueUnitsNumber,
      seasonRoundInfo = seasonRoundInfo)
    Future(Ok(Json.toJson(restDivisionLevelData)))
  }

  def teamHatstats(leagueId: Int, divisionLevel: Int, restStatisticsParameters: RestStatisticsParameters) = Action.async{implicit request =>
    restClickhouseDAO.executeStatisticsRequest(clickhouseRequest = TeamHatstatsRequest,
      parameters = restStatisticsParameters,
      OrderingKeyPath(leagueId = Some(leagueId),
        divisionLevel = Some(divisionLevel)))
        .map(Ok(_))
  }

  def leagueUnits(leagueId: Int, divisionLevel: Int, restStatisticsParameters: RestStatisticsParameters) = Action.async { implicit request =>
    restClickhouseDAO.executeStatisticsRequest(clickhouseRequest = LeagueUnitHatstatsRequest,
      parameters = restStatisticsParameters,
      OrderingKeyPath(leagueId = Some(leagueId),
        divisionLevel = Some(divisionLevel)))
        .map(Ok(_))
  }
}
