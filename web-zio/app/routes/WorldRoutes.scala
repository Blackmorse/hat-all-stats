package routes

import databases.requests.matchdetails.*
import databases.requests.model.player.{PlayerCards, PlayerGamesGoals, PlayerRating, PlayerSalaryTSI}
import databases.requests.model.team.*
import databases.requests.playerstats.player.stats.*
import databases.requests.playerstats.team.{TeamAgeInjuryRequest, TeamCardsRequest, TeamRatingsRequest, TeamSalaryTSIRequest}
import databases.requests.teamdetails.{OldestTeamsRequest, TeamFanclubFlagsRequest, TeamPowerRatingsRequest, TeamStreakTrophiesRequest}
import databases.requests.{ClickhouseStatisticsRequest, OrderingKeyPath}
import hattid.zio.HattidCache.zDreamTeamCache
import hattid.zio.*
import models.web.{HattidError, RestTableData}
import service.leagueinfo.LeagueInfoServiceZIO
import zio.ZIO
import zio.http.*
import zio.json.*

object WorldRoutes {
  private val GetWorld: RoutePattern[Unit] = Method.GET / "api" / "world"
  val routes: Seq[Route[HattidEnv, HattidError]] = Seq(
    GetWorld / "teamHatstats" -> statisticsHandler(TeamHatstatsRequest),
    GetWorld / "teamSalaryTsi" -> teamSalaryTSIHandler,
    GetWorld / "teamCards" -> statisticsHandler(TeamCardsRequest),
    GetWorld / "teamRatings" -> statisticsHandler(TeamRatingsRequest),
    GetWorld / "teamAgeInjuries" -> statisticsHandler(TeamAgeInjuryRequest),
    GetWorld / "teamGoalPoints" -> teamGoalPointsHandler,
    GetWorld / "teamPowerRatings" -> statisticsHandler(TeamPowerRatingsRequest),
    GetWorld / "teamFanclubFlags" -> statisticsHandler(TeamFanclubFlagsRequest),
    GetWorld / "teamStreakTrophies" -> statisticsHandler(TeamStreakTrophiesRequest),

    GetWorld / "playerTsiSalary" -> playerStatsHandler(PlayerSalaryTSIRequest),
    GetWorld / "playerRatings" -> playerStatsHandler(PlayerRatingsRequest),
    GetWorld / "playerCards" -> playerStatsHandler(PlayerCardsRequest),
    GetWorld / "playerGoalGames" -> playerStatsHandler(PlayerGamesGoalsRequest),
    GetWorld / "playerInjuries" -> statisticsHandler(PlayerInjuryRequest),
    GetWorld / "topMatches" -> statisticsHandler(MatchTopHatstatsRequest),
    GetWorld / "surprisingMatches" -> statisticsHandler(MatchSurprisingRequest),
    GetWorld / "matchSpectators" -> statisticsHandler(MatchSpectatorsRequest),
    GetWorld / "dreamTeam" -> dreamTeamHandler,
    GetWorld / "oldestTeams" -> statisticsHandler(OldestTeamsRequest),
  )

  private def dreamTeamHandler = handler { (req: Request) =>
    for {
      cache     <- zDreamTeamCache
      season    <- req.intParam("season")
      statsType <- req.statsType()
      sortBy    <- req.stringParam("sortBy")
      entities  <- cache.get((OrderingKeyPath(season = Some(season)), statsType, sortBy))
    } yield Response.json(entities.toJson)
  }

  private def teamSalaryTSIHandler: Handler[DBServices, HattidError, Request, Response] = handler { (req: Request) =>
    for {
      playedInLastMatch        <- req.boolParam("playedInLastMatch")
      excludeZeroTsi           <- req.boolParam("excludeZeroTsi")
      restStatisticsParameters <- req.restStatisticsParameters()
      entities                 <- TeamSalaryTSIRequest.execute(
        orderingKeyPath = OrderingKeyPath(),
        parameters = restStatisticsParameters,
        playedInLastMatch = playedInLastMatch,
        excludeZeroTsi = excludeZeroTsi
      )
    } yield Response.json(restTableData(entities, restStatisticsParameters.pageSize).toJson)
  }

  private def playerStatsHandler[T: JsonEncoder, R]
  (clickhouseRequest: ClickhousePlayerStatsRequest[T]) =
    handler { (req: Request) =>
      for {
        restStatisticsParameters <- req.restStatisticsParameters()
        playerParameters         <- req.playersParameters()
        entities                 <- clickhouseRequest.execute(
          orderingKeyPath = OrderingKeyPath(),
          parameters = restStatisticsParameters,
          playersParameters = playerParameters
        )
      } yield Response.json(restTableData(entities, restStatisticsParameters.pageSize).toJson)
    }

  private def statisticsHandler[T: JsonEncoder, R](clickhouseRequest: ClickhouseStatisticsRequest[T]) = handler { (req: Request) =>
    for {
      restStatisticsParameters <- req.restStatisticsParameters()
      entities                 <- clickhouseRequest.execute(
        orderingKeyPath = OrderingKeyPath(),
        parameters = restStatisticsParameters
      )
    } yield Response.json(restTableData(entities, restStatisticsParameters.pageSize).toJson)
  }

  private def teamGoalPointsHandler: Handler[DBServices & LeagueInfoServiceZIO, HattidError, Request, Response] = handler { (req: Request) =>
    for {
      leagueInfoService        <- ZIO.service[LeagueInfoServiceZIO]
      playedAllMatches         <- req.boolParamWithDefault("playedAllMatches", false)
      oneTeamPerUnit           <- req.boolParam("oneTeamPerUnit")
      restStatisticsParameters <- req.restStatisticsParameters()
      round                    <- leagueInfoService.leagueRoundForSeason(100, restStatisticsParameters.season)
      entities                 <- TeamGoalPointsRequest.execute(orderingKeyPath = OrderingKeyPath(),
        parameters = restStatisticsParameters,
        playedAllMatches = playedAllMatches,
        currentRound = round,
        oneTeamPerUnit = oneTeamPerUnit)
    } yield Response.json(restTableData(entities, restStatisticsParameters.pageSize).toJson)
  }
}
