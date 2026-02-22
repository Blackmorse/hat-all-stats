package com.blackmorse.hattid.web.routes

import chpp.leaguedetails.models.LeagueDetails
import com.blackmorse.hattid.web.zios.{CHPPServices, DBServices, HattidEnv, restTableData}
import com.blackmorse.hattid.web.databases.requests.matchdetails.chart.TeamHatstatsChartRequest
import com.blackmorse.hattid.web.databases.requests.matchdetails.{MatchSpectatorsRequest, MatchSurprisingRequest, MatchTopHatstatsRequest, TeamHatstatsRequest}
import com.blackmorse.hattid.web.databases.requests.model.Chart
import com.blackmorse.hattid.web.databases.requests.model.promotions.PromotionWithType
import com.blackmorse.hattid.web.databases.requests.playerstats.dreamteam.DreamTeamRequest
import com.blackmorse.hattid.web.databases.requests.playerstats.player.stats.*
import com.blackmorse.hattid.web.databases.requests.playerstats.team.chart.{TeamAgeInjuryChartRequest, TeamCardsChartRequest, TeamRatingsChartRequest, TeamSalaryTSIChartRequest}
import com.blackmorse.hattid.web.databases.requests.playerstats.team.{TeamAgeInjuryRequest, TeamCardsRequest, TeamRatingsRequest, TeamSalaryTSIRequest}
import com.blackmorse.hattid.web.databases.requests.promotions.PromotionsRequest
import com.blackmorse.hattid.web.databases.requests.teamdetails.chart.{TeamFanclubFlagsChartRequest, TeamPowerRatingsChartRequest, TeamStreakTrophiesChartRequest}
import com.blackmorse.hattid.web.databases.requests.teamdetails.{OldestTeamsRequest, TeamFanclubFlagsRequest, TeamPowerRatingsRequest, TeamStreakTrophiesRequest}
import com.blackmorse.hattid.web.databases.requests.teamrankings.ClickhouseChartRequest
import com.blackmorse.hattid.web.databases.requests.{ClickhouseStatisticsRequest, OrderingKeyPath}
import com.blackmorse.hattid.web.zios.*
import com.blackmorse.hattid.web.models.web.leagueUnit.RestLeagueUnitData
import com.blackmorse.hattid.web.models.web.{Desc, HattidError, HattidInternalError, RestTableData}
import com.blackmorse.hattid.web.service.ChppService
import com.blackmorse.hattid.web.service.leagueinfo.LeagueInfoServiceZIO
import com.blackmorse.hattid.web.service.leagueunit.{LeagueUnitCalculatorService, LeagueUnitTeamStat, LeagueUnitTeamStatHistoryInfo, LeagueUnitTeamStatsWithPositionDiff}
import zio.ZIO
import zio.http.*
import zio.json.*

object LeagueUnitRoutes {
  private type LeagueUnitParams = (Int, Request)
  
  private val GetLeagueUnit: RoutePattern[Int] = Method.GET / "api" / "leagueUnit" / int("leagueUnitId")

