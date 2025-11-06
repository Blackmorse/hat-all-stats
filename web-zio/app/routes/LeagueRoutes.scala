package routes

import controllers.RestLeagueData
import databases.requests.matchdetails.{LeagueUnitHatstatsRequest, MatchSpectatorsRequest, MatchSurprisingRequest, MatchTopHatstatsRequest, TeamGoalPointsRequest, TeamHatstatsRequest}
import databases.requests.model.promotions.PromotionWithType
import databases.requests.playerstats.dreamteam.DreamTeamRequest
import databases.requests.playerstats.player.stats.{ClickhousePlayerStatsRequest, PlayerCardsRequest, PlayerGamesGoalsRequest, PlayerInjuryRequest, PlayerRatingsRequest, PlayerSalaryTSIRequest}
import databases.requests.playerstats.team.{TeamAgeInjuryRequest, TeamCardsRequest, TeamRatingsRequest, TeamSalaryTSIRequest}
import databases.requests.promotions.PromotionsRequest
import databases.requests.teamdetails.{OldestTeamsRequest, TeamFanclubFlagsRequest, TeamPowerRatingsRequest, TeamStreakTrophiesRequest}
import databases.requests.{ClickhouseStatisticsRequest, OrderingKeyPath}
import hattid.zio.{DBServices, HattidEnv, boolParam, boolParamWithDefault, intParam, playersParameters, restStatisticsParameters, restTableData, statsType, stringParam}
import models.web.{HattidError, NotFoundError}
import service.leagueinfo.{LeagueInfoServiceZIO, LeagueState}
import utils.{CurrencyUtils, Romans}
import zio.ZIO
import zio.http.*
import zio.json.*

object LeagueRoutes {
  private val GetLeague: RoutePattern[Int] = Method.GET / "api" / "league" / int("leagueId")
  
  val routes: Seq[Route[HattidEnv, HattidError]] = Seq(
    GetLeague -> leagueDataHandler,
    GetLeague / "teamHatstats" -> statisticsHandler(TeamHatstatsRequest),
    GetLeague / "leagueUnits" -> statisticsHandler(LeagueUnitHatstatsRequest),
    GetLeague / "playerGoalGames" -> playerStatsHandler(PlayerGamesGoalsRequest),
    GetLeague / "playerCards" -> playerStatsHandler(PlayerCardsRequest),
    GetLeague / "playerTsiSalary" -> playerStatsHandler(PlayerSalaryTSIRequest),
    GetLeague / "playerRatings" -> playerStatsHandler(PlayerRatingsRequest),
    GetLeague / "playerInjuries" -> statisticsHandler(PlayerInjuryRequest),
    GetLeague / "teamSalaryTsi" -> teamSalaryTSIHandler,
    GetLeague / "teamCards" -> statisticsHandler(TeamCardsRequest),
    GetLeague / "teamRatings" -> statisticsHandler(TeamRatingsRequest),
    GetLeague / "teamAgeInjuries" -> statisticsHandler(TeamAgeInjuryRequest),
    GetLeague / "teamGoalPoints" -> teamGoalPointsHandler,

    GetLeague / "teamPowerRatings" -> statisticsHandler(TeamPowerRatingsRequest),
    GetLeague / "teamFanclubFlags" -> statisticsHandler(TeamFanclubFlagsRequest),
    GetLeague / "teamStreakTrophies" -> statisticsHandler(TeamStreakTrophiesRequest),
    GetLeague / "topMatches" -> statisticsHandler(MatchTopHatstatsRequest),
    GetLeague / "surprisingMatches" -> statisticsHandler(MatchSurprisingRequest),
    GetLeague / "matchSpectators" -> statisticsHandler(MatchSpectatorsRequest),
    GetLeague / "oldestTeams" -> statisticsHandler(OldestTeamsRequest),
    GetLeague / "promotions" -> promotionsHandler,
    GetLeague / "dreamTeam" -> dreamTeamHandler,
  )
  
  private def dreamTeamHandler = handler { (leagueId: Int, req: Request) =>
    for {
      season    <- req.intParam("season")
      statsType <- req.statsType()
      sortBy    <- req.stringParam("sortBy")
      entities  <- DreamTeamRequest.execute(
        orderingKeyPath = OrderingKeyPath(season = Some(season), leagueId = Some(leagueId)),
        statsType = statsType,
        sortBy = sortBy)
    } yield Response.json(entities.toJson)
  }
  
