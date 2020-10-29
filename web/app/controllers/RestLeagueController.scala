package controllers

import com.google.inject.{Inject, Singleton}
import databases.RestClickhouseDAO
import databases.requests.{ClickhouseStatisticsRequest, OrderingKeyPath}
import databases.requests.matchdetails.{LeagueUnitHatstatsRequest, TeamGoalPointsRequest, TeamHatstatsRequest}
import databases.requests.playerstats.player.{PlayerCardsRequest, PlayerGamesGoalsRequest, PlayerInjuryRequest, PlayerRatingsRequest, PlayerSalaryTSIRequest}
import databases.requests.playerstats.team.{TeamAgeInjuryRequest, TeamCardsRequest, TeamRatingsRequest, TeamSalaryTSIRequest}
import databases.requests.teamdetails.{TeamFanclubFlagsRequest, TeamPowerRatingsRequest, TeamStreakTrophiesRequest}
import hattrick.Hattrick
import io.swagger.annotations.Api
import models.web.rest.LevelData
import models.web.rest.LevelData.Rounds
import models.web.{RestStatisticsParameters, ViewDataFactory}
import play.api.libs.json.{Json, Writes}
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import service.LeagueInfoService
import utils.Romans

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class RestLeagueData(leagueId: Int,
                          leagueName: String,
                          divisionLevels: Seq[String],
                          seasonRoundInfo: Seq[(Int, Rounds)],
                          currency: String,
                          currencyRate: Double) extends LevelData

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
      val league = leagueInfoService.leagueInfo(leagueId).league
      val leagueName = league.getEnglishName
      val numberOfDivisions = league.getNumberOfLevels
      val divisionLevels = (1 to numberOfDivisions).map(Romans(_))
      val seasonRoundInfo = leagueInfoService.leagueInfo.seasonRoundInfo(leagueId)

      val restLeagueData = RestLeagueData(
        leagueId = leagueId,
        leagueName = leagueName,
        divisionLevels = divisionLevels,
        seasonRoundInfo = seasonRoundInfo,
        currency = if (league.getCountry.getCurrencyName == null) "$" else league.getCountry.getCurrencyName,
        currencyRate = if (league.getCountry.getCurrencyRate == null) 10.0d else league.getCountry.getCurrencyRate)
      Future(Ok(Json.toJson(restLeagueData)))
    }

  private def stats[T](chRequest: ClickhouseStatisticsRequest[T],
                       leagueId: Int,
                       restStatisticsParameters: RestStatisticsParameters)
                      (implicit writes: Writes[T])= Action.async { implicit request =>
    chRequest.execute(OrderingKeyPath(leagueId = Some(leagueId)), restStatisticsParameters)
      .map(entities => restTableDataJson(entities, restStatisticsParameters.pageSize))
  }

  def teamHatstats(leagueId: Int, restStatisticsParameters: RestStatisticsParameters) =
    stats(TeamHatstatsRequest, leagueId, restStatisticsParameters)

  def leagueUnits(leagueId: Int, restStatisticsParameters: RestStatisticsParameters) =
    stats(LeagueUnitHatstatsRequest, leagueId, restStatisticsParameters)

  def playerGoalGames(leagueId: Int, restStatisticsParameters: RestStatisticsParameters) =
    stats(PlayerGamesGoalsRequest, leagueId, restStatisticsParameters)

  def playerCards(leagueId: Int, restStatisticsParameters: RestStatisticsParameters) =
    stats(PlayerCardsRequest, leagueId, restStatisticsParameters)

  def playerTsiSalary(leagueId: Int, restStatisticsParameters: RestStatisticsParameters) =
    stats(PlayerSalaryTSIRequest, leagueId, restStatisticsParameters)

  def playerRatings(leagueId: Int, restStatisticsParameters: RestStatisticsParameters) =
    stats(PlayerRatingsRequest, leagueId, restStatisticsParameters)

  def playerInjuries(leagueId: Int, restStatisticsParameters: RestStatisticsParameters) =
    stats(PlayerInjuryRequest, leagueId, restStatisticsParameters)

  def teamSalaryTsi(leagueId: Int, restStatisticsParameters: RestStatisticsParameters) =
    stats(TeamSalaryTSIRequest, leagueId, restStatisticsParameters)

  def teamCards(leagueId: Int, restStatisticsParameters: RestStatisticsParameters) =
    stats(TeamCardsRequest, leagueId, restStatisticsParameters)

  def teamRatings(leagueId: Int, restStatisticsParameters: RestStatisticsParameters) =
    stats(TeamRatingsRequest, leagueId, restStatisticsParameters)

  def teamAgeInjuries(leagueId: Int, restStatisticsParameters: RestStatisticsParameters) =
    stats(TeamAgeInjuryRequest, leagueId, restStatisticsParameters)

  def teamGoalPoints(leagueId: Int, restStatisticsParameters: RestStatisticsParameters) =
    stats(TeamGoalPointsRequest, leagueId, restStatisticsParameters)

  def teamPowerRatings(leagueId: Int, restStatisticsParameters: RestStatisticsParameters) =
    stats(TeamPowerRatingsRequest, leagueId, restStatisticsParameters)

  def teamFanclubFlags(leagueId: Int, restStatisticsParameters: RestStatisticsParameters) =
    stats(TeamFanclubFlagsRequest, leagueId, restStatisticsParameters)

  def teamStreakTrophies(leagueId: Int, restStatisticsParameters: RestStatisticsParameters) =
    stats(TeamStreakTrophiesRequest, leagueId, restStatisticsParameters)
}