  val routes: Seq[Route[HattidEnv, HattidError]] = Seq(
    GetLeagueUnit -> getLeagueUnitData,
    GetLeagueUnit / "teamHatstats" -> statisticsHandler(TeamHatstatsRequest),
    GetLeagueUnit / "teamHatstatsChart" -> teamChartHandler(TeamHatstatsChartRequest),
    GetLeagueUnit / "teamPositions" -> teamPositionsHandler,
    GetLeagueUnit / "teamPositionsHistory" -> teamPositionsHistoryHandler,
    GetLeagueUnit / "playerGoalGames" -> playerStatsHandler(PlayerGamesGoalsRequest),
    GetLeagueUnit / "playerCards" -> playerStatsHandler(PlayerCardsRequest),
    GetLeagueUnit / "playerTsiSalary" -> playerStatsHandler(PlayerSalaryTSIRequest),
    GetLeagueUnit / "playerRatings" -> playerStatsHandler(PlayerRatingsRequest),
    GetLeagueUnit / "playerInjuries" -> statisticsHandler(PlayerInjuryRequest),
    GetLeagueUnit / "teamSalaryTsi" -> teamSalaryTSIHandler,
    GetLeagueUnit / "teamSalaryTsiChart" -> teamSalaryTSIChartHandler,
    GetLeagueUnit / "teamCards" -> statisticsHandler(TeamCardsRequest),
    GetLeagueUnit / "teamCardsChart" -> teamChartHandler(TeamCardsChartRequest),
    GetLeagueUnit / "teamRatings" -> statisticsHandler(TeamRatingsRequest),
    GetLeagueUnit / "teamRatingsChart" -> teamChartHandler(TeamRatingsChartRequest),
    GetLeagueUnit / "teamAgeInjuries" -> statisticsHandler(TeamAgeInjuryRequest),
    GetLeagueUnit / "teamAgeInjuriesChart" -> teamChartHandler(TeamAgeInjuryChartRequest),
    GetLeagueUnit / "teamPowerRatings" -> statisticsHandler(TeamPowerRatingsRequest),
    GetLeagueUnit / "teamPowerRatingsChart" -> teamChartHandler(TeamPowerRatingsChartRequest),
    GetLeagueUnit / "teamFanclubFlags" -> statisticsHandler(TeamFanclubFlagsRequest),
    GetLeagueUnit / "teamFanclubFlagsChart" -> teamChartHandler(TeamFanclubFlagsChartRequest),
    GetLeagueUnit / "teamStreakTrophies" -> statisticsHandler(TeamStreakTrophiesRequest),
    GetLeagueUnit / "teamStreakTrophiesChart" -> teamChartHandler(TeamStreakTrophiesChartRequest),
    GetLeagueUnit / "topMatches" -> statisticsHandler(MatchTopHatstatsRequest),
    GetLeagueUnit / "surprisingMatches" -> statisticsHandler(MatchSurprisingRequest),
    GetLeagueUnit / "matchSpectators" -> statisticsHandler(MatchSpectatorsRequest),
    GetLeagueUnit / "oldestTeams" -> statisticsHandler(OldestTeamsRequest),
    GetLeagueUnit / "promotions" -> promotionsHandler,
    GetLeagueUnit / "dreamTeam" -> dreamTeamHandler,
  )

  private def dreamTeamHandler = handler { (leagueUnitId: Int, req: Request) =>
    for {
      season        <- req.intParam("season")
      sortBy        <- req.stringParam("sortBy")
      statsType     <- req.statsType()
      chppService   <- ZIO.service[ChppService]
      leagueDetails <- chppService.leagueDetails(leagueUnitId)
      players       <- DreamTeamRequest.execute(
        orderingKeyPath = OrderingKeyPath(
          season = Some(season),
          leagueId = Some(leagueDetails.leagueId),
          divisionLevel = Some(leagueDetails.leagueLevel),
          leagueUnitId = Some(leagueUnitId)),
        statsType = statsType,
        sortBy = sortBy
      )
    } yield Response.json(players.toJson)
  }

  private def promotionsHandler = handler { (leagueUnitId: Int, req: Request) =>
    for {
      leagueInfoService <- ZIO.service[LeagueInfoServiceZIO]
      chppService       <- ZIO.service[ChppService]
      leagueDetails     <- chppService.leagueDetails(leagueUnitId)
      currenSeason      <- leagueInfoService.currentSeason(leagueDetails.leagueId)
      promotions        <- PromotionsRequest.execute(
        orderingKeyPath = OrderingKeyPath(
          leagueId = Some(leagueDetails.leagueId),
          divisionLevel = Some(leagueDetails.leagueLevel),
          leagueUnitId = Some(leagueUnitId)),
        season = currenSeason)
    } yield Response.json(PromotionWithType.convert(promotions).toJson)
  }

