package controllers

import com.google.inject.{Inject, Singleton}
import databases.RestClickhouseDAO
import databases.requests.OrderingKeyPath
import databases.requests.matchdetails.{LeagueUnitHatstatsRequest, TeamHatstatsRequest}
import hattrick.Hattrick
import io.swagger.annotations.Api
import models.web.rest.LevelData
import models.web.rest.LevelData.Rounds
import models.web.{RestStatisticsParameters, ViewDataFactory}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents}
import service.LeagueInfoService
import utils.Romans

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class RestLeagueData(leagueId: Int,
                          leagueName: String,
                          divisionLevels: Seq[String],
                          seasonRoundInfo: Seq[(Int, Rounds)]) extends LevelData

object RestLeagueData {
  implicit val writes = Json.writes[RestLeagueData]
}

@Singleton
@Api(produces = "application/json")
class RestLeagueController @Inject() (val controllerComponents: ControllerComponents,
                                  val restClickhouseDAO: RestClickhouseDAO,
                                  val leagueInfoService: LeagueInfoService,
                                  val viewDataFactory: ViewDataFactory,
                                  val hattrick: Hattrick) extends BaseController  {

    def getLeagueData(leagueId: Int): Action[AnyContent] =  Action.async { implicit request =>
        val leagueName = leagueInfoService.leagueInfo(leagueId).league.getEnglishName
        val numberOfDivisions = leagueInfoService.leagueInfo(leagueId).league.getNumberOfLevels
        val divisionLevels = (1 to numberOfDivisions).map(Romans(_))
        val seasonRoundInfo = leagueInfoService.leagueInfo.seasonRoundInfo(leagueId)

        val restLeagueData = RestLeagueData(
          leagueId = leagueId,
          leagueName = leagueName,
          divisionLevels = divisionLevels,
          seasonRoundInfo = seasonRoundInfo)
        Future(Ok(Json.toJson(restLeagueData)))
      }


    def teamHatstats(leagueId: Int, restStatisticsParameters: RestStatisticsParameters) = Action.async { implicit request =>
      restClickhouseDAO.executeStatisticsRequest(clickhouseRequest = TeamHatstatsRequest,
        parameters = restStatisticsParameters,
        OrderingKeyPath(leagueId = Some(leagueId))).map(Ok(_))
    }

    def leagueUnits(leagueId: Int, restStatisticsParameters: RestStatisticsParameters) = Action.async { implicit request =>
      restClickhouseDAO.executeStatisticsRequest(clickhouseRequest = LeagueUnitHatstatsRequest,
        parameters = restStatisticsParameters,
        OrderingKeyPath(leagueId = Some(leagueId))).map(Ok(_))
    }
}

