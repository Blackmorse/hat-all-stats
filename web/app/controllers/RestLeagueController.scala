package controllers

import com.google.inject.{Inject, Singleton}
import databases.dao.RestClickhouseDAO
import databases.requests.matchdetails._
import databases.requests.model.promotions.PromotionWithType
import databases.requests.playerstats.dreamteam.DreamTeamRequest
import databases.requests.playerstats.player._
import databases.requests.playerstats.team.{TeamAgeInjuryRequest, TeamCardsRequest, TeamRatingsRequest, TeamSalaryTSIRequest}
import databases.requests.promotions.PromotionsRequest
import databases.requests.teamdetails.{TeamFanclubFlagsRequest, TeamPowerRatingsRequest, TeamStreakTrophiesRequest}
import databases.requests.{ClickhouseStatisticsRequest, OrderingKeyPath}
import hattrick.Hattrick
import models.web.rest.CountryLevelData
import models.web.rest.LevelData.Rounds
import models.web.{PlayersParameters, RestStatisticsParameters, StatsType}
import play.api.libs.json.{Json, Writes}
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import service.leagueinfo.{LeagueInfoService, LoadingInfo}
import utils.Romans

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class RestLeagueData(leagueId: Int,
                          leagueName: String,
                          divisionLevels: Seq[String],
                          seasonOffset: Int,
                          seasonRoundInfo: Seq[(Int, Rounds)],
                          currency: String,
                          currencyRate: Double,
                          loadingInfo: LoadingInfo,
                          countries: Seq[(Int, String)]) extends CountryLevelData

object RestLeagueData {
  implicit val writes = Json.writes[RestLeagueData]
}

