package controllers

import databases.dao.RestClickhouseDAO
import databases.requests.OrderingKeyPath
import databases.requests.matchdetails.{MatchSurprisingRequest, MatchTopHatstatsRequest, TeamHatstatsRequest}
import databases.requests.playerstats.dreamteam.DreamTeamRequest
import databases.requests.playerstats.player.stats.{PlayerRatingsRequest, PlayerSalaryTSIRequest}
import databases.requests.playerstats.team.TeamSalaryTSIRequest
import databases.requests.teamdetails.OldestTeamsRequest
import models.web.{PlayersParameters, RestStatisticsParameters, StatsType}
import play.api.cache.AsyncCacheApi
import play.api.i18n.I18nSupport
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import service.leagueinfo.LeagueInfoService
import webclients.ChppClient

import javax.inject.{Inject, Singleton}
//TODO
import scala.concurrent.ExecutionContext.Implicits.global
import databases.requests.playerstats.player.stats.PlayerCardsRequest
import databases.requests.playerstats.player.stats.PlayerGamesGoalsRequest
import databases.requests.model.player.PlayerCards
import databases.requests.playerstats.player.stats.PlayerInjuryRequest
import databases.requests.playerstats.team.TeamRatingsRequest
import databases.requests.playerstats.team.TeamAgeInjuryRequest
import databases.requests.teamdetails.TeamPowerRatingsRequest
import databases.requests.teamdetails.TeamFanclubFlagsRequest
import databases.requests.teamdetails.TeamStreakTrophiesRequest
import databases.requests.matchdetails.MatchSpectatorsRequest
import databases.requests.matchdetails.TeamGoalPointsRequest
import databases.requests.playerstats.team.TeamCardsRequest

