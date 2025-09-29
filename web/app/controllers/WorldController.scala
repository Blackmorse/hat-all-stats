package controllers

import cache.ZioCacheModule.{DreamTeamCacheKey, ZDreamTeamCache}
import databases.dao.RestClickhouseDAO
import databases.requests.OrderingKeyPath
import databases.requests.matchdetails.{MatchSurprisingRequest, MatchTopHatstatsRequest, TeamHatstatsRequest}
import databases.requests.model.player.DreamTeamPlayer
import databases.requests.playerstats.player.stats.{PlayerRatingsRequest, PlayerSalaryTSIRequest}
import databases.requests.playerstats.team.TeamSalaryTSIRequest
import databases.requests.teamdetails.OldestTeamsRequest
import models.web.{HattidError, PlayersParameters, RestStatisticsParameters, StatsType}
import play.api.cache.AsyncCacheApi
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import service.leagueinfo.LeagueInfoService
import webclients.ChppClient
import zio.cache.Cache
import zio.{URIO, ZIO, ZLayer}

import javax.inject.{Inject, Named, Singleton}
//TODO
import databases.requests.matchdetails.{MatchSpectatorsRequest, TeamGoalPointsRequest}
import databases.requests.model.player.PlayerCards
import databases.requests.playerstats.player.stats.{PlayerCardsRequest, PlayerGamesGoalsRequest, PlayerInjuryRequest}
import databases.requests.playerstats.team.{TeamAgeInjuryRequest, TeamCardsRequest, TeamRatingsRequest}
import databases.requests.teamdetails.{TeamFanclubFlagsRequest, TeamPowerRatingsRequest, TeamStreakTrophiesRequest}

