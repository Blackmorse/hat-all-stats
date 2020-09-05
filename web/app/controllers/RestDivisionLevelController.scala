package controllers

import databases.ClickhouseDAO
import databases.clickhouse.StatisticsCHRequest
import io.swagger.annotations.Api
import javax.inject.{Inject, Singleton}
import models.web.{RestStatisticsParameters, RestTableData, StatisticsParameters}
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
                                 currentRound: Int,
                                 rounds: Seq[Int],
                                 currentSeason: Int,
                                 seasons: Seq[Int])

object RestDivisionLevelData {
  implicit val writes = Json.writes[RestDivisionLevelData]
}

@Singleton
@Api(produces = "application/json")
class RestDivisionLevelController @Inject()(val controllerComponents: ControllerComponents,
                                            val leagueInfoService: LeagueInfoService,
                                            implicit val clickhouseDAO: ClickhouseDAO) extends BaseController{
  def getDivisionLevelData(leagueId: Int, divisionLevel: Int) = Action.async { implicit request =>
    val leagueName = leagueInfoService.leagueInfo(leagueId).league.getEnglishName
    val leagueUnitsNumber = leagueInfoService.leagueNumbersMap(divisionLevel).max
    val currentRound = leagueInfoService.leagueInfo.currentRound(leagueId)
    val rounds = leagueInfoService.leagueInfo.rounds(leagueId, leagueInfoService.leagueInfo.currentSeason(leagueId))
    val currentSeason = leagueInfoService.leagueInfo.currentSeason(leagueId)
    val seasons = leagueInfoService.leagueInfo.seasons(leagueId)

    Future(Ok(Json.toJson(RestDivisionLevelData(leagueId, leagueName, divisionLevel, Romans(divisionLevel),
      leagueUnitsNumber, currentRound, rounds, currentSeason, seasons))))
  }

  def teamHatstats(leagueId: Int, divisionLevel: Int, restStatisticsParameters: RestStatisticsParameters) = Action.async{implicit request =>
        val statisticsParameters =
          StatisticsParameters(season = restStatisticsParameters.season,
            page = restStatisticsParameters.page,
            statsType = restStatisticsParameters.statsType,
            sortBy = restStatisticsParameters.sortBy,
            pageSize = restStatisticsParameters.pageSize,
            sortingDirection = restStatisticsParameters.sortingDirection
          )

      StatisticsCHRequest.bestHatstatsTeamRequest.execute(
        leagueId = Some(leagueId),
        divisionLevel = Some(divisionLevel),
        statisticsParameters = statisticsParameters
      ).map(teamRatings => {
        val isLastPage = teamRatings.size <= statisticsParameters.pageSize

        val entities = if(!isLastPage) teamRatings.dropRight(1) else teamRatings
        val restTableData = RestTableData(entities, isLastPage)
        Ok(Json.toJson(restTableData))
      })
  }

  def leagueUnits(leagueId: Int, divisionLevel: Int, restStatisticsParameters: RestStatisticsParameters) = Action.async { implicit request =>
    val statisticsParameters =
      StatisticsParameters(season = restStatisticsParameters.season,
        page = restStatisticsParameters.page,
        statsType = restStatisticsParameters.statsType,
        sortBy = restStatisticsParameters.sortBy,
        pageSize = restStatisticsParameters.pageSize,
        sortingDirection = restStatisticsParameters.sortingDirection
      )

    StatisticsCHRequest.bestHatstatsLeagueRequest.execute(
      leagueId = Some(leagueId),
      divisionLevel = Some(divisionLevel),
      statisticsParameters = statisticsParameters
    ).map(leagueUnits => {
      val isLastPage = leagueUnits.size <= statisticsParameters.pageSize

      val entities = if(!isLastPage) leagueUnits.dropRight(1) else leagueUnits
      val restTableData = RestTableData(entities, isLastPage)
      Ok(Json.toJson(restTableData))
    })
  }
}