  private def teamSalaryTSIChartHandler = handler { (leagueUnitId: Int, req: Request) =>
    for {
      season                   <- req.intParam("season")
      playedInLastMatch        <- req.boolParam("playedInLastMatch")
      excludeZeroTsi           <- req.boolParam("excludeZeroTsi")
      chppService              <- ZIO.service[ChppService]
      leagueDetails            <- chppService.leagueDetails(leagueUnitId)
      entities                 <- TeamSalaryTSIChartRequest.execute(
        orderingKeyPath = OrderingKeyPath(
          leagueId = Some(leagueDetails.leagueId),
          divisionLevel = Some(leagueDetails.leagueLevel),
          leagueUnitId = Some(leagueUnitId)
        ),
        season = season,
        playedInLastMatch = playedInLastMatch,
        excludeZeroTsi = excludeZeroTsi)
    } yield Response.json(entities.toJson)
  }

  private def teamSalaryTSIHandler = handler { (leagueUnitId: Int, req: Request) =>
    for {
      restStatisticsParameters <- req.restStatisticsParameters()
      playedInLastMatch        <- req.boolParam("playedInLastMatch")
      excludeZeroTsi           <- req.boolParam("excludeZeroTsi")
      chppService   <- ZIO.service[ChppService]
      leagueDetails <- chppService.leagueDetails(leagueUnitId)
      entities <- TeamSalaryTSIRequest.execute(
          OrderingKeyPath(leagueId = Some(leagueDetails.leagueId),
            divisionLevel = Some(leagueDetails.leagueLevel),
            leagueUnitId = Some(leagueUnitId)),
          restStatisticsParameters,
          playedInLastMatch = playedInLastMatch,
          excludeZeroTsi = excludeZeroTsi
        )
    } yield Response.json(restTableData(entities, restStatisticsParameters.pageSize).toJson)
  }
  
  private def playerStatsHandler[T: JsonEncoder](clickhouseRequest: ClickhousePlayerStatsRequest[T]): Handler[DBServices & CHPPServices, HattidError, LeagueUnitParams, Response] = handler { (leagueUnitId: Int, req: Request) =>
    for {
      restStatisticsParameters <- req.restStatisticsParameters()
      playersParameters        <- req.playersParameters()
      chppService              <- ZIO.service[ChppService]
      leagueDetails            <- chppService.leagueDetails(leagueUnitId)
      entities                 <- clickhouseRequest.execute(
        orderingKeyPath = OrderingKeyPath(
          leagueId = Some(leagueDetails.leagueId),
          divisionLevel = Some(leagueDetails.leagueLevel),
          leagueUnitId = Some(leagueUnitId)),
        parameters = restStatisticsParameters,
        playersParameters = playersParameters)
    } yield Response.json(restTableData(entities, restStatisticsParameters.pageSize).toJson)
  }

  private def teamPositionsHandler: Handler[CHPPServices & LeagueInfoServiceZIO & LeagueUnitCalculatorService, HattidError, LeagueUnitParams, Response] = handler { (leagueUnitId: Int, req: Request) =>
    for {
      restStatisticsParameters <- req.restStatisticsParameters()
      season = restStatisticsParameters.season
      positionsWithDiff <- teamPositionsInternal(leagueUnitId, season, _.teamsLastRoundWithPositionsDiff, lastRoundPositionsDiffFromLeagueDetails)
    } yield Response.json(RestTableData(positionsWithDiff, isLastPage = true).toJson)
  }

  private def teamPositionsHistoryHandler = handler { (leagueUnitId: Int, req: Request) =>
    for {
      season        <- req.intParam("season")
      teamPositions <- teamPositionsInternal(leagueUnitId, season, _.positionsHistory, _ => Seq())
    } yield Response.json(teamPositions.toJson)
  }

  private def teamPositionsInternal[T](leagueUnitId: Int,
                                       season: Int,
                                       resultFunc: LeagueUnitTeamStatHistoryInfo => Seq[T],
                                       fallbackResultFunc: LeagueDetails => Seq[T]): ZIO[CHPPServices & LeagueInfoServiceZIO & LeagueUnitCalculatorService, HattidError, Seq[T]] = {
    for {
      chppService <- ZIO.service[ChppService]
      leagueDetails <- chppService.leagueDetails(leagueUnitId)
      history <- historyInfo(leagueDetails, leagueUnitId, season).map(resultFunc)
        .onError(_ => ZIO.succeed(fallbackResultFunc(leagueDetails)))
    } yield history
  }