@Singleton
class WorldController @Inject() (val controllerComponents: ControllerComponents,
             val restClickhouseDAO: RestClickhouseDAO,
             val leagueInfoService: LeagueInfoService,
             val chppClient: ChppClient,
             val cache: AsyncCacheApi,
             @Named("DreamTeamCache") val zDreamTeamCache: URIO[RestClickhouseDAO, ZDreamTeamCache])
        extends RestController {

  def teamHatstats(restStatisticsParameters: RestStatisticsParameters): Action[AnyContent] = asyncZio {
    TeamHatstatsRequest.execute(OrderingKeyPath(), restStatisticsParameters)
      .map(entities => restTableData(entities, restStatisticsParameters.pageSize))
      .provide(ZLayer.succeed(restClickhouseDAO))
  }

  def teamSalaryTsi(restStatisticsParameters: RestStatisticsParameters, playedInLastMatch: Boolean, excludeZeroTsi: Boolean): Action[AnyContent] = asyncZio {
    TeamSalaryTSIRequest.execute(
        orderingKeyPath = OrderingKeyPath(),
        parameters = restStatisticsParameters,
        playedInLastMatch = playedInLastMatch,
        excludeZeroTsi = excludeZeroTsi
    )
      .map(entities => restTableData(entities, restStatisticsParameters.pageSize))
      .provide(ZLayer.succeed(restClickhouseDAO))
  }

  def playersTsiSalary(restStatisticsParameters: RestStatisticsParameters, playersParameters: PlayersParameters): Action[AnyContent] = asyncZio {
      PlayerSalaryTSIRequest.execute(orderingKeyPath = OrderingKeyPath(),
          parameters = restStatisticsParameters,
          playersParameters = playersParameters)
        .map(entities => restTableData(entities, restStatisticsParameters.pageSize))
        .provide(ZLayer.succeed(restClickhouseDAO))
  }

  def playerCards(restStatisticsParameters: RestStatisticsParameters, playersParameters: PlayersParameters): Action[AnyContent] = asyncZio {
      PlayerCardsRequest.execute(orderingKeyPath = OrderingKeyPath(),
          parameters = restStatisticsParameters,
          playersParameters = playersParameters)
        .map(entities => restTableData(entities, restStatisticsParameters.pageSize))
        .provide(ZLayer.succeed(restClickhouseDAO))
  }

  def playerGoalGames(restStatisticsParameters: RestStatisticsParameters, playersParameters: PlayersParameters): Action[AnyContent] = asyncZio {
      PlayerGamesGoalsRequest.execute(orderingKeyPath = OrderingKeyPath(),
          parameters = restStatisticsParameters,
          playersParameters = playersParameters)
        .map(entities => restTableData(entities, restStatisticsParameters.pageSize))
        .provide(ZLayer.succeed(restClickhouseDAO))
  }

  def playerInjuries(restStatisticsParameters: RestStatisticsParameters): Action[AnyContent] = asyncZio {
      PlayerInjuryRequest.execute(OrderingKeyPath(), restStatisticsParameters)
        .map(entities => restTableData(entities, restStatisticsParameters.pageSize))
        .provide(ZLayer.succeed(restClickhouseDAO))
  }

  def playerRatings(restStatisticsParameters: RestStatisticsParameters, playersParameters: PlayersParameters): Action[AnyContent] = asyncZio {
      PlayerRatingsRequest.execute(orderingKeyPath = OrderingKeyPath(),
          parameters = restStatisticsParameters,
          playersParameters = playersParameters)
        .map(entities => restTableData(entities, restStatisticsParameters.pageSize))
        .provide(ZLayer.succeed(restClickhouseDAO))
    }


  def oldestTeams(restStatisticsParameters: RestStatisticsParameters): Action[AnyContent] = asyncZio {
    OldestTeamsRequest.execute(orderingKeyPath = OrderingKeyPath(),
        parameters = restStatisticsParameters)
      .map(entities => restTableData(entities, restStatisticsParameters.pageSize))
      .provide(ZLayer.succeed(restClickhouseDAO))
  }

  def dreamTeam(season: Int, sortBy: String, statsType: StatsType): Action[AnyContent] = asyncZio {
      (for {
       cache <- zDreamTeamCache
       result <- cache.get((OrderingKeyPath(season = Some(season)), statsType, sortBy))
      } yield result)
      .provide(ZLayer.succeed(restClickhouseDAO))
    }
  
  
  private def dreamTeamZIO(key: DreamTeamCacheKey): ZIO[ZDreamTeamCache, HattidError, List[DreamTeamPlayer]] = {
    for {
      cache <- ZIO.service[ZDreamTeamCache]
      result <- cache.get(key)
    } yield result
  }

  def teamCards(restStatisticsParameters: RestStatisticsParameters): Action[AnyContent] = asyncZio {
    TeamCardsRequest.execute(
        orderingKeyPath = OrderingKeyPath(),
        parameters = restStatisticsParameters)
      .map(entities => restTableData(entities, restStatisticsParameters.pageSize))
      .provide(ZLayer.succeed(restClickhouseDAO))
  }

  def teamRatings(restStatisticsParameters: RestStatisticsParameters): Action[AnyContent] = asyncZio {
    TeamRatingsRequest.execute(OrderingKeyPath(), restStatisticsParameters)
      .map(entities => restTableData(entities, restStatisticsParameters.pageSize))
      .provide(ZLayer.succeed(restClickhouseDAO))
  }

  def teamAgeInjuries(restStatisticsParameters: RestStatisticsParameters): Action[AnyContent] = asyncZio {
    TeamAgeInjuryRequest.execute(OrderingKeyPath(), restStatisticsParameters)
      .map(entities => restTableData(entities, restStatisticsParameters.pageSize))
      .provide(ZLayer.succeed(restClickhouseDAO))
  }

  def teamGoalPoints(restStatisticsParameters: RestStatisticsParameters,
                     playedAllMatches: Boolean, oneTeamPerUnit: Boolean): Action[AnyContent] = asyncZio {
    TeamGoalPointsRequest.execute(orderingKeyPath = OrderingKeyPath(),
          parameters = restStatisticsParameters,
          playedAllMatches = playedAllMatches,
          currentRound = leagueInfoService.leagueInfo(100).seasonInfo(restStatisticsParameters.season).roundInfo.size,
          oneTeamPerUnit = oneTeamPerUnit)
      .map(entities => restTableData(entities, restStatisticsParameters.pageSize))
      .provide(ZLayer.succeed(restClickhouseDAO))
  }

  def teamPowerRatings(restStatisticsParameters: RestStatisticsParameters): Action[AnyContent] = asyncZio {
    TeamPowerRatingsRequest.execute(OrderingKeyPath(), restStatisticsParameters)
      .map(entities => restTableData(entities, restStatisticsParameters.pageSize))
      .provide(ZLayer.succeed(restClickhouseDAO))
  }

  def teamFanclubFlags(restStatisticsParameters: RestStatisticsParameters): Action[AnyContent] = asyncZio {
    TeamFanclubFlagsRequest.execute(OrderingKeyPath(), restStatisticsParameters)
      .map(entities => restTableData(entities, restStatisticsParameters.pageSize))
      .provide(ZLayer.succeed(restClickhouseDAO))
  }

  def teamStreakTrophies(restStatisticsParameters: RestStatisticsParameters): Action[AnyContent] = asyncZio {
    TeamStreakTrophiesRequest.execute(OrderingKeyPath(), restStatisticsParameters)
      .map(entities => restTableData(entities, restStatisticsParameters.pageSize))
      .provide(ZLayer.succeed(restClickhouseDAO))
  }

  def topMatches(restStatisticsParameters: RestStatisticsParameters): Action[AnyContent] = asyncZio {
      MatchTopHatstatsRequest.execute(OrderingKeyPath(), restStatisticsParameters)
        .map(entities => restTableData(entities, restStatisticsParameters.pageSize))
        .provide(ZLayer.succeed(restClickhouseDAO))
    }

  def surprisingMatches(restStatisticsParameters: RestStatisticsParameters): Action[AnyContent] = asyncZio {
    MatchSurprisingRequest.execute(OrderingKeyPath(), restStatisticsParameters)
      .map(entities => restTableData(entities, restStatisticsParameters.pageSize))
      .provide(ZLayer.succeed(restClickhouseDAO))
  }

  def matchSpectators(restStatisticsParameters: RestStatisticsParameters): Action[AnyContent] = asyncZio {
    MatchSpectatorsRequest.execute(OrderingKeyPath(), restStatisticsParameters)
      .map(entities => restTableData(entities, restStatisticsParameters.pageSize))
      .provide(ZLayer.succeed(restClickhouseDAO))
  }
}
