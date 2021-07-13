package controllers

import databases.dao.RestClickhouseDAO
import databases.requests.matchdetails._
import databases.requests.model.promotions.PromotionWithType
import databases.requests.playerstats.dreamteam.DreamTeamRequest
import databases.requests.playerstats.player._
import databases.requests.playerstats.team.{TeamAgeInjuryRequest, TeamCardsRequest, TeamRatingsRequest, TeamSalaryTSIRequest}
import databases.requests.promotions.PromotionsRequest
import databases.requests.teamdetails.{TeamFanclubFlagsRequest, TeamPowerRatingsRequest, TeamStreakTrophiesRequest}
import databases.requests.{ClickhouseStatisticsRequest, OrderingKeyPath}

import javax.inject.{Inject, Singleton}
import models.web.rest.CountryLevelData
import models.web.rest.LevelData.Rounds
import models.web.{PlayersParameters, RestStatisticsParameters, StatsType}
import play.api.libs.json.{Json, Writes}
import play.api.mvc.ControllerComponents
import service.leagueinfo.{LeagueInfoService, LoadingInfo}
import utils.{CurrencyUtils, Romans}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class RestDivisionLevelData(leagueId: Int,
                                 leagueName: String,
                                 divisionLevel: Int,
                                 divisionLevelName: String,
                                 leagueUnitsNumber: Int,
                                 seasonOffset: Int,
                                 seasonRoundInfo: Seq[(Int, Rounds)],
                                 currency: String,
                                 currencyRate: Double,
                                 loadingInfo: LoadingInfo,
                                 countries: Seq[(Int, String)]) extends CountryLevelData

object RestDivisionLevelData {
  implicit val writes = Json.writes[RestDivisionLevelData]
}

