package controllers

import databases.dao.RestClickhouseDAO
import databases.requests.matchdetails.*
import databases.requests.model.promotions.PromotionWithType
import databases.requests.playerstats.dreamteam.DreamTeamRequest
import databases.requests.playerstats.player.stats.{ClickhousePlayerStatsRequest, PlayerCardsRequest, PlayerGamesGoalsRequest, PlayerInjuryRequest, PlayerRatingsRequest, PlayerSalaryTSIRequest}
import databases.requests.playerstats.team.{TeamAgeInjuryRequest, TeamCardsRequest, TeamRatingsRequest, TeamSalaryTSIRequest}
import databases.requests.promotions.PromotionsRequest
import databases.requests.teamdetails.{OldestTeamsRequest, TeamFanclubFlagsRequest, TeamPowerRatingsRequest, TeamStreakTrophiesRequest}
import databases.requests.{ClickhouseStatisticsRequest, OrderingKeyPath}
import hattid.CommonData

import javax.inject.{Inject, Singleton}
import models.web.rest.CountryLevelData
import models.web.rest.LevelData.Rounds
import models.web.{NotFoundError, PlayersParameters, RestStatisticsParameters, StatsType}
import play.api.libs.json.{JsValue, Json, OWrites, Writes}
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import service.leagueinfo.{LeagueInfo, LeagueInfoService, LoadingInfo}
import utils.{CurrencyUtils, Romans}
import zio.ZLayer

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
                                            val leagueInfoService: LeagueInfoService,
                                            val restClickhouseDAO: RestClickhouseDAO) extends RestController {
  def getDivisionLevelData(leagueId: Int, divisionLevel: Int): Action[AnyContent] = Action { implicit request =>
    leagueInfoService.leagueInfo.get(leagueId)
      .map(leagueInfo => createRestDivisionLevelData(leagueInfo, divisionLevel))
      .map(restData => Ok(Json.toJson(restData)))
      .getOrElse(NotFound(Json.toJson(NotFoundError(
        entityType = NotFoundError.DIVISION_LEVEL,
        entityId = s"$leagueId-${CommonData.arabToRomans.getOrElse(divisionLevel, divisionLevel)}",
        description = ""
      ))))
  }

  private def createRestDivisionLevelData(leagueInfo: LeagueInfo, divisionLevel: Int): RestDivisionLevelData = {
    val leagueUnitsNumber = leagueInfoService.leagueNumbersMap(divisionLevel).max
    val seasonRoundInfo = leagueInfoService.leagueInfo.seasonRoundInfo(leagueInfo.league.leagueId)

    RestDivisionLevelData(
      leagueId = leagueInfo.league.leagueId,
      leagueName = leagueInfo.league.englishName,
      divisionLevel = divisionLevel,
      divisionLevelName = Romans(divisionLevel),
      leagueUnitsNumber = leagueUnitsNumber,
      seasonOffset = leagueInfo.league.seasonOffset,
      seasonRoundInfo = seasonRoundInfo,
      currency = CurrencyUtils.currencyName(leagueInfo.league.country),
      currencyRate = CurrencyUtils.currencyRate(leagueInfo.league.country),
      loadingInfo = leagueInfoService.leagueInfo(leagueInfo.league.leagueId).loadingInfo,
      countries = leagueInfoService.idToStringCountryMap)
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
      .provide(ZLayer.succeed(restClickhouseDAO))
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
      .provide(ZLayer.succeed(restClickhouseDAO))
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
      .provide(ZLayer.succeed(restClickhouseDAO))
  }

  def teamCards(leagueId: Int, divisionLevel: Int, restStatisticsParameters: RestStatisticsParameters): Action[AnyContent] = asyncZio {
    TeamCardsRequest.execute(
      orderingKeyPath = OrderingKeyPath(
        leagueId = Some(leagueId),
        divisionLevel = Some(divisionLevel)),
      parameters = restStatisticsParameters)
      .map(entities => restTableData(entities, restStatisticsParameters.pageSize))
      .provide(ZLayer.succeed(restClickhouseDAO))
  }

  def teamRatings(leagueId: Int, divisionLevel: Int, restStatisticsParameters: RestStatisticsParameters): Action[AnyContent] =
    stats(TeamRatingsRequest, leagueId, divisionLevel, restStatisticsParameters)

  def teamAgeInjuries(leagueId: Int, divisionLevel: Int, restStatisticsParameters: RestStatisticsParameters): Action[AnyContent] =
    stats(TeamAgeInjuryRequest, leagueId, divisionLevel, restStatisticsParameters)

  def teamGoalPoints(leagueId: Int, divisionLevel: Int, restStatisticsParameters: RestStatisticsParameters,
                     playedAllMatches: Boolean, oneTeamPerUnit: Boolean): Action[AnyContent] = asyncZio {
    TeamGoalPointsRequest.execute(
        orderingKeyPath = OrderingKeyPath(
          leagueId = Some(leagueId),
          divisionLevel = Some(divisionLevel)),
        parameters = restStatisticsParameters,
        playedAllMatches = playedAllMatches,
        currentRound = leagueInfoService.leagueInfo(leagueId).seasonInfo(restStatisticsParameters.season).roundInfo.size,
        oneTeamPerUnit = oneTeamPerUnit)
      .map(entities => restTableData(entities, restStatisticsParameters.pageSize))
      .provide(ZLayer.succeed(restClickhouseDAO))
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
    PromotionsRequest.execute(
        OrderingKeyPath(leagueId = Some(leagueId),
            divisionLevel = Some(divisionLevel)),
          leagueInfoService.leagueInfo.currentSeason(leagueId))
      .map(PromotionWithType.convert)
      .provide(ZLayer.succeed(restClickhouseDAO))
  }

  def dreamTeam(season: Int, leagueId: Int, divisionLevel: Int, sortBy: String, statsType: StatsType): Action[AnyContent] = asyncZio {
    DreamTeamRequest.execute(
      orderingKeyPath = OrderingKeyPath(season = Some(season),
        leagueId = Some(leagueId),
        divisionLevel = Some(divisionLevel)),
      statsType = statsType,
      sortBy = sortBy)
      .provide(ZLayer.succeed(restClickhouseDAO))
  }
}
