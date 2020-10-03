package controllers

import com.blackmorse.hattrick.api.teamdetails.model.Team
import databases.ClickhouseDAO
import databases.clickhouse.StatisticsCHRequest
import hattrick.Hattrick
import io.swagger.annotations.Api
import javax.inject.Inject
import models.web.{RestStatisticsParameters, RestTableData, StatisticsParameters}
import models.web.rest.LevelData
import models.web.rest.LevelData.Rounds
import play.api.libs.json.Json
import play.api.mvc.{BaseController, ControllerComponents}
import service.LeagueInfoService
import utils.Romans

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import collection.JavaConverters._

case class RestTeamData(leagueId: Int,
                        leagueName: String,
                        divisionLevel: Int,
                        divisionLevelName: String,
                        leagueUnitId: Long,
                        leagueUnitName: String,
                        teamId: Long,
                        teamName: String,
                        seasonRoundInfo: Seq[(Int, Rounds)]) extends LevelData

object RestTeamData {
  implicit val writes = Json.writes[RestTeamData]
}

@Api(produces = "application/json")
class RestTeamController @Inject() (val controllerComponents: ControllerComponents,
                                    val hattrick: Hattrick,
                                    val leagueInfoService: LeagueInfoService,
                                    implicit val clickhouseDAO: ClickhouseDAO) extends BaseController {

  private def getTeamById(teamId: Long): Future[Team] = Future {
    hattrick.api.teamDetails().teamID(teamId).execute()
      .getTeams.asScala.filter(_.getTeamId == teamId).head
  }

  def getTeamData(teamId: Long) = Action.async {
    getTeamById(teamId)
      .map(team => {
        Ok(Json.toJson(
          RestTeamData(
            leagueId = team.getLeague.getLeagueId,
            leagueName = leagueInfoService.leagueInfo(team.getLeague.getLeagueId).league.getEnglishName,
            divisionLevel = team.getLeagueLevelUnit.getLeagueLevel,
            divisionLevelName = Romans(team.getLeagueLevelUnit.getLeagueLevel),
            leagueUnitId = team.getLeagueLevelUnit.getLeagueLevelUnitId,
            leagueUnitName = team.getLeagueLevelUnit.getLeagueLevelUnitName,
            teamId = teamId,
            teamName = team.getTeamName,
            seasonRoundInfo = leagueInfoService.leagueInfo.seasonRoundInfo(team.getLeague.getLeagueId)
          )
        ))
      })
  }

  def playerStats(teamId: Long, restStatisticsParameters: RestStatisticsParameters) = Action.async { implicit request =>
    val statisticsParameters =
      StatisticsParameters(season = restStatisticsParameters.season,
        page = restStatisticsParameters.page,
        statsType = restStatisticsParameters.statsType,
        sortBy = restStatisticsParameters.sortBy,
        pageSize = restStatisticsParameters.pageSize,
        sortingDirection = restStatisticsParameters.sortingDirection
      )

    getTeamById(teamId).flatMap(team => {
      val htRound = hattrick.api.worldDetails().leagueId(team.getLeague.getLeagueId)
        .execute()
        .getLeagueList.get(0).getMatchRound

      val (divisionLevel: Int, leagueUnitId: Long) = if(htRound == 16 ||
                leagueInfoService.leagueInfo.currentSeason(team.getLeague.getLeagueId) > statisticsParameters.season) {
        val infoOpt = clickhouseDAO.historyTeamLeagueUnitInfo(statisticsParameters.season, team.getLeague.getLeagueId, teamId)
        infoOpt.map(info => (info.divisionLevel, info.leagueUnitId))
          .getOrElse((team.getLeagueLevelUnit.getLeagueLevel, team.getLeagueLevelUnit.getLeagueLevelUnitId))
      } else {
        (team.getLeagueLevelUnit.getLeagueLevel.toInt, team.getLeagueLevelUnit.getLeagueLevelUnitId.toLong)
      }

      StatisticsCHRequest.playerStatsRequest.execute(
        leagueId = Some(team.getLeague.getLeagueId),
        divisionLevel = Some(divisionLevel),
        leagueUnitId = Some(leagueUnitId),
        teamId = Some(teamId),
        statisticsParameters = statisticsParameters
      ).map(playerStats => {
        val isLastPage = playerStats.size <= statisticsParameters.pageSize

        val entities = if(!isLastPage) playerStats.dropRight(1) else playerStats
        val restTableData = RestTableData(entities, isLastPage)
        Ok(Json.toJson(restTableData))
      })
    })
  }
}
