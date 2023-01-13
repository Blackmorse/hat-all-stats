package controllers

import databases.dao.RestClickhouseDAO
import databases.requests.matchdetails._
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
import play.api.libs.json.{Json, OWrites, Writes}
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import service.leagueinfo.{LeagueInfo, LeagueInfoService, LoadingInfo}
import utils.{CurrencyUtils, Romans}

import scala.concurrent.ExecutionContext.Implicits.global

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
                                            implicit val restClickhouseDAO: RestClickhouseDAO) extends RestController {
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

  private def playersRequest[T](plRequest: ClickhousePlayerStatsRequest[T],
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

  def teamSalaryTsi(leagueId: Int, divisionLevel: Int, restStatisticsParameters: RestStatisticsParameters, playedInLastMatch: Boolean, excludeZeroTsi: Boolean): Action[AnyContent] = Action.async { implicit request =>
    TeamSalaryTSIRequest.execute(
        orderingKeyPath = OrderingKeyPath(
          leagueId = Some(leagueId),
          divisionLevel = Some(divisionLevel)),
        parameters = restStatisticsParameters,
        playedInLastMatch = playedInLastMatch,
        excludeZeroTsi = excludeZeroTsi)
      .map(entities => restTableDataJson(entities, restStatisticsParameters.pageSize))
  }

  def teamCards(leagueId: Int, divisionLevel: Int, restStatisticsParameters: RestStatisticsParameters): Action[AnyContent] = Action.async { implicit request =>
    TeamCardsRequest.execute(
      OrderingKeyPath(leagueId = Some(leagueId),
        divisionLevel = Some(divisionLevel)),
      restStatisticsParameters)
      .map(entities => restTableDataJson(entities, restStatisticsParameters.pageSize))
  }

  def teamRatings(leagueId: Int, divisionLevel: Int, restStatisticsParameters: RestStatisticsParameters): Action[AnyContent] =
    stats(TeamRatingsRequest, leagueId, divisionLevel, restStatisticsParameters)

  def teamAgeInjuries(leagueId: Int, divisionLevel: Int, restStatisticsParameters: RestStatisticsParameters): Action[AnyContent] =
    stats(TeamAgeInjuryRequest, leagueId, divisionLevel, restStatisticsParameters)

  def teamGoalPoints(leagueId: Int, divisionLevel: Int, restStatisticsParameters: RestStatisticsParameters,
                     playedAllMatches: Boolean, oneTeamPerUnit: Boolean): Action[AnyContent] = Action.async { implicit request =>
    TeamGoalPointsRequest.execute(
        OrderingKeyPath(
          leagueId = Some(leagueId),
          divisionLevel = Some(divisionLevel)),
        restStatisticsParameters,
        playedAllMatches,
        leagueInfoService.leagueInfo(leagueId).seasonInfo(restStatisticsParameters.season).roundInfo.size,
        oneTeamPerUnit)
      .map(entities => restTableDataJson(entities, restStatisticsParameters.pageSize))
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

  def promotions(leagueId: Int, divisionLevel: Int): Action[AnyContent] = Action.async { implicit request =>
    PromotionsRequest.execute(
        OrderingKeyPath(leagueId = Some(leagueId),
            divisionLevel = Some(divisionLevel)),
          leagueInfoService.leagueInfo.currentSeason(leagueId))
      .map(PromotionWithType.convert)
      .map(result => Ok(Json.toJson(result)))
  }

  def dreamTeam(season: Int, leagueId: Int, divisionLevel: Int, sortBy: String, statsType: StatsType): Action[AnyContent] = Action.async{ implicit request =>
    DreamTeamRequest.execute(
      orderingKeyPath = OrderingKeyPath(season = Some(season), leagueId = Some(leagueId), divisionLevel = Some(divisionLevel)),
      statsType = statsType,
      sortBy = sortBy)
    .map(players => Ok(Json.toJson(players)))
  }
}
