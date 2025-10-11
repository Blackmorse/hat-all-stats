package controllers

import cache.ZioCacheModule.HattidEnv
import chpp.worlddetails.models.League
import databases.requests.matchdetails.*
import databases.requests.model.promotions.PromotionWithType
import databases.requests.playerstats.dreamteam.DreamTeamRequest
import databases.requests.playerstats.player.stats.*
import databases.requests.playerstats.team.{TeamAgeInjuryRequest, TeamCardsRequest, TeamRatingsRequest, TeamSalaryTSIRequest}
import databases.requests.promotions.PromotionsRequest
import databases.requests.teamdetails.{OldestTeamsRequest, TeamFanclubFlagsRequest, TeamPowerRatingsRequest, TeamStreakTrophiesRequest}
import databases.requests.{ClickhouseStatisticsRequest, OrderingKeyPath}
import models.web.rest.CountryLevelData
import models.web.rest.LevelData.Rounds
import models.web.{PlayersParameters, RestStatisticsParameters, StatsType}
import play.api.libs.json.{JsValue, Json, OWrites, Writes}
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import service.leagueinfo.{LeagueInfoServiceZIO, LeagueState, LoadingInfo}
import utils.{CurrencyUtils, Romans}
import zio.ZIO

import javax.inject.{Inject, Singleton}

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
  implicit val writes: OWrites[RestDivisionLevelData] = Json.writes[RestDivisionLevelData]
}

