package controllers

import javax.inject.Inject
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

class RestDivisionLevelController @Inject()(val controllerComponents: ControllerComponents,
                                            val leagueInfoService: LeagueInfoService) extends BaseController{
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
}