  private def historyInfo[T](leagueDetails: LeagueDetails, leagueUnitId: Int, season: Int): ZIO[CHPPServices & LeagueInfoServiceZIO & LeagueUnitCalculatorService, HattidError, LeagueUnitTeamStatHistoryInfo] = {
    val leagueId = leagueDetails.leagueId
    for {
      leagueUnitCalculatorService <- ZIO.service[LeagueUnitCalculatorService]
      chppService                 <- ZIO.service[ChppService]
      leagueInfoService           <- ZIO.service[LeagueInfoServiceZIO]
      offsettedSeason             <- leagueInfoService.getRelativeSeasonFromAbsolute(season, leagueId)
      leagueFixture               <- chppService.leagueFixtures(leagueUnitId, offsettedSeason)
      currentSeason               <- leagueInfoService.currentSeason(leagueId)
      round                       <- if (currentSeason == season) leagueInfoService.lastRound(leagueId, season) else ZIO.succeed(14)
      leagueUnitTeamHistoryInfo   <- ZIO.fromEither(leagueUnitCalculatorService.calculateSafe(leagueFixture, Some(round),
          /* doesn't matter what parameters are there */ "points", Desc))
        .mapError(u => HattidInternalError("Unable to calculate league unit team history info"))
    } yield leagueUnitTeamHistoryInfo
  }

  private def lastRoundPositionsDiffFromLeagueDetails(leagueDetails: LeagueDetails): Seq[LeagueUnitTeamStatsWithPositionDiff] = {
    leagueDetails.teams.map { team =>
      LeagueUnitTeamStatsWithPositionDiff(
        positionDiff = team.positionChange,
        leagueUnitTeamStat = LeagueUnitTeamStat(
          round = leagueDetails.currentMatchRound,
          position = team.position,
          teamId = team.teamId,
          teamName = team.teamName,
          games = team.matches,
          scored = team.goalsFor,
          missed = team.goalsAgainst,
          win = team.won,
          draw = team.draws,
          lost = team.lost,
          points = team.points
        )
      )
    }
  }

  private def getLeagueUnitData: Handler[CHPPServices & LeagueInfoServiceZIO, HattidError, (Int, Request), Response] = handler { (leagueUnitId: Int, req: Request) =>
    for {
      chppService       <- ZIO.service[ChppService]
      leagueInfoService <- ZIO.service[LeagueInfoServiceZIO]
      leagueDetails     <- chppService.leagueDetails(leagueUnitId)
      leagueState       <- leagueInfoService.leagueState(leagueDetails.leagueId)
    } yield Response.json(RestLeagueUnitData(leagueDetails, leagueState, leagueUnitId).toJson)
  }

  private def statisticsHandler[T: JsonEncoder, R](clickhouseRequest: ClickhouseStatisticsRequest[T]) = handler { (leagueUnitId: Int, req: Request) =>
    for {
      restStatisticsParameters <- req.restStatisticsParameters()
      chppService              <- ZIO.service[ChppService]
      leagueDetails            <- chppService.leagueDetails(leagueUnitId)
      entities                 <- clickhouseRequest.execute(
        orderingKeyPath = OrderingKeyPath(leagueId = Some(leagueDetails.leagueId),
          divisionLevel = Some(leagueDetails.leagueLevel),
          leagueUnitId = Some(leagueUnitId)),
        parameters = restStatisticsParameters)
    } yield Response.json(restTableData(entities, restStatisticsParameters.pageSize).toJson)
  }

  private def teamChartHandler[T <: Chart : JsonEncoder](chartRequest: ClickhouseChartRequest[T]) = handler { (leagueUnitId: Int, req: Request) =>
    for {
      season        <- req.intParam("season")
      chppService   <- ZIO.service[ChppService]
      leagueDetails <- chppService.leagueDetails(leagueUnitId)
      entities      <- chartRequest.execute(
        orderingKeyPath = OrderingKeyPath(
          leagueId = Some(leagueDetails.leagueId),
          divisionLevel = Some(leagueDetails.leagueLevel),
          leagueUnitId = Some(leagueUnitId)),
        season = season)
    } yield Response.json(entities.toJson)
  }
}
