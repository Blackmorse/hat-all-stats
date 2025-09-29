package controllers

import chpp.worlddetails.models.League
import com.google.inject.{Inject, Singleton}
import databases.dao.RestClickhouseDAO
import databases.requests.matchdetails.*
import databases.requests.model.promotions.PromotionWithType
import databases.requests.playerstats.dreamteam.DreamTeamRequest
import databases.requests.playerstats.player.*
import databases.requests.playerstats.player.stats.{ClickhousePlayerStatsRequest, PlayerCardsRequest, PlayerGamesGoalsRequest, PlayerInjuryRequest, PlayerRatingsRequest, PlayerSalaryTSIRequest}
import databases.requests.playerstats.team.{TeamAgeInjuryRequest, TeamCardsRequest, TeamRatingsRequest, TeamSalaryTSIRequest}
import databases.requests.promotions.PromotionsRequest
import databases.requests.teamdetails.{OldestTeamsRequest, TeamFanclubFlagsRequest, TeamPowerRatingsRequest, TeamStreakTrophiesRequest}
import databases.requests.{ClickhouseStatisticsRequest, OrderingKeyPath}
import models.web.rest.CountryLevelData
import models.web.rest.LevelData.Rounds
import models.web.{NotFoundError, PlayersParameters, RestStatisticsParameters, StatsType}
import play.api.libs.json.{JsValue, Json, OWrites, Writes}
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import service.leagueinfo.{LeagueInfo, LeagueInfoService, LoadingInfo}
import utils.{CurrencyUtils, Romans}
import zio.ZLayer

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
  implicit val writes: OWrites[RestLeagueData] = Json.writes[RestLeagueData]
}

