package com.blackmorse.hattid.web.routes

import com.blackmorse.hattid.web.zios.{HattidEnv, restTableData}
import com.blackmorse.hattid.web.databases.requests.matchdetails.*
import com.blackmorse.hattid.web.databases.requests.model.promotions.PromotionWithType
import com.blackmorse.hattid.web.databases.requests.playerstats.dreamteam.DreamTeamRequest
import com.blackmorse.hattid.web.databases.requests.playerstats.player.stats.*
import com.blackmorse.hattid.web.databases.requests.playerstats.team.{TeamAgeInjuryRequest, TeamCardsRequest, TeamRatingsRequest, TeamSalaryTSIRequest}
import com.blackmorse.hattid.web.databases.requests.promotions.PromotionsRequest
import com.blackmorse.hattid.web.databases.requests.teamdetails.{OldestTeamsRequest, TeamFanclubFlagsRequest, TeamPowerRatingsRequest, TeamStreakTrophiesRequest}
import com.blackmorse.hattid.web.databases.requests.{ClickhouseStatisticsRequest, OrderingKeyPath}
import com.blackmorse.hattid.web.zios.*
import com.blackmorse.hattid.web.models.web.HattidError
import com.blackmorse.hattid.web.models.web.divisionlevel.RestDivisionLevelData
import com.blackmorse.hattid.web.service.leagueinfo.{LeagueInfoServiceZIO, LeagueState}
import com.blackmorse.hattid.web.utils.{CurrencyUtils, Romans}
import zio.ZIO
import zio.http.*
import zio.json.*

object DivisionLevelRoutes {
  private val GetDivisionLevel: RoutePattern[(Int, Int)] = Method.GET / "api" / "league" / int("leagueId")  / "divisionLevel" / int("divisionLevel")

  val routes: Seq[Route[HattidEnv, HattidError]] = Seq(
    GetDivisionLevel -> divisionLevelDataHandler,
    GetDivisionLevel / "teamHatstats" -> statisticsHandler(TeamHatstatsRequest),
    GetDivisionLevel / "leagueUnits" -> statisticsHandler(LeagueUnitHatstatsRequest),
    GetDivisionLevel / "playerGoalGames" -> playerStatsHandler(PlayerGamesGoalsRequest),
    GetDivisionLevel / "playerCards" -> playerStatsHandler(PlayerCardsRequest),
    GetDivisionLevel / "playerTsiSalary" -> playerStatsHandler(PlayerSalaryTSIRequest),
    GetDivisionLevel / "playerRatings" -> playerStatsHandler(PlayerRatingsRequest),
    GetDivisionLevel / "playerInjuries" -> statisticsHandler(PlayerInjuryRequest),
    GetDivisionLevel / "teamSalaryTsi" -> teamSalaryTSIHandler,
    GetDivisionLevel / "teamCards" -> statisticsHandler(TeamCardsRequest),
    GetDivisionLevel / "teamRatings" -> statisticsHandler(TeamRatingsRequest),
    GetDivisionLevel / "teamAgeInjuries" -> statisticsHandler(TeamAgeInjuryRequest),
    GetDivisionLevel / "teamGoalPoints" -> teamGoalPointsHandler,
    GetDivisionLevel / "teamPowerRatings" -> statisticsHandler(TeamPowerRatingsRequest),
    GetDivisionLevel / "teamFanclubFlags" -> statisticsHandler(TeamFanclubFlagsRequest),
    GetDivisionLevel / "teamStreakTrophies" -> statisticsHandler(TeamStreakTrophiesRequest),
    GetDivisionLevel / "topMatches" -> statisticsHandler(MatchTopHatstatsRequest),
    GetDivisionLevel / "surprisingMatches" -> statisticsHandler(MatchSurprisingRequest),
    GetDivisionLevel / "matchSpectators" -> statisticsHandler(MatchSpectatorsRequest),
    GetDivisionLevel / "oldestTeams" -> statisticsHandler(OldestTeamsRequest),
    GetDivisionLevel / "promotions" -> promotionsHandler,
    GetDivisionLevel / "dreamTeam" -> dreamTeamHandler,
  )
  
