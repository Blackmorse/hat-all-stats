package controllers

import com.blackmorse.hattrick.model.enums.SearchType
import databases.ClickhouseDAO
import databases.clickhouse.StatisticsCHRequest
import hattrick.Hattrick
import io.swagger.annotations.Api
import javax.inject.Inject
import models.web.{RestStatisticsParameters, RestTableData, Round, StatisticsParameters}
import play.api.libs.json.Json
import play.api.mvc.{BaseController, ControllerComponents}
import service.{LeagueInfoService, LeagueUnitCalculatorService}
import utils.Romans

import collection.JavaConverters._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class LeagueUnitData()

case class RestLeagueUnitData(leagueId: Int,
                              leagueName: String,
                              divisionLevel: Int,
                              divisionLevelName: String,
                              leagueUnitId: Long,
                              leagueUnitName: String,
                              currentRound: Int,
                              rounds: Seq[Int],
                              currentSeason: Int,
                              seasons: Seq[Int],
                              teams: Seq[(Long, String)])

object RestLeagueUnitData {
  implicit val writes = Json.writes[RestLeagueUnitData]
}

@Api(produces = "application/json")
class RestLeagueUnitController @Inject() (val controllerComponents: ControllerComponents,
                                          val leagueInfoService: LeagueInfoService,
                                          val hattrick: Hattrick,
                                          implicit val clickhouseDAO: ClickhouseDAO,
                                          val leagueUnitCalculatorService: LeagueUnitCalculatorService) extends BaseController {
  case class LongWrapper(id: Long)
  implicit val writes = Json.writes[LongWrapper]

  def leagueUnitIdByName(leagueUnitName: String, leagueId: Int) = Action.async{implicit request =>
    Future(hattrick.api.search().searchLeagueId(leagueId).searchString(leagueUnitName).searchType(SearchType.SERIES).execute())
      .map(result => result.getSearchResults.get(0).getResultId)
      .map(id => Ok(Json.toJson(LongWrapper(id))))
  }

  private def leagueUnitDataFromId(leagueUnitId: Long) =
    Future(hattrick.api.leagueDetails().leagueLevelUnitId(leagueUnitId).execute())
    .map(leagueDetails =>
      RestLeagueUnitData(leagueId = leagueDetails.getLeagueId,
        leagueName = leagueInfoService.leagueInfo(leagueDetails.getLeagueId).league.getEnglishName,
        divisionLevel = leagueDetails.getLeagueLevel,
        divisionLevelName = Romans(leagueDetails.getLeagueLevel),
        leagueUnitId = leagueUnitId,
        leagueUnitName = leagueDetails.getLeagueLevelUnitName,
        currentRound = leagueInfoService.leagueInfo.currentRound(leagueDetails.getLeagueId),
        rounds = leagueInfoService.leagueInfo.rounds(leagueDetails.getLeagueId, leagueInfoService.leagueInfo.currentSeason(leagueDetails.getLeagueId)),
        currentSeason = leagueInfoService.leagueInfo.currentSeason(leagueDetails.getLeagueId),
        seasons = leagueInfoService.leagueInfo.seasons(leagueDetails.getLeagueId),
        teams = leagueDetails.getTeams.asScala.map(team => (team.getTeamId.toLong, team.getTeamName)))
      )

  def getLeagueUnitData(leagueUnitId: Long) = Action.async {implicit request =>
    leagueUnitDataFromId(leagueUnitId)
      .map(rlud =>  Ok(Json.toJson(rlud)))
  }

  def teamHatstats(leagueUnitId: Long, restStatisticsParameters: RestStatisticsParameters) = Action.async{ implicit request =>
    val statisticsParameters =
      StatisticsParameters(season = restStatisticsParameters.season,
        page = restStatisticsParameters.page,
        statsType = restStatisticsParameters.statsType,
        sortBy = restStatisticsParameters.sortBy,
        pageSize = restStatisticsParameters.pageSize,
        sortingDirection = restStatisticsParameters.sortingDirection
      )

    leagueUnitDataFromId(leagueUnitId).flatMap(leagueUnitData =>

      StatisticsCHRequest.bestHatstatsTeamRequest.execute(
        leagueId = Some(leagueUnitData.leagueId),
        divisionLevel = Some(leagueUnitData.divisionLevel),
        leagueUnitId = Some(leagueUnitData.leagueUnitId),
        statisticsParameters = statisticsParameters
      )
    ).map(teamRatings => {
      val isLastPage = teamRatings.size <= statisticsParameters.pageSize

      val entities = if(!isLastPage) teamRatings.dropRight(1) else teamRatings
      val restTableData = RestTableData(entities, isLastPage)
      Ok(Json.toJson(restTableData))
    })
  }

  def teamPositions(leagueUnitId: Long, restStatisticsParameters: RestStatisticsParameters) = Action.async{implicit request =>
    Future{
      val leagueDetails = hattrick.api.leagueDetails().leagueLevelUnitId(leagueUnitId).execute()

      val offsettedSeason = leagueInfoService.getRelativeSeasonFromAbsolute(restStatisticsParameters.season, leagueDetails.getLeagueId)

      val leagueFixture = hattrick.api.leagueFixtures().season(offsettedSeason).leagueLevelUnitId(leagueUnitId).execute()

      val round = restStatisticsParameters.statsType.asInstanceOf[Round].round

      val teams = leagueUnitCalculatorService.calculate(leagueFixture, Some(round),
        restStatisticsParameters.sortBy, restStatisticsParameters.sortingDirection)

      Ok(Json.toJson(RestTableData(teams, true)))
    }
  }
}