@Singleton
class WorldController @Inject() (val controllerComponents: ControllerComponents,
             implicit val restClickhouseDAO: RestClickhouseDAO,
             val leagueInfoService: LeagueInfoService,
             val chppClient: ChppClient,
             val cache: AsyncCacheApi)
        extends RestController with I18nSupport with MessageSupport {

  def teamHatstats(restStatisticsParameters: RestStatisticsParameters): Action[AnyContent] = Action.async {
    TeamHatstatsRequest.execute(OrderingKeyPath(), restStatisticsParameters)
      .map(entities => restTableDataJson(entities, restStatisticsParameters.pageSize))
  }

  def teamSalaryTsi(restStatisticsParameters: RestStatisticsParameters, playedInLastMatch: Boolean, excludeZeroTsi: Boolean): Action[AnyContent] = Action.async {
    TeamSalaryTSIRequest.execute(
        orderingKeyPath = OrderingKeyPath(),
        parameters = restStatisticsParameters,
        playedInLastMatch = playedInLastMatch,
        excludeZeroTsi = excludeZeroTsi
    )
      .map(entities => restTableDataJson(entities, restStatisticsParameters.pageSize))
  }

  def playersTsiSalary(restStatisticsParameters: RestStatisticsParameters, playersParameters: PlayersParameters): Action[AnyContent] = Action.async {
      PlayerSalaryTSIRequest.execute(OrderingKeyPath(), restStatisticsParameters, playersParameters)
        .map(entities => restTableDataJson(entities, restStatisticsParameters.pageSize))
  }

  def playerCards(restStatisticsParameters: RestStatisticsParameters, playersParameters: PlayersParameters): Action[AnyContent] = Action.async {
      PlayerCardsRequest.execute(OrderingKeyPath(), restStatisticsParameters, playersParameters)
        .map(entities => restTableDataJson(entities, restStatisticsParameters.pageSize))
  }

  def playerGoalGames(restStatisticsParameters: RestStatisticsParameters, playersParameters: PlayersParameters): Action[AnyContent] = Action.async {
      PlayerGamesGoalsRequest.execute(OrderingKeyPath(), restStatisticsParameters, playersParameters)
        .map(entities => restTableDataJson(entities, restStatisticsParameters.pageSize))
  }

  def playerInjuries(restStatisticsParameters: RestStatisticsParameters): Action[AnyContent] = Action.async {
      PlayerInjuryRequest.execute(OrderingKeyPath(), restStatisticsParameters)
        .map(entities => restTableDataJson(entities, restStatisticsParameters.pageSize))
  }

  def playerRatings(restStatisticsParameters: RestStatisticsParameters, playersParameters: PlayersParameters): Action[AnyContent] = Action.async {
      PlayerRatingsRequest.execute(OrderingKeyPath(), restStatisticsParameters, playersParameters)
        .map(entities => restTableDataJson(entities, restStatisticsParameters.pageSize))
    }


  def oldestTeams(restStatisticsParameters: RestStatisticsParameters): Action[AnyContent] = Action.async {
    OldestTeamsRequest.execute(OrderingKeyPath(), restStatisticsParameters)
      .map(entities => restTableDataJson(entities, restStatisticsParameters.pageSize))
  }

  def dreamTeam(season: Int, sortBy: String, statsType: StatsType): Action[AnyContent] = Action.async {
      cache.getOrElseUpdate(s"worldDreamTeam_s${season}_r${statsType.toString}")(
        DreamTeamRequest.execute(OrderingKeyPath(season = Some(season)),
          statsType,
          sortBy)
      )
        .map(players => Ok(Json.toJson(players)))
    }

  def teamCards(restStatisticsParameters: RestStatisticsParameters): Action[AnyContent] = Action.async {
    TeamCardsRequest.execute(OrderingKeyPath(), restStatisticsParameters)
      .map(entities => restTableDataJson(entities, restStatisticsParameters.pageSize))
  }

  def teamRatings(restStatisticsParameters: RestStatisticsParameters): Action[AnyContent] = Action.async {
    TeamRatingsRequest.execute(OrderingKeyPath(), restStatisticsParameters)
      .map(entities => restTableDataJson(entities, restStatisticsParameters.pageSize))
  }

  def teamAgeInjuries(restStatisticsParameters: RestStatisticsParameters): Action[AnyContent] = Action.async {
    TeamAgeInjuryRequest.execute(OrderingKeyPath(), restStatisticsParameters)
      .map(entities => restTableDataJson(entities, restStatisticsParameters.pageSize))
  }

  def teamGoalPoints(restStatisticsParameters: RestStatisticsParameters,
                     playedAllMatches: Boolean, oneTeamPerUnit: Boolean): Action[AnyContent] = Action.async {
    TeamGoalPointsRequest.execute(OrderingKeyPath(),
          restStatisticsParameters,
          playedAllMatches,
          leagueInfoService.leagueInfo(100).seasonInfo(restStatisticsParameters.season).roundInfo.size,
          oneTeamPerUnit)
      .map(entities => restTableDataJson(entities, restStatisticsParameters.pageSize))
  }

  def teamPowerRatings(restStatisticsParameters: RestStatisticsParameters): Action[AnyContent] = Action.async {
    TeamPowerRatingsRequest.execute(OrderingKeyPath(), restStatisticsParameters)
      .map(entities => restTableDataJson(entities, restStatisticsParameters.pageSize))
  }

  def teamFanclubFlags(restStatisticsParameters: RestStatisticsParameters): Action[AnyContent] = Action.async {
    TeamFanclubFlagsRequest.execute(OrderingKeyPath(), restStatisticsParameters)
      .map(entities => restTableDataJson(entities, restStatisticsParameters.pageSize))
  }

  def teamStreakTrophies(restStatisticsParameters: RestStatisticsParameters): Action[AnyContent] = Action.async {
    TeamStreakTrophiesRequest.execute(OrderingKeyPath(), restStatisticsParameters)
      .map(entities => restTableDataJson(entities, restStatisticsParameters.pageSize))
  }

  def topMatches(restStatisticsParameters: RestStatisticsParameters): Action[AnyContent] = Action.async {
      MatchTopHatstatsRequest.execute(OrderingKeyPath(), restStatisticsParameters)
        .map(entities => restTableDataJson(entities, restStatisticsParameters.pageSize))
    }

  def surprisingMatches(restStatisticsParameters: RestStatisticsParameters): Action[AnyContent] = Action.async {
    MatchSurprisingRequest.execute(OrderingKeyPath(), restStatisticsParameters)
      .map(entities => restTableDataJson(entities, restStatisticsParameters.pageSize))
  }

  def matchSpectators(restStatisticsParameters: RestStatisticsParameters): Action[AnyContent] = Action.async {
    MatchSpectatorsRequest.execute(OrderingKeyPath(), restStatisticsParameters)
      .map(entities => restTableDataJson(entities, restStatisticsParameters.pageSize))
  }
}