  private def promotionsHandler: Handler[DBServices & LeagueInfoServiceZIO, HattidError, (Int, Request), Response] = handler { (leagueId: Int, req: Request) =>
    for {
      leagueInfoService <- ZIO.service[LeagueInfoServiceZIO]
      currentSeason     <- leagueInfoService.currentSeason(leagueId)
      entities          <- PromotionsRequest.execute(
        orderingKeyPath = OrderingKeyPath(leagueId = Some(leagueId)),
        season = currentSeason)
    } yield Response.json(PromotionWithType.convert(entities).toJson)
  }

  private def teamGoalPointsHandler: Handler[DBServices & LeagueInfoServiceZIO, HattidError, (Int, Request), Response] = handler { (leagueId: Int, req: Request) =>
    for {
      leagueInfoService        <- ZIO.service[LeagueInfoServiceZIO]
      restStatisticsParameters <- req.restStatisticsParameters()
      playedAllMatches         <- req.boolParamWithDefault("playedAllMatches", false)
      oneTeamPerUnit           <- req.boolParam("oneTeamPerUnit")
      currentRound             <- leagueInfoService.lastRound(leagueId, restStatisticsParameters.season)
      entities                 <- TeamGoalPointsRequest.execute(
        orderingKeyPath = OrderingKeyPath(leagueId = Some(leagueId)),
        parameters = restStatisticsParameters,
        playedAllMatches = playedAllMatches,
        currentRound = currentRound,
        oneTeamPerUnit = oneTeamPerUnit)
    } yield Response.json(restTableData(entities, restStatisticsParameters.pageSize).toJson)
  }
  
  private def teamSalaryTSIHandler: Handler[DBServices, HattidError, (Int, Request), Response] = handler { (leagueId: Int, req: Request) =>
    for {
      restStatisticsParameters <- req.restStatisticsParameters()
      playedInLastMatch        <- req.boolParam("playedInLastMatch")
      excludeZeroTsi           <- req.boolParam("excludeZeroTsi")
      entities                 <- TeamSalaryTSIRequest.execute(
        orderingKeyPath = OrderingKeyPath(leagueId = Some(leagueId)),
        parameters = restStatisticsParameters,
        playedInLastMatch = playedInLastMatch,
        excludeZeroTsi = excludeZeroTsi)
    } yield Response.json(restTableData(entities, restStatisticsParameters.pageSize).toJson)
  }


  private def playerStatsHandler[T: JsonEncoder, R](clickhouseRequest: ClickhousePlayerStatsRequest[T]): Handler[DBServices, HattidError, (Int, Request), Response] = handler { (leagueId: Int, req: Request) =>
    for {
      restStatisticsParameters <- req.restStatisticsParameters()
      playersParameters        <- req.playersParameters()
      entities                 <- clickhouseRequest.execute(
        orderingKeyPath = OrderingKeyPath(leagueId = Some(leagueId)),
        parameters = restStatisticsParameters,
        playersParameters = playersParameters
      )
    } yield Response.json(restTableData(entities, restStatisticsParameters.pageSize).toJson)
  }


  private def statisticsHandler[T: JsonEncoder, R](clickhouseRequest: ClickhouseStatisticsRequest[T]): Handler[DBServices, HattidError, (Int, Request), Response] = handler { (leagueId: Int, req: Request) =>
    for {
      restStatisticsParameters <- req.restStatisticsParameters()
      entities                 <- clickhouseRequest.execute(
        orderingKeyPath = OrderingKeyPath(leagueId = Some(leagueId)),
        parameters = restStatisticsParameters
      )
    } yield Response.json(restTableData(entities, restStatisticsParameters.pageSize).toJson)
  }
  
  private def leagueDataHandler: Handler[LeagueInfoServiceZIO, NotFoundError, (Int, Request), Response] = handler { (leagueId: Int, req: Request) =>
    for {
      leagueInfoService <- ZIO.service[LeagueInfoServiceZIO]
      leagueState       <- leagueInfoService.leagueState(leagueId)
    } yield Response.json(createRestLeagueData(leagueState).toJson)
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
}
