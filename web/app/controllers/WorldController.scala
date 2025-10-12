package controllers

import cache.ZioCacheModule.{DreamTeamCacheKey, HattidEnv, ZDreamTeamCache}
import databases.dao.RestClickhouseDAO
import databases.requests.OrderingKeyPath
import databases.requests.matchdetails.*
import databases.requests.model.player.{DreamTeamPlayer, PlayerCards}
import databases.requests.playerstats.player.stats.*
import databases.requests.playerstats.team.{TeamAgeInjuryRequest, TeamCardsRequest, TeamRatingsRequest, TeamSalaryTSIRequest}
import databases.requests.teamdetails.{OldestTeamsRequest, TeamFanclubFlagsRequest, TeamPowerRatingsRequest, TeamStreakTrophiesRequest}
import models.web.{HattidError, PlayersParameters, RestStatisticsParameters, StatsType}
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import service.leagueinfo.LeagueInfoServiceZIO
import zio.cache.Cache
import zio.{URIO, ZIO}

import javax.inject.{Inject, Named, Singleton}

@Singleton
class WorldController @Inject() (val controllerComponents: ControllerComponents,
             val hattidEnvironment: zio.ZEnvironment[HattidEnv],
             @Named("DreamTeamCache") val zDreamTeamCache: URIO[RestClickhouseDAO, ZDreamTeamCache])
        extends RestController(hattidEnvironment) {
  private val leagueInfoServiceLayer = LeagueInfoServiceZIO.layer

  def teamHatstats(restStatisticsParameters: RestStatisticsParameters): Action[AnyContent] = asyncZio {
    TeamHatstatsRequest.execute(OrderingKeyPath(), restStatisticsParameters)
      .map(entities => restTableData(entities, restStatisticsParameters.pageSize))
  }

  def teamSalaryTsi(restStatisticsParameters: RestStatisticsParameters, playedInLastMatch: Boolean, excludeZeroTsi: Boolean): Action[AnyContent] = asyncZio {
    TeamSalaryTSIRequest.execute(
        orderingKeyPath = OrderingKeyPath(),
        parameters = restStatisticsParameters,
        playedInLastMatch = playedInLastMatch,
        excludeZeroTsi = excludeZeroTsi
    )
      .map(entities => restTableData(entities, restStatisticsParameters.pageSize))
  }

  def playersTsiSalary(restStatisticsParameters: RestStatisticsParameters, playersParameters: PlayersParameters): Action[AnyContent] = asyncZio {
      PlayerSalaryTSIRequest.execute(orderingKeyPath = OrderingKeyPath(),
          parameters = restStatisticsParameters,
          playersParameters = playersParameters)
        .map(entities => restTableData(entities, restStatisticsParameters.pageSize))
  }

  def playerCards(restStatisticsParameters: RestStatisticsParameters, playersParameters: PlayersParameters): Action[AnyContent] = asyncZio {
      PlayerCardsRequest.execute(orderingKeyPath = OrderingKeyPath(),
          parameters = restStatisticsParameters,
          playersParameters = playersParameters)
        .map(entities => restTableData(entities, restStatisticsParameters.pageSize))
  }

  def playerGoalGames(restStatisticsParameters: RestStatisticsParameters, playersParameters: PlayersParameters): Action[AnyContent] = asyncZio {
      PlayerGamesGoalsRequest.execute(orderingKeyPath = OrderingKeyPath(),
          parameters = restStatisticsParameters,
          playersParameters = playersParameters)
        .map(entities => restTableData(entities, restStatisticsParameters.pageSize))
  }

  def playerInjuries(restStatisticsParameters: RestStatisticsParameters): Action[AnyContent] = asyncZio {
      PlayerInjuryRequest.execute(OrderingKeyPath(), restStatisticsParameters)
        .map(entities => restTableData(entities, restStatisticsParameters.pageSize))
  }

  def playerRatings(restStatisticsParameters: RestStatisticsParameters, playersParameters: PlayersParameters): Action[AnyContent] = asyncZio {
      PlayerRatingsRequest.execute(orderingKeyPath = OrderingKeyPath(),
          parameters = restStatisticsParameters,
          playersParameters = playersParameters)
        .map(entities => restTableData(entities, restStatisticsParameters.pageSize))
    }


  def oldestTeams(restStatisticsParameters: RestStatisticsParameters): Action[AnyContent] = asyncZio {
    OldestTeamsRequest.execute(orderingKeyPath = OrderingKeyPath(),
        parameters = restStatisticsParameters)
      .map(entities => restTableData(entities, restStatisticsParameters.pageSize))
  }

  def dreamTeam(season: Int, sortBy: String, statsType: StatsType): Action[AnyContent] = asyncZio {
      for {
       cache <- zDreamTeamCache
       result <- cache.get((OrderingKeyPath(season = Some(season)), statsType, sortBy))
      } yield result
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
  }

  def teamRatings(restStatisticsParameters: RestStatisticsParameters): Action[AnyContent] = asyncZio {
    TeamRatingsRequest.execute(OrderingKeyPath(), restStatisticsParameters)
      .map(entities => restTableData(entities, restStatisticsParameters.pageSize))
  }

  def teamAgeInjuries(restStatisticsParameters: RestStatisticsParameters): Action[AnyContent] = asyncZio {
    TeamAgeInjuryRequest.execute(OrderingKeyPath(), restStatisticsParameters)
      .map(entities => restTableData(entities, restStatisticsParameters.pageSize))
  }

  def teamGoalPoints(restStatisticsParameters: RestStatisticsParameters,
                        playedAllMatches: Boolean,
                        oneTeamPerUnit: Boolean): Action[AnyContent] = asyncZio {
    for {
      leagueInfoService <- ZIO.service[LeagueInfoServiceZIO]
      round             <- leagueInfoService.leagueRoundForSeason(100, restStatisticsParameters.season)
      entities          <- TeamGoalPointsRequest.execute(orderingKeyPath = OrderingKeyPath(),
        parameters = restStatisticsParameters,
        playedAllMatches = playedAllMatches,
        currentRound = round,
        oneTeamPerUnit = oneTeamPerUnit)
    } yield restTableData(entities, restStatisticsParameters.pageSize)
  }

  def teamPowerRatings(restStatisticsParameters: RestStatisticsParameters): Action[AnyContent] = asyncZio {
    TeamPowerRatingsRequest.execute(OrderingKeyPath(), restStatisticsParameters)
      .map(entities => restTableData(entities, restStatisticsParameters.pageSize))
  }

  def teamFanclubFlags(restStatisticsParameters: RestStatisticsParameters): Action[AnyContent] = asyncZio {
    TeamFanclubFlagsRequest.execute(OrderingKeyPath(), restStatisticsParameters)
      .map(entities => restTableData(entities, restStatisticsParameters.pageSize))
  }

  def teamStreakTrophies(restStatisticsParameters: RestStatisticsParameters): Action[AnyContent] = asyncZio {
    TeamStreakTrophiesRequest.execute(OrderingKeyPath(), restStatisticsParameters)
      .map(entities => restTableData(entities, restStatisticsParameters.pageSize))
  }

  def topMatches(restStatisticsParameters: RestStatisticsParameters): Action[AnyContent] = asyncZio {
      MatchTopHatstatsRequest.execute(OrderingKeyPath(), restStatisticsParameters)
        .map(entities => restTableData(entities, restStatisticsParameters.pageSize))
    }

  def surprisingMatches(restStatisticsParameters: RestStatisticsParameters): Action[AnyContent] = asyncZio {
    MatchSurprisingRequest.execute(OrderingKeyPath(), restStatisticsParameters)
      .map(entities => restTableData(entities, restStatisticsParameters.pageSize))
  }

  def matchSpectators(restStatisticsParameters: RestStatisticsParameters): Action[AnyContent] = asyncZio {
    MatchSpectatorsRequest.execute(OrderingKeyPath(), restStatisticsParameters)
      .map(entities => restTableData(entities, restStatisticsParameters.pageSize))
  }
}
