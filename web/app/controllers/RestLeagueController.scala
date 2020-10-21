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
import play.api.mvc.{Action, AnyContent, ControllerComponents}
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
                                      implicit val restClickhouseDAO: RestClickhouseDAO,
                                  val leagueInfoService: LeagueInfoService,
                                  val viewDataFactory: ViewDataFactory,
                                  val hattrick: Hattrick) extends RestController  {

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
      TeamHatstatsRequest.execute(OrderingKeyPath(leagueId = Some(leagueId)), restStatisticsParameters)
        .map(entities => restTableDataJson(entities, restStatisticsParameters.pageSize))
    }

    def leagueUnits(leagueId: Int, restStatisticsParameters: RestStatisticsParameters) = Action.async { implicit request =>
      LeagueUnitHatstatsRequest.execute(OrderingKeyPath(leagueId = Some(leagueId)), restStatisticsParameters)
        .map(entities => restTableDataJson(entities, restStatisticsParameters.pageSize))
    }
}

