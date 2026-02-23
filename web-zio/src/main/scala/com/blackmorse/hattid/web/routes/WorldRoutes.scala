package com.blackmorse.hattid.web.routes

import com.blackmorse.hattid.web.zios.{HattidEnv, restTableData}
import com.blackmorse.hattid.web.databases.requests.matchdetails.*
import com.blackmorse.hattid.web.databases.requests.model.player.{PlayerCards, PlayerGamesGoals, PlayerRating, PlayerSalaryTSI}
import com.blackmorse.hattid.web.databases.requests.model.team.*
import com.blackmorse.hattid.web.databases.requests.playerstats.player.stats.*
import com.blackmorse.hattid.web.databases.requests.playerstats.team.{TeamAgeInjuryRequest, TeamCardsRequest, TeamRatingsRequest, TeamSalaryTSIRequest}
import com.blackmorse.hattid.web.databases.requests.teamdetails.{OldestTeamsRequest, TeamFanclubFlagsRequest, TeamPowerRatingsRequest, TeamStreakTrophiesRequest}
import com.blackmorse.hattid.web.databases.requests.{ClickhouseStatisticsRequest, OrderingKeyPath}
import com.blackmorse.hattid.web.databases.ClickhousePool.ClickhousePool
import com.blackmorse.hattid.web.zios.*
import com.blackmorse.hattid.web.zios.HattidCache.zDreamTeamCache
import com.blackmorse.hattid.web.models.web.{HattidError, RestTableData}
import com.blackmorse.hattid.web.service.leagueinfo.LeagueInfoServiceZIO
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

  private def teamSalaryTSIHandler: Handler[ClickhousePool, HattidError, Request, Response] = handler { (req: Request) =>
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

  private def teamGoalPointsHandler: Handler[ClickhousePool & LeagueInfoServiceZIO, HattidError, Request, Response] = handler { (req: Request) =>
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