@Singleton
class RestDivisionLevelController @Inject()(val controllerComponents: ControllerComponents,
                                            val leagueInfoService: LeagueInfoService,
                                            implicit val restClickhouseDAO: RestClickhouseDAO) extends RestController {
  def getDivisionLevelData(leagueId: Int, divisionLevel: Int) = Action.async { implicit request =>
    val league = leagueInfoService.leagueInfo(leagueId).league
    val leagueName = league.englishName
    val leagueUnitsNumber = leagueInfoService.leagueNumbersMap(divisionLevel).max
    val seasonRoundInfo = leagueInfoService.leagueInfo.seasonRoundInfo(leagueId)

    val restDivisionLevelData = RestDivisionLevelData(
      leagueId = leagueId,
      leagueName = leagueName,
      divisionLevel = divisionLevel,
      divisionLevelName = Romans(divisionLevel),
      leagueUnitsNumber = leagueUnitsNumber,
      seasonOffset = league.seasonOffset,
      seasonRoundInfo = seasonRoundInfo,
      currency = CurrencyUtils.currencyName(league.country),
      currencyRate = CurrencyUtils.currencyRate(league.country),
      loadingInfo = leagueInfoService.leagueInfo(leagueId).loadingInfo,
      countries = leagueInfoService.idToStringCountryMap)
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

  private def playersRequest[T](plRequest: ClickhousePlayerRequest[T],
                                leagueId: Int,
                                divisionLevel: Int,
                                restStatisticsParameters: RestStatisticsParameters,
                                playersParameters: PlayersParameters)
                               (implicit writes: Writes[T]) = Action.async { implicit request =>
    plRequest.execute(
        OrderingKeyPath(
          leagueId = Some(leagueId),
          divisionLevel = Some(divisionLevel)),
        restStatisticsParameters,
        playersParameters)
      .map(entities => restTableDataJson(entities, restStatisticsParameters.pageSize))
  }

  def teamHatstats(leagueId: Int, divisionLevel: Int, restStatisticsParameters: RestStatisticsParameters) =
    stats(TeamHatstatsRequest, leagueId, divisionLevel, restStatisticsParameters)

  def leagueUnits(leagueId: Int, divisionLevel: Int, restStatisticsParameters: RestStatisticsParameters) =
    stats(LeagueUnitHatstatsRequest, leagueId, divisionLevel, restStatisticsParameters)

  def playerGoalGames(leagueId: Int, divisionLevel: Int,
                      restStatisticsParameters: RestStatisticsParameters, playersParameters: PlayersParameters) =
    playersRequest(PlayerGamesGoalsRequest, leagueId, divisionLevel, restStatisticsParameters, playersParameters)

  def playerCards(leagueId: Int, divisionLevel: Int,
                  restStatisticsParameters: RestStatisticsParameters, playersParameters: PlayersParameters) =
    playersRequest(PlayerCardsRequest, leagueId, divisionLevel, restStatisticsParameters, playersParameters)

  def playerTsiSalary(leagueId: Int, divisionLevel: Int, restStatisticsParameters: RestStatisticsParameters, playersParameters: PlayersParameters) =
    playersRequest(PlayerSalaryTSIRequest, leagueId, divisionLevel, restStatisticsParameters, playersParameters)

  def playerRatings(leagueId: Int, divisionLevel: Int, restStatisticsParameters: RestStatisticsParameters, playersParameters: PlayersParameters) =
    playersRequest(PlayerRatingsRequest, leagueId, divisionLevel, restStatisticsParameters, playersParameters)

  def playerInjuries(leagueId: Int, divisionLevel: Int, restStatisticsParameters: RestStatisticsParameters) =
    stats(PlayerInjuryRequest, leagueId, divisionLevel, restStatisticsParameters)

  def teamSalaryTsi(leagueId: Int, divisionLevel: Int, restStatisticsParameters: RestStatisticsParameters, playedInLastMatch: Boolean) = Action.async { implicit request =>
    TeamSalaryTSIRequest.execute(OrderingKeyPath(
        leagueId = Some(leagueId),
        divisionLevel = Some(divisionLevel)),
        restStatisticsParameters,
        playedInLastMatch)
      .map(entities => restTableDataJson(entities, restStatisticsParameters.pageSize))
  }

  def teamCards(leagueId: Int, divisionLevel: Int, restStatisticsParameters: RestStatisticsParameters) = Action.async { implicit request =>
    TeamCardsRequest.execute(
      OrderingKeyPath(leagueId = Some(leagueId),
        divisionLevel = Some(divisionLevel)),
      restStatisticsParameters)
      .map(entities => restTableDataJson(entities, restStatisticsParameters.pageSize))
  }

  def teamRatings(leagueId: Int, divisionLevel: Int, restStatisticsParameters: RestStatisticsParameters) =
    stats(TeamRatingsRequest, leagueId, divisionLevel, restStatisticsParameters)

  def teamAgeInjuries(leagueId: Int, divisionLevel: Int, restStatisticsParameters: RestStatisticsParameters) =
    stats(TeamAgeInjuryRequest, leagueId, divisionLevel, restStatisticsParameters)

  def teamGoalPoints(leagueId: Int, divisionLevel: Int, restStatisticsParameters: RestStatisticsParameters,
                     playedAllMatches: Boolean) = Action.async { implicit request =>
    TeamGoalPointsRequest.execute(
        OrderingKeyPath(
          leagueId = Some(leagueId),
          divisionLevel = Some(divisionLevel)),
        restStatisticsParameters,
        playedAllMatches,
        leagueInfoService.leagueInfo(leagueId).seasonInfo(restStatisticsParameters.season).roundInfo.size)
      .map(entities => restTableDataJson(entities, restStatisticsParameters.pageSize))
  }

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

  def promotions(leagueId: Int, divisionLevel: Int) = Action.async { implicit request =>
    PromotionsRequest.execute(
        OrderingKeyPath(leagueId = Some(leagueId),
            divisionLevel = Some(divisionLevel)),
          leagueInfoService.leagueInfo.currentSeason(leagueId))
      .map(PromotionWithType.convert)
      .map(result => Ok(Json.toJson(result)))
  }

  def dreamTeam(season: Int, leagueId: Int, divisionLevel: Int, sortBy: String, statsType: StatsType) = Action.async{ implicit request =>
    DreamTeamRequest.execute(
      OrderingKeyPath(season = Some(season), leagueId = Some(leagueId), divisionLevel = Some(divisionLevel)),
        statsType,
        sortBy)
      .map(players => Ok(Json.toJson(players)))
  }
}
