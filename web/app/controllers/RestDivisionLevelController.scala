package controllers

import databases.RestClickhouseDAO
import databases.requests.matchdetails.{LeagueUnitHatstatsRequest, MatchSpectatorsRequest, MatchSurprisingRequest, MatchTopHatstatsRequest, TeamGoalPointsRequest, TeamHatstatsRequest}
import databases.requests.playerstats.player._
import databases.requests.playerstats.team.{TeamAgeInjuryRequest, TeamCardsRequest, TeamRatingsRequest, TeamSalaryTSIRequest}
import databases.requests.teamdetails.{TeamFanclubFlagsRequest, TeamPowerRatingsRequest, TeamStreakTrophiesRequest}
import databases.requests.{ClickhouseStatisticsRequest, OrderingKeyPath}
import io.swagger.annotations.Api
import javax.inject.{Inject, Singleton}
import models.web.RestStatisticsParameters
import models.web.rest.LevelData
import models.web.rest.LevelData.Rounds
import play.api.libs.json.{Json, Writes}
import play.api.mvc.ControllerComponents
import service.LeagueInfoService
import utils.Romans

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class RestDivisionLevelData(leagueId: Int,
                                 leagueName: String,
                                 divisionLevel: Int,
                                 divisionLevelName: String,
                                 leagueUnitsNumber: Int,
                                 seasonRoundInfo: Seq[(Int, Rounds)],
                                 currency: String,
                                 currencyRate: Double) extends LevelData

object RestDivisionLevelData {
  implicit val writes = Json.writes[RestDivisionLevelData]
}

@Singleton
@Api(produces = "application/json")
class RestDivisionLevelController @Inject()(val controllerComponents: ControllerComponents,
                                            val leagueInfoService: LeagueInfoService,
                                            implicit val restClickhouseDAO: RestClickhouseDAO) extends RestController {
  def getDivisionLevelData(leagueId: Int, divisionLevel: Int) = Action.async { implicit request =>
    val league = leagueInfoService.leagueInfo(leagueId).league
    val leagueName = league.getEnglishName
    val leagueUnitsNumber = leagueInfoService.leagueNumbersMap(divisionLevel).max
    val seasonRoundInfo = leagueInfoService.leagueInfo.seasonRoundInfo(leagueId)

    val restDivisionLevelData = RestDivisionLevelData(
      leagueId = leagueId,
      leagueName = leagueName,
      divisionLevel = divisionLevel,
      divisionLevelName = Romans(divisionLevel),
      leagueUnitsNumber = leagueUnitsNumber,
      seasonRoundInfo = seasonRoundInfo,
      currency = if (league.getCountry.getCurrencyName == null) "$" else league.getCountry.getCurrencyName,
      currencyRate = if (league.getCountry.getCurrencyRate == null) 10.0d else league.getCountry.getCurrencyRate)
    Future(Ok(Json.toJson(restDivisionLevelData)))
  }

  private def stats[T](chRequest: ClickhouseStatisticsRequest[T],
                       leagueId: Int,
                       divisionLevel: Int,
                       restStatisticsParameters: RestStatisticsParameters)
                      (implicit writes: Writes[T])= Action.async { implicit request =>
    chRequest.execute(
      OrderingKeyPath(
        leagueId = Some(leagueId),
        divisionLevel = Some(divisionLevel)),
      restStatisticsParameters)
      .map(entities => restTableDataJson(entities, restStatisticsParameters.pageSize))
  }

  def teamHatstats(leagueId: Int, divisionLevel: Int, restStatisticsParameters: RestStatisticsParameters) =
    stats(TeamHatstatsRequest, leagueId, divisionLevel, restStatisticsParameters)

  def leagueUnits(leagueId: Int, divisionLevel: Int, restStatisticsParameters: RestStatisticsParameters) =
    stats(LeagueUnitHatstatsRequest, leagueId, divisionLevel, restStatisticsParameters)

  def playerGoalGames(leagueId: Int, divisionLevel: Int, restStatisticsParameters: RestStatisticsParameters) =
    stats(PlayerGamesGoalsRequest, leagueId, divisionLevel, restStatisticsParameters)

  def playerCards(leagueId: Int, divisionLevel: Int, restStatisticsParameters: RestStatisticsParameters) =
    stats(PlayerCardsRequest, leagueId, divisionLevel, restStatisticsParameters)

  def playerTsiSalary(leagueId: Int, divisionLevel: Int, restStatisticsParameters: RestStatisticsParameters) =
    stats(PlayerSalaryTSIRequest, leagueId, divisionLevel, restStatisticsParameters)

  def playerRatings(leagueId: Int, divisionLevel: Int, restStatisticsParameters: RestStatisticsParameters) =
    stats(PlayerRatingsRequest, leagueId, divisionLevel, restStatisticsParameters)

  def playerInjuries(leagueId: Int, divisionLevel: Int, restStatisticsParameters: RestStatisticsParameters) =
    stats(PlayerInjuryRequest, leagueId, divisionLevel, restStatisticsParameters)

  def teamSalaryTsi(leagueId: Int, divisionLevel: Int, restStatisticsParameters: RestStatisticsParameters) =
    stats(TeamSalaryTSIRequest, leagueId, divisionLevel, restStatisticsParameters)

  def teamCards(leagueId: Int, divisionLevel: Int, restStatisticsParameters: RestStatisticsParameters) =
    stats(TeamCardsRequest, leagueId, divisionLevel, restStatisticsParameters)

  def teamRatings(leagueId: Int, divisionLevel: Int, restStatisticsParameters: RestStatisticsParameters) =
    stats(TeamRatingsRequest, leagueId, divisionLevel, restStatisticsParameters)

  def teamAgeInjuries(leagueId: Int, divisionLevel: Int, restStatisticsParameters: RestStatisticsParameters) =
    stats(TeamAgeInjuryRequest, leagueId, divisionLevel, restStatisticsParameters)

  def teamGoalPoints(leagueId: Int, divisionLevel: Int, restStatisticsParameters: RestStatisticsParameters) =
    stats(TeamGoalPointsRequest, leagueId, divisionLevel, restStatisticsParameters)

  def teamPowerRatings(leagueId: Int, divisionLevel: Int, restStatisticsParameters: RestStatisticsParameters) =
    stats(TeamPowerRatingsRequest, leagueId, divisionLevel, restStatisticsParameters)

  def teamFanclubFlags(leagueId: Int, divisionLevel: Int, restStatisticsParameters: RestStatisticsParameters) =
    stats(TeamFanclubFlagsRequest, leagueId, divisionLevel, restStatisticsParameters)

  def teamStreakTrophies(leagueId: Int, divisionLevel: Int, restStatisticsParameters: RestStatisticsParameters) =
    stats(TeamStreakTrophiesRequest, leagueId, divisionLevel, restStatisticsParameters)

  def topMatches(leagueId: Int, divisionLevel: Int, restStatisticsParameters: RestStatisticsParameters) =
    stats(MatchTopHatstatsRequest, leagueId, divisionLevel, restStatisticsParameters)

  def surprisingMatches(leagueId: Int, divisionLevel: Int, restStatisticsParameters: RestStatisticsParameters) =
    stats(MatchSurprisingRequest, leagueId, divisionLevel, restStatisticsParameters)

  def matchSpectators(leagueId: Int, divisionLevel: Int, restStatisticsParameters: RestStatisticsParameters) =
    stats(MatchSpectatorsRequest, leagueId, divisionLevel, restStatisticsParameters)
}