  private def dreamTeamHandler = handler { (leagueId: Int, divisionLevel: Int, req: Request) =>
    for {
      sortBy    <- req.stringParam("sortBy")
      season    <- req.intParam("season")
      statsType <- req.statsType()
      dreamTeam <- DreamTeamRequest.execute(
        orderingKeyPath = OrderingKeyPath(season = Some(season),
          leagueId = Some(leagueId),
          divisionLevel = Some(divisionLevel)),
        statsType = statsType,
        sortBy = sortBy)
    } yield Response.json(dreamTeam.toJson)
  }
  
  private def promotionsHandler = handler { (leagueId: Int, divisionLevel: Int, req: Request) =>
    for {
      leagueInfoService <- ZIO.service[LeagueInfoServiceZIO]
      currentSeason     <- leagueInfoService.currentSeason(leagueId)
      entities          <- PromotionsRequest.execute(
        orderingKeyPath = OrderingKeyPath(leagueId = Some(leagueId),
          divisionLevel = Some(divisionLevel)),
        season = currentSeason)
    } yield Response.json(PromotionWithType.convert(entities).toJson)
  }
  
  private def teamGoalPointsHandler = handler { (leagueId: Int, divisionLevel: Int, req: Request) =>
    for {
      restStatisticsParameters <- req.restStatisticsParameters()
      playedAllMatches         <- req.boolParamWithDefault("playedAllMatches", false)
      oneTeamPerUnit           <- req.boolParam("oneTeamPerUnit")
      leagueInfoService        <- ZIO.service[LeagueInfoServiceZIO]
      currentRound             <- leagueInfoService.leagueRoundForSeason(leagueId, restStatisticsParameters.season)
      entities          <- TeamGoalPointsRequest.execute(
        orderingKeyPath = OrderingKeyPath(
          leagueId = Some(leagueId),
          divisionLevel = Some(divisionLevel)),
        parameters = restStatisticsParameters,
        playedAllMatches = playedAllMatches,
        currentRound = currentRound,
        oneTeamPerUnit = oneTeamPerUnit)
    } yield Response.json(restTableData(entities, restStatisticsParameters.pageSize).toJson)
  }
  
  private def teamSalaryTSIHandler = handler { (leagueId: Int, divisionLevel: Int, req: Request) =>
    for {
      restStatisticsParameters <- req.restStatisticsParameters()
      playedInLastMatch        <- req.boolParam("playedInLastMatch")
      excludeZeroTsi           <- req.boolParam("excludeZeroTsi")
      entities                 <- TeamSalaryTSIRequest.execute(
        orderingKeyPath = OrderingKeyPath(
          leagueId = Some(leagueId),
          divisionLevel = Some(divisionLevel)),
        parameters = restStatisticsParameters,
        playedInLastMatch = playedInLastMatch,
        excludeZeroTsi = excludeZeroTsi)
    } yield Response.json(restTableData(entities, restStatisticsParameters.pageSize).toJson)
  }

  private def playerStatsHandler[T : JsonEncoder](clickhouseRequest: ClickhousePlayerStatsRequest[T]) = handler { (leagueId: Int, divisionLevel: Int, req: Request) =>
    for {
      restStatisticsParameters <- req.restStatisticsParameters()
      playersParameters        <- req.playersParameters()
      entities                 <- clickhouseRequest.execute(
        OrderingKeyPath(
          leagueId = Some(leagueId),
          divisionLevel = Some(divisionLevel)),
        restStatisticsParameters,
        playersParameters)
    } yield Response.json(restTableData(entities, restStatisticsParameters.pageSize).toJson)
  }

  private def divisionLevelDataHandler = handler { (leagueId: Int, divisionLevel: Int, req: Request) =>
    for {
      leagueInfoService <- ZIO.service[LeagueInfoServiceZIO]
      leagueState <- leagueInfoService.leagueState(leagueId)
    } yield Response.json(createRestDivisionLevelData(leagueState, divisionLevel).toJson)
  }

  private def statisticsHandler[T : JsonEncoder](clickhouseRequest: ClickhouseStatisticsRequest[T]) = handler { (leagueId: Int, divisionLevel: Int, req: Request) =>
    for {
      restStatisticsParameters <- req.restStatisticsParameters()
      entities                 <- clickhouseRequest.execute(
        orderingKeyPath = OrderingKeyPath(
          leagueId = Some(leagueId),
          divisionLevel = Some(divisionLevel)),
        parameters = restStatisticsParameters)
    } yield Response.json(restTableData(entities, restStatisticsParameters.pageSize).toJson)

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
}
