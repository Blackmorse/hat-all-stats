package controllers

import cache.ZioCacheModule.HattidEnv
import com.google.inject.{Inject, Singleton}
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
                                      val hattidEnvironment: zio.ZEnvironment[HattidEnv]) extends RestController(hattidEnvironment) {

  def getLeagueData(leagueId: Int): Action[AnyContent] = asyncZio {
    for {
      leagueInfoService <- ZIO.service[LeagueInfoServiceZIO]
      leagueState       <- leagueInfoService.leagueState(leagueId)
    } yield createRestLeagueData(leagueState)
  }

  private def createRestLeagueData(leagueState: LeagueState): RestLeagueData = {
    val numberOfDivisions = leagueState.league.numberOfLevels
    val divisionLevels = (1 to numberOfDivisions).map(Romans(_))

    RestLeagueData(
      leagueId = leagueState.league.leagueId,
      leagueName = leagueState.league.englishName,
      divisionLevels = divisionLevels,
      seasonOffset = leagueState.league.seasonOffset,
      seasonRoundInfo = leagueState.seasonRoundInfo,
      currency = CurrencyUtils.currencyName(leagueState.league.country),
      currencyRate = CurrencyUtils.currencyRate(leagueState.league.country),
      loadingInfo = leagueState.loadingInfo,
      countries = leagueState.idToCountryName)
  }
  
  private def stats[T : Writes](chRequest: ClickhouseStatisticsRequest[T],
                       leagueId: Int,
                       restStatisticsParameters: RestStatisticsParameters) = asyncZio {
    chRequest.execute(orderingKeyPath = OrderingKeyPath(leagueId = Some(leagueId)),
        parameters = restStatisticsParameters)
      .map(entities => restTableData(entities, restStatisticsParameters.pageSize))
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
  }

  def teamCards(leagueId: Int, restStatisticsParameters: RestStatisticsParameters): Action[AnyContent] = asyncZio {
    TeamCardsRequest.execute(
      orderingKeyPath = OrderingKeyPath(leagueId = Some(leagueId)),
      parameters = restStatisticsParameters)
      .map(entities => restTableData(entities, restStatisticsParameters.pageSize))
  }

  def teamRatings(leagueId: Int, restStatisticsParameters: RestStatisticsParameters): Action[AnyContent] =
    stats(TeamRatingsRequest, leagueId, restStatisticsParameters)

  def teamAgeInjuries(leagueId: Int, restStatisticsParameters: RestStatisticsParameters): Action[AnyContent] =
    stats(TeamAgeInjuryRequest, leagueId, restStatisticsParameters)

  def teamGoalPoints(leagueId: Int, restStatisticsParameters: RestStatisticsParameters,
                     playedAllMatches: Boolean, oneTeamPerUnit: Boolean): Action[AnyContent] = asyncZio {
    for {
      leagueInfoService <- ZIO.service[LeagueInfoServiceZIO]
      currentRound      <- leagueInfoService.lastRound(leagueId, restStatisticsParameters.season)
      entities          <- TeamGoalPointsRequest.execute(
                            orderingKeyPath = OrderingKeyPath(leagueId = Some(leagueId)),
                            parameters = restStatisticsParameters,
                            playedAllMatches = playedAllMatches,
                            currentRound = currentRound,
                            oneTeamPerUnit = oneTeamPerUnit)
    } yield restTableData(entities, restStatisticsParameters.pageSize)
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
    for {
      leagueInfoService <- ZIO.service[LeagueInfoServiceZIO]
      currentSeason     <- leagueInfoService.currentSeason(leagueId)
      entities          <- PromotionsRequest.execute(
                            orderingKeyPath = OrderingKeyPath(leagueId = Some(leagueId)),
                            season = currentSeason)
    } yield PromotionWithType.convert(entities)
  }

  def oldestTeams(leagueId: Int, restStatisticsParameters: RestStatisticsParameters): Action[AnyContent] = {
    stats(OldestTeamsRequest, leagueId, restStatisticsParameters)
  }

  def dreamTeam(season: Int, leagueId: Int, sortBy: String, statsType: StatsType): Action[AnyContent] = asyncZio {
    DreamTeamRequest.execute(
      orderingKeyPath = OrderingKeyPath(season = Some(season), leagueId = Some(leagueId)),
      statsType = statsType,
      sortBy = sortBy)
  }
}

