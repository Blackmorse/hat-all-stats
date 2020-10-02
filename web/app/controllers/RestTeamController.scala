package controllers

import hattrick.Hattrick
import io.swagger.annotations.Api
import javax.inject.Inject
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
                                    val leagueInfoService: LeagueInfoService) extends BaseController {

  def getTeamData(teamId: Long) = Action.async {
    Future(hattrick.api.teamDetails().teamID(teamId).execute())
      .map(teamDetails => {
        if(teamDetails.getUser.getUserId == 0L) {
          NotFound("")
        } else {
          val team = teamDetails.getTeams.asScala.filter(_.getTeamId == teamId).head
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
        }
      })
  }
}