@Singleton
class RestLeagueController @Inject() (val controllerComponents: ControllerComponents,
                                      val restClickhouseDAO: RestClickhouseDAO,
                                  val leagueInfoService: LeagueInfoService) extends RestController  {

  def getLeagueData(leagueId: Int): Action[AnyContent] = Action { implicit request =>
    leagueInfoService.leagueInfo.get(leagueId)
      .map(createRestLeagueData)
      .map(restLeagueData => Ok(Json.toJson(restLeagueData)))
      .getOrElse(NotFound(Json.toJson(NotFoundError(
        entityType = NotFoundError.LEAGUE,
        entityId = leagueId.toString,
        description = ""
      ))))
    }

  private def createRestLeagueData(leagueInfo: LeagueInfo): RestLeagueData = {
    val league = leagueInfo.league
    val numberOfDivisions = league.numberOfLevels
    val divisionLevels = (1 to numberOfDivisions).map(Romans(_))
    val seasonRoundInfo = leagueInfoService.leagueInfo.seasonRoundInfo(league.leagueId)

    RestLeagueData(
      leagueId = league.leagueId,
      leagueName = league.englishName,
      divisionLevels = divisionLevels,
      seasonOffset = league.seasonOffset,
      seasonRoundInfo = seasonRoundInfo,
      currency = CurrencyUtils.currencyName(league.country),
      currencyRate = CurrencyUtils.currencyRate(league.country),
      loadingInfo = leagueInfo.loadingInfo,
      countries = leagueInfoService.idToStringCountryMap)
  }

  private def stats[T : Writes](chRequest: ClickhouseStatisticsRequest[T],
                       leagueId: Int,
                       restStatisticsParameters: RestStatisticsParameters) = asyncZio {
    chRequest.execute(orderingKeyPath = OrderingKeyPath(leagueId = Some(leagueId)),
        parameters = restStatisticsParameters)
      .map(entities => restTableData(entities, restStatisticsParameters.pageSize))
      .provide(ZLayer.succeed(restClickhouseDAO))
  }

  private def playersRequest[T : Writes](plRequest: ClickhousePlayerStatsRequest[T],
                                leagueId: Int,
                                restStatisticsParameters: RestStatisticsParameters,
                                playersParameters: PlayersParameters)  = asyncZio {
    plRequest.execute(
        OrderingKeyPath(leagueId = Some(leagueId)),
        restStatisticsParameters,
        playersParameters)
      .map(entities => restTableData(entities, restStatisticsParameters.pageSize))
      .provide(ZLayer.succeed(restClickhouseDAO))
  }

  def teamHatstats(leagueId: Int, restStatisticsParameters: RestStatisticsParameters): Action[AnyContent] =
    stats(TeamHatstatsRequest, leagueId, restStatisticsParameters)

  def leagueUnits(leagueId: Int, restStatisticsParameters: RestStatisticsParameters): Action[AnyContent] =
    stats(LeagueUnitHatstatsRequest, leagueId, restStatisticsParameters)

  def playerGoalGames(leagueId: Int, restStatisticsParameters: RestStatisticsParameters, playersParameters: PlayersParameters): Action[AnyContent] =
    playersRequest(PlayerGamesGoalsRequest, leagueId, restStatisticsParameters, playersParameters)

  def playerCards(leagueId: Int, restStatisticsParameters: RestStatisticsParameters, playersParameters: PlayersParameters): Action[AnyContent] =
    playersRequest(PlayerCardsRequest, leagueId, restStatisticsParameters, playersParameters)

  def playerTsiSalary(leagueId: Int, restStatisticsParameters: RestStatisticsParameters, playersParameters: PlayersParameters): Action[AnyContent] =
    playersRequest(PlayerSalaryTSIRequest, leagueId, restStatisticsParameters, playersParameters)

  def playerRatings(leagueId: Int, restStatisticsParameters: RestStatisticsParameters, playersParameters: PlayersParameters): Action[AnyContent] =
    playersRequest(PlayerRatingsRequest, leagueId, restStatisticsParameters, playersParameters)

  def playerInjuries(leagueId: Int, restStatisticsParameters: RestStatisticsParameters): Action[AnyContent] =
    stats(PlayerInjuryRequest, leagueId, restStatisticsParameters)

  def teamSalaryTsi(leagueId: Int, restStatisticsParameters: RestStatisticsParameters, playedInLastMatch: Boolean, excludeZeroTsi: Boolean): Action[AnyContent] = asyncZio {
    TeamSalaryTSIRequest.execute(
        orderingKeyPath = OrderingKeyPath(leagueId = Some(leagueId)),
        parameters = restStatisticsParameters,
        playedInLastMatch = playedInLastMatch,
        excludeZeroTsi = excludeZeroTsi)
      .map(entities => restTableData(entities, restStatisticsParameters.pageSize))
      .provide(ZLayer.succeed(restClickhouseDAO))
  }

  def teamCards(leagueId: Int, restStatisticsParameters: RestStatisticsParameters): Action[AnyContent] = asyncZio {
    TeamCardsRequest.execute(
      orderingKeyPath = OrderingKeyPath(leagueId = Some(leagueId)),
      parameters = restStatisticsParameters)
      .map(entities => restTableData(entities, restStatisticsParameters.pageSize))
      .provide(ZLayer.succeed(restClickhouseDAO))
  }

  def teamRatings(leagueId: Int, restStatisticsParameters: RestStatisticsParameters): Action[AnyContent] =
    stats(TeamRatingsRequest, leagueId, restStatisticsParameters)

  def teamAgeInjuries(leagueId: Int, restStatisticsParameters: RestStatisticsParameters): Action[AnyContent] =
    stats(TeamAgeInjuryRequest, leagueId, restStatisticsParameters)

  def teamGoalPoints(leagueId: Int, restStatisticsParameters: RestStatisticsParameters,
                     playedAllMatches: Boolean, oneTeamPerUnit: Boolean): Action[AnyContent] = asyncZio {
    TeamGoalPointsRequest.execute(
        orderingKeyPath = OrderingKeyPath(leagueId = Some(leagueId)),
        parameters = restStatisticsParameters,
        playedAllMatches = playedAllMatches,
        currentRound = leagueInfoService.leagueInfo(leagueId).seasonInfo(restStatisticsParameters.season).roundInfo.size,
        oneTeamPerUnit = oneTeamPerUnit)
      .map(entities => restTableData(entities, restStatisticsParameters.pageSize))
      .provide(ZLayer.succeed(restClickhouseDAO))
  }

  def teamPowerRatings(leagueId: Int, restStatisticsParameters: RestStatisticsParameters): Action[AnyContent] =
    stats(TeamPowerRatingsRequest, leagueId, restStatisticsParameters)

  def teamFanclubFlags(leagueId: Int, restStatisticsParameters: RestStatisticsParameters): Action[AnyContent] =
    stats(TeamFanclubFlagsRequest, leagueId, restStatisticsParameters)

  def teamStreakTrophies(leagueId: Int, restStatisticsParameters: RestStatisticsParameters): Action[AnyContent] =
    stats(TeamStreakTrophiesRequest, leagueId, restStatisticsParameters)

  def topMatches(leagueId: Int, restStatisticsParameters: RestStatisticsParameters): Action[AnyContent] =
    stats(MatchTopHatstatsRequest, leagueId, restStatisticsParameters)

  def surprisingMatches(leagueId: Int, restStatisticsParameters: RestStatisticsParameters): Action[AnyContent] =
    stats(MatchSurprisingRequest, leagueId, restStatisticsParameters)

  def matchSpectators(leagueId: Int, restStatisticsParameters: RestStatisticsParameters): Action[AnyContent] =
    stats(MatchSpectatorsRequest, leagueId, restStatisticsParameters)

  def promotions(leagueId: Int): Action[AnyContent] = asyncZio {
    PromotionsRequest.execute(orderingKeyPath = OrderingKeyPath(leagueId = Some(leagueId)),
        season = leagueInfoService.leagueInfo.currentSeason(leagueId))
      .map(PromotionWithType.convert)
      .provide(ZLayer.succeed(restClickhouseDAO))
  }

  def oldestTeams(leagueId: Int, restStatisticsParameters: RestStatisticsParameters): Action[AnyContent] = {
    stats(OldestTeamsRequest, leagueId, restStatisticsParameters)
  }

  def dreamTeam(season: Int, leagueId: Int, sortBy: String, statsType: StatsType): Action[AnyContent] = asyncZio {
    DreamTeamRequest.execute(orderingKeyPath = OrderingKeyPath(season = Some(season), leagueId = Some(leagueId)),
      statsType = statsType,
      sortBy = sortBy)
      .provide(ZLayer.succeed(restClickhouseDAO))
  }
}

