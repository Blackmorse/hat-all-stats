package controllers

import com.blackmorse.hattrick.model.enums.SearchType
import databases.ClickhouseDAO
import databases.clickhouse.StatisticsCHRequest
import hattrick.Hattrick
import io.swagger.annotations.Api
import javax.inject.Inject
import models.web.rest.LevelData
import models.web.rest.LevelData.Rounds
import models.web.{RestStatisticsParameters, RestTableData, Round, StatisticsParameters}
import play.api.libs.json.Json
import play.api.mvc.{BaseController, ControllerComponents}
import service.{LeagueInfoService, LeagueUnitCalculatorService}
import utils.{LeagueNameParser, Romans}

import collection.JavaConverters._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import com.blackmorse.hattrick.common.CommonData.higherLeagueMap

case class RestLeagueUnitData(leagueId: Int,
                              leagueName: String,
                              divisionLevel: Int,
                              divisionLevelName: String,
                              leagueUnitId: Long,
                              leagueUnitName: String,
                              teams: Seq[(Long, String)],
                              seasonRoundInfo: Seq[(Int, Rounds)]) extends LevelData

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
    Future(findLeagueUnitIdByName(leagueUnitName, leagueId))
      .map(id => Ok(Json.toJson(LongWrapper(id))))
  }

  private def findLeagueUnitIdByName(leagueUnitName: String, leagueId: Int) = {
    if(leagueUnitName == "I.1") {
      higherLeagueMap.get(leagueId).getLeagueUnitId
    } else {
      if(leagueId == 1) { //Sweden
        val (division, number) = LeagueNameParser.getLeagueUnitNumberByName(leagueUnitName)
        val actualDivision = Romans(Romans(division) - 1)
        val actualNumber = if (division == "II" || division == "III") {
          ('a' + number - 1).toChar.toString
        } else {
          "." + number.toString
        }
        hattrick.api.search()
          .searchLeagueId(leagueId).searchString(actualDivision + actualNumber).searchType(SearchType.SERIES)
          .execute()
          .getSearchResults.get(0).getResultId
      } else {
        hattrick.api.search()
          .searchLeagueId(leagueId).searchString(leagueUnitName).searchType(SearchType.SERIES)
          .execute()
          .getSearchResults.get(0).getResultId
      }
    }
  }

  private def leagueUnitDataFromId(leagueUnitId: Long): Future[RestLeagueUnitData] =
    Future(hattrick.api.leagueDetails().leagueLevelUnitId(leagueUnitId).execute())
    .map(leagueDetails =>
      RestLeagueUnitData(leagueId = leagueDetails.getLeagueId,
        leagueName = leagueInfoService.leagueInfo(leagueDetails.getLeagueId).league.getEnglishName,
        divisionLevel = leagueDetails.getLeagueLevel,
        divisionLevelName = Romans(leagueDetails.getLeagueLevel),
        leagueUnitId = leagueUnitId,
        leagueUnitName = leagueDetails.getLeagueLevelUnitName,
        teams = leagueDetails.getTeams.asScala.map(team => (team.getTeamId.toLong, team.getTeamName)),
        seasonRoundInfo = leagueInfoService.leagueInfo.seasonRoundInfo(leagueDetails.getLeagueId))
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