@Singleton
class RestDivisionLevelController @Inject()(val controllerComponents: ControllerComponents,
                                            val hattidEnvironment: zio.ZEnvironment[HattidEnv]) extends RestController(hattidEnvironment) {
  def getDivisionLevelData(leagueId: Int, divisionLevel: Int): Action[AnyContent] = asyncZio {
    for {
      leagueInfoService <- ZIO.service[LeagueInfoServiceZIO]
      leagueState       <- leagueInfoService.leagueState(leagueId)
    } yield createRestDivisionLevelData(leagueState, divisionLevel)
  }

  private def createRestDivisionLevelData(leagueState: LeagueState,
                                           divisionLevel: Int): RestDivisionLevelData = {
    RestDivisionLevelData(
      leagueId = leagueState.league.leagueId,
      leagueName = leagueState.league.englishName,
      divisionLevel = divisionLevel,
      divisionLevelName = Romans(divisionLevel),
      leagueUnitsNumber = LeagueInfoServiceZIO.leagueNumbersMap(divisionLevel).max,
      seasonOffset = leagueState.league.seasonOffset,
      seasonRoundInfo = leagueState.seasonRoundInfo,
      currency = CurrencyUtils.currencyName(leagueState.league.country),
      currencyRate = CurrencyUtils.currencyRate(leagueState.league.country),
      loadingInfo = leagueState.loadingInfo,
      countries = leagueState.idToCountryName)
  }

  private def stats[T: Writes](chRequest: ClickhouseStatisticsRequest[T],
                               leagueId: Int,
                               divisionLevel: Int,
                               restStatisticsParameters: RestStatisticsParameters): Action[AnyContent] = asyncZio {
    chRequest.execute(
        orderingKeyPath = OrderingKeyPath(
          leagueId = Some(leagueId),
          divisionLevel = Some(divisionLevel)),
        parameters = restStatisticsParameters)
      .map(entities => restTableData(entities, restStatisticsParameters.pageSize))
  }

  private def playersRequest[T : Writes](plRequest: ClickhousePlayerStatsRequest[T],
                                leagueId: Int,
                                divisionLevel: Int,
                                restStatisticsParameters: RestStatisticsParameters,
                                playersParameters: PlayersParameters) = asyncZio {
    plRequest.execute(
        OrderingKeyPath(
          leagueId = Some(leagueId),
          divisionLevel = Some(divisionLevel)),
        restStatisticsParameters,
        playersParameters)
      .map(entities => restTableData(entities, restStatisticsParameters.pageSize))
  }

  def teamHatstats(leagueId: Int, divisionLevel: Int, restStatisticsParameters: RestStatisticsParameters): Action[AnyContent] =
    stats(TeamHatstatsRequest, leagueId, divisionLevel, restStatisticsParameters)

  def leagueUnits(leagueId: Int, divisionLevel: Int, restStatisticsParameters: RestStatisticsParameters): Action[AnyContent] =
    stats(LeagueUnitHatstatsRequest, leagueId, divisionLevel, restStatisticsParameters)

  def playerGoalGames(leagueId: Int, divisionLevel: Int,
                      restStatisticsParameters: RestStatisticsParameters, playersParameters: PlayersParameters): Action[AnyContent] =
    playersRequest(PlayerGamesGoalsRequest, leagueId, divisionLevel, restStatisticsParameters, playersParameters)

  def playerCards(leagueId: Int, divisionLevel: Int,
                  restStatisticsParameters: RestStatisticsParameters, playersParameters: PlayersParameters): Action[AnyContent] =
    playersRequest(PlayerCardsRequest, leagueId, divisionLevel, restStatisticsParameters, playersParameters)

  def playerTsiSalary(leagueId: Int, divisionLevel: Int, restStatisticsParameters: RestStatisticsParameters, playersParameters: PlayersParameters): Action[AnyContent] =
    playersRequest(PlayerSalaryTSIRequest, leagueId, divisionLevel, restStatisticsParameters, playersParameters)

  def playerRatings(leagueId: Int, divisionLevel: Int, restStatisticsParameters: RestStatisticsParameters, playersParameters: PlayersParameters): Action[AnyContent] =
    playersRequest(PlayerRatingsRequest, leagueId, divisionLevel, restStatisticsParameters, playersParameters)

  def playerInjuries(leagueId: Int, divisionLevel: Int, restStatisticsParameters: RestStatisticsParameters): Action[AnyContent] =
    stats(PlayerInjuryRequest, leagueId, divisionLevel, restStatisticsParameters)

  def teamSalaryTsi(leagueId: Int, divisionLevel: Int, restStatisticsParameters: RestStatisticsParameters, playedInLastMatch: Boolean, excludeZeroTsi: Boolean): Action[AnyContent] = asyncZio {
    TeamSalaryTSIRequest.execute(
        orderingKeyPath = OrderingKeyPath(
          leagueId = Some(leagueId),
          divisionLevel = Some(divisionLevel)),
        parameters = restStatisticsParameters,
        playedInLastMatch = playedInLastMatch,
        excludeZeroTsi = excludeZeroTsi)
      .map(entities => restTableData(entities, restStatisticsParameters.pageSize))
  }

  def teamCards(leagueId: Int, divisionLevel: Int, restStatisticsParameters: RestStatisticsParameters): Action[AnyContent] = asyncZio {
    TeamCardsRequest.execute(
      orderingKeyPath = OrderingKeyPath(
        leagueId = Some(leagueId),
        divisionLevel = Some(divisionLevel)),
      parameters = restStatisticsParameters)
      .map(entities => restTableData(entities, restStatisticsParameters.pageSize))
  }

  def teamRatings(leagueId: Int, divisionLevel: Int, restStatisticsParameters: RestStatisticsParameters): Action[AnyContent] =
    stats(TeamRatingsRequest, leagueId, divisionLevel, restStatisticsParameters)

  def teamAgeInjuries(leagueId: Int, divisionLevel: Int, restStatisticsParameters: RestStatisticsParameters): Action[AnyContent] =
    stats(TeamAgeInjuryRequest, leagueId, divisionLevel, restStatisticsParameters)

  def teamGoalPoints(leagueId: Int, divisionLevel: Int, restStatisticsParameters: RestStatisticsParameters,
                     playedAllMatches: Boolean, oneTeamPerUnit: Boolean): Action[AnyContent] = asyncZio {
    for {
      leagueInfoService <- ZIO.service[LeagueInfoServiceZIO]
      currentRound      <- leagueInfoService.leagueRoundForSeason(leagueId, restStatisticsParameters.season)
      entities          <- TeamGoalPointsRequest.execute(
        orderingKeyPath = OrderingKeyPath(
          leagueId = Some(leagueId),
          divisionLevel = Some(divisionLevel)),
        parameters = restStatisticsParameters,
        playedAllMatches = playedAllMatches,
        currentRound = currentRound,
        oneTeamPerUnit = oneTeamPerUnit)
    } yield restTableData(entities, restStatisticsParameters.pageSize)
  }

  def teamPowerRatings(leagueId: Int, divisionLevel: Int, restStatisticsParameters: RestStatisticsParameters): Action[AnyContent] =
    stats(TeamPowerRatingsRequest, leagueId, divisionLevel, restStatisticsParameters)

  def teamFanclubFlags(leagueId: Int, divisionLevel: Int, restStatisticsParameters: RestStatisticsParameters): Action[AnyContent] =
    stats(TeamFanclubFlagsRequest, leagueId, divisionLevel, restStatisticsParameters)

  def teamStreakTrophies(leagueId: Int, divisionLevel: Int, restStatisticsParameters: RestStatisticsParameters): Action[AnyContent] =
    stats(TeamStreakTrophiesRequest, leagueId, divisionLevel, restStatisticsParameters)

  def topMatches(leagueId: Int, divisionLevel: Int, restStatisticsParameters: RestStatisticsParameters): Action[AnyContent] =
    stats(MatchTopHatstatsRequest, leagueId, divisionLevel, restStatisticsParameters)

  def surprisingMatches(leagueId: Int, divisionLevel: Int, restStatisticsParameters: RestStatisticsParameters): Action[AnyContent] =
    stats(MatchSurprisingRequest, leagueId, divisionLevel, restStatisticsParameters)

  def matchSpectators(leagueId: Int, divisionLevel: Int, restStatisticsParameters: RestStatisticsParameters): Action[AnyContent] =
    stats(MatchSpectatorsRequest, leagueId, divisionLevel, restStatisticsParameters)

  def oldestTeams(leagueId: Int, divisionLevel: Int, restStatisticsParameters: RestStatisticsParameters): Action[AnyContent] =
    stats(OldestTeamsRequest, leagueId, divisionLevel, restStatisticsParameters)

  def promotions(leagueId: Int, divisionLevel: Int): Action[AnyContent] = asyncZio {
    for {
      leagueInfoService <- ZIO.service[LeagueInfoServiceZIO]
      currentSeason     <- leagueInfoService.currentSeason(leagueId)
      entities          <- PromotionsRequest.execute(
                            orderingKeyPath = OrderingKeyPath(leagueId = Some(leagueId),
                              divisionLevel = Some(divisionLevel)),
                            season = currentSeason)
    } yield PromotionWithType.convert(entities)
  }

  def dreamTeam(season: Int, leagueId: Int, divisionLevel: Int, sortBy: String, statsType: StatsType): Action[AnyContent] = asyncZio {
    DreamTeamRequest.execute(
      orderingKeyPath = OrderingKeyPath(season = Some(season),
        leagueId = Some(leagueId),
        divisionLevel = Some(divisionLevel)),
      statsType = statsType,
      sortBy = sortBy)
  }
}