@Singleton
class RestLeagueController @Inject() (val controllerComponents: ControllerComponents,
                                      implicit val restClickhouseDAO: RestClickhouseDAO,
                                  val leagueInfoService: LeagueInfoService) extends RestController  {

  def getLeagueData(leagueId: Int): Action[AnyContent] =  Action.async { implicit request =>
      val league = leagueInfoService.leagueInfo(leagueId).league
      val leagueName = league.getEnglishName
      val numberOfDivisions = league.getNumberOfLevels
      val divisionLevels = (1 to numberOfDivisions).map(Romans(_))
      val seasonOffset = leagueInfoService.leagueInfo(leagueId).league.getSeasonOffset
      val seasonRoundInfo = leagueInfoService.leagueInfo.seasonRoundInfo(leagueId)

      val restLeagueData = RestLeagueData(
        leagueId = leagueId,
        leagueName = leagueName,
        divisionLevels = divisionLevels,
        seasonOffset = seasonOffset,
        seasonRoundInfo = seasonRoundInfo,
        currency = if (league.getCountry.getCurrencyName == null) "$" else league.getCountry.getCurrencyName,
        currencyRate = if (league.getCountry.getCurrencyRate == null) 10.0d else league.getCountry.getCurrencyRate,
        loadingInfo = leagueInfoService.leagueInfo(leagueId).loadingInfo,
        countries = leagueInfoService.idToStringCountryMap)
      Future(Ok(Json.toJson(restLeagueData)))
    }

  private def stats[T](chRequest: ClickhouseStatisticsRequest[T],
                       leagueId: Int,
                       restStatisticsParameters: RestStatisticsParameters)
                      (implicit writes: Writes[T]) = Action.async { implicit request =>
    chRequest.execute(OrderingKeyPath(leagueId = Some(leagueId)), restStatisticsParameters)
      .map(entities => restTableDataJson(entities, restStatisticsParameters.pageSize))
  }

  private def playersRequest[T](plRequest: ClickhousePlayerRequest[T],
                                leagueId: Int,
                                restStatisticsParameters: RestStatisticsParameters,
                                playersParameters: PlayersParameters)(implicit writes: Writes[T])  = Action.async { implicit request =>
    plRequest.execute(
        OrderingKeyPath(leagueId = Some(leagueId)),
        restStatisticsParameters,
        playersParameters)
      .map(entities => restTableDataJson(entities, restStatisticsParameters.pageSize))
  }

  def teamHatstats(leagueId: Int, restStatisticsParameters: RestStatisticsParameters) =
    stats(TeamHatstatsRequest, leagueId, restStatisticsParameters)

  def leagueUnits(leagueId: Int, restStatisticsParameters: RestStatisticsParameters) =
    stats(LeagueUnitHatstatsRequest, leagueId, restStatisticsParameters)

  def playerGoalGames(leagueId: Int, restStatisticsParameters: RestStatisticsParameters, playersParameters: PlayersParameters) =
    playersRequest(PlayerGamesGoalsRequest, leagueId, restStatisticsParameters, playersParameters)

  def playerCards(leagueId: Int, restStatisticsParameters: RestStatisticsParameters, playersParameters: PlayersParameters) =
    playersRequest(PlayerCardsRequest, leagueId, restStatisticsParameters, playersParameters)

  def playerTsiSalary(leagueId: Int, restStatisticsParameters: RestStatisticsParameters, playersParameters: PlayersParameters) =
    playersRequest(PlayerSalaryTSIRequest, leagueId, restStatisticsParameters, playersParameters)

  def playerRatings(leagueId: Int, restStatisticsParameters: RestStatisticsParameters, playersParameters: PlayersParameters) =
    playersRequest(PlayerRatingsRequest, leagueId, restStatisticsParameters, playersParameters)

  def playerInjuries(leagueId: Int, restStatisticsParameters: RestStatisticsParameters) =
    stats(PlayerInjuryRequest, leagueId, restStatisticsParameters)

  def teamSalaryTsi(leagueId: Int, restStatisticsParameters: RestStatisticsParameters, playedInLastMatch: Boolean) = Action.async { implicit request =>
    TeamSalaryTSIRequest.execute(OrderingKeyPath(leagueId = Some(leagueId)),
        restStatisticsParameters,
        playedInLastMatch)
      .map(entities => restTableDataJson(entities, restStatisticsParameters.pageSize))
  }

  def teamCards(leagueId: Int, restStatisticsParameters: RestStatisticsParameters) = Action.async { implicit request =>
    TeamCardsRequest.execute(
      OrderingKeyPath(leagueId = Some(leagueId)),
      restStatisticsParameters)
      .map(entities => restTableDataJson(entities, restStatisticsParameters.pageSize))
  }

  def teamRatings(leagueId: Int, restStatisticsParameters: RestStatisticsParameters) =
    stats(TeamRatingsRequest, leagueId, restStatisticsParameters)

  def teamAgeInjuries(leagueId: Int, restStatisticsParameters: RestStatisticsParameters) =
    stats(TeamAgeInjuryRequest, leagueId, restStatisticsParameters)

  def teamGoalPoints(leagueId: Int, restStatisticsParameters: RestStatisticsParameters,
                     playedAllMatches: Boolean) = Action.async { implicit request =>
    TeamGoalPointsRequest.execute(OrderingKeyPath(leagueId = Some(leagueId)),
          restStatisticsParameters,
          playedAllMatches,
          leagueInfoService.leagueInfo(leagueId).seasonInfo(restStatisticsParameters.season).roundInfo.size
          )
      .map(entities => restTableDataJson(entities, restStatisticsParameters.pageSize))
  }

  def teamPowerRatings(leagueId: Int, restStatisticsParameters: RestStatisticsParameters) =
    stats(TeamPowerRatingsRequest, leagueId, restStatisticsParameters)

  def teamFanclubFlags(leagueId: Int, restStatisticsParameters: RestStatisticsParameters) =
    stats(TeamFanclubFlagsRequest, leagueId, restStatisticsParameters)

  def teamStreakTrophies(leagueId: Int, restStatisticsParameters: RestStatisticsParameters) =
    stats(TeamStreakTrophiesRequest, leagueId, restStatisticsParameters)

  def topMatches(leagueId: Int, restStatisticsParameters: RestStatisticsParameters) =
    stats(MatchTopHatstatsRequest, leagueId, restStatisticsParameters)

  def surprisingMatches(leagueId: Int, restStatisticsParameters: RestStatisticsParameters) =
    stats(MatchSurprisingRequest, leagueId, restStatisticsParameters)

  def matchSpectators(leagueId: Int, restStatisticsParameters: RestStatisticsParameters) =
    stats(MatchSpectatorsRequest, leagueId, restStatisticsParameters)

  def promotions(leagueId: Int) = Action.async { implicit request =>
    PromotionsRequest.execute(OrderingKeyPath(leagueId = Some(leagueId)), leagueInfoService.leagueInfo.currentSeason(leagueId))
      .map(PromotionWithType.convert).map(result => Ok(Json.toJson(result)))
  }

  def dreamTeam(season: Int, leagueId: Int, sortBy: String, statsType: StatsType) = Action.async { implicit request =>
    DreamTeamRequest.execute(OrderingKeyPath(season = Some(season), leagueId = Some(leagueId)),
      statsType,
      sortBy)
      .map(players => Ok(Json.toJson(players)))
  }
}

