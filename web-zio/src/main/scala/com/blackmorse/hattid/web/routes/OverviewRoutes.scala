package com.blackmorse.hattid.web.routes

import com.blackmorse.hattid.web.zios.DBServices
import com.blackmorse.hattid.web.databases.ClickhousePool.ClickhousePool
import com.blackmorse.hattid.web.databases.dao.RestClickhouseDAO
import com.blackmorse.hattid.web.databases.requests.OrderingKeyPath
import com.blackmorse.hattid.web.databases.requests.model.overview.TotalOverview
import com.blackmorse.hattid.web.databases.requests.overview.charts.*
import hattid.CommonData
import com.blackmorse.hattid.web.zios.*
import com.blackmorse.hattid.web.models.web.HattidError
import com.blackmorse.hattid.web.models.web.world.{WorldData, WorldLoadingInfo}
import com.blackmorse.hattid.web.service.RestOverviewStatsService
import com.blackmorse.hattid.web.service.leagueinfo.LeagueInfoServiceZIO
import zio.*
import zio.http.*
import zio.json.*

object OverviewRoutes {
  private val GetOverview = Method.GET / "api" / "overview"
 
  val routes: Seq[Route[HattidEnv, HattidError]] = Seq(
    GetOverview / "numberOverview" -> overviewHandler(_.numberOverview),
    GetOverview / "formations" -> overviewHandler(_.formations),
    GetOverview / "averagesOverview" -> overviewHandler(_.averageOverview),
    GetOverview / "surprisingMatches" -> overviewHandler(_.surprisingMatches),
    GetOverview / "topHatstatsTeams" -> overviewHandler(_.topHatstatsTeams),
    GetOverview / "topSalaryTeams" -> overviewHandler(_.topSalaryTeams),
    GetOverview / "topMatches" -> overviewHandler(_.topMatches),
    GetOverview / "topSalaryPlayers" -> overviewHandler(_.topSalaryPlayers),
    GetOverview / "topRatingPlayers" -> overviewHandler(_.topRatingPlayers),
    GetOverview / "totalOverview" -> totalOverviewHandler,
    GetOverview / "matchAttendance" -> overviewHandler(_.topMatchAttendance),
    GetOverview / "topVictories" -> overviewHandler(_.topTeamVictories),
    GetOverview / "topSeasonScorers" -> overviewHandler(_.topSeasonScorers),
    GetOverview / "worldData" -> worldDataHandler,
    GetOverview / "teamNumbersChart" -> numbersChartHandler(TeamsNumberOverviewChartRequest),
    GetOverview / "playerNumbersChart" -> numbersChartHandler(PlayersNumberOverviewChartRequest),
    GetOverview / "goalNumbersChart" -> numbersChartHandler(GoalsNumberOverviewChartRequest),
    GetOverview / "injuryNumbersChart" -> numbersChartHandler(InjuriesNumberOverviewChartRequest),
    GetOverview / "yellowCardNumbersChart" -> numbersChartHandler(YellowCardsNumberOverviewRequest),
    GetOverview / "redCardNumbersChart" -> numbersChartHandler(RedCardsNumberOverviewRequest),
    GetOverview / "formationsChart" -> formationsChartHandler,
    GetOverview / "averageHatstatNumbersChart" -> numbersChartHandler(AverageHatstatsChartRequest),
    GetOverview / "averageSpectatorNumbersChart" -> numbersChartHandler(AverageSpectatorsChartRequest),
    GetOverview / "averageGoalNumbersChart" -> numbersChartHandler(AverageGoalsChartRequest),
    GetOverview / "newTeamNumbersChart" -> newTeamNumbersChartHandler,
  )
  
  private def newTeamNumbersChartHandler = handler { (req: Request) =>
    for {
      leagueId          <- req.intParamOpt("leagueId")
      divisionLevel     <- req.intParamOpt("divisionLevel")
      leagueInfoService <- ZIO.service[LeagueInfoServiceZIO]
      leagueIdEffective = leagueId.getOrElse(CommonData.LAST_SERIES_LEAGUE_ID)
      currentSeason     <- leagueInfoService.currentSeason(leagueIdEffective)
      currentRound      <- leagueInfoService.lastRound(leagueIdEffective, season = currentSeason)
      entities          <- NewTeamsNumberChartRequest.execute(
        orderingKeyPath = OrderingKeyPath(leagueId = leagueId, divisionLevel = divisionLevel),
        currentRound = currentRound,
        currentSeason = currentSeason)
    } yield Response.json(entities.toJson)
  }
  
  private def formationsChartHandler = handler { (req: Request) =>
    for {
      leagueId          <- req.intParamOpt("leagueId")
      divisionLevel     <- req.intParamOpt("divisionLevel")
      leagueInfoService <- ZIO.service[LeagueInfoServiceZIO]
      leagueIdEffective = leagueId.getOrElse(CommonData.LAST_SERIES_LEAGUE_ID)
      currentSeason     <- leagueInfoService.currentSeason(leagueIdEffective)
      currentRound      <- leagueInfoService.lastRound(leagueIdEffective, season = currentSeason)
      entities          <- FormationsChartRequest.execute(
        orderingKeyPath = OrderingKeyPath(leagueId = leagueId, divisionLevel = divisionLevel),
        currentRound = currentRound,
        currentSeason = currentSeason)
    } yield Response.json(entities.toJson)
  }
  
  private def numbersChartHandler(clickhouseRequest: NumbersOverviewChartRequest) = handler { (req: Request) =>
    for {
      leagueId          <- req.intParamOpt("leagueId")
      divisionLevel     <- req.intParamOpt("divisionLevel")
      leagueInfoService <- ZIO.service[LeagueInfoServiceZIO]
      leagueIdEffective = leagueId.getOrElse(CommonData.LAST_SERIES_LEAGUE_ID)
      currentSeason     <- leagueInfoService.currentSeason(leagueIdEffective)
      currentRound      <- leagueInfoService.lastRound(leagueIdEffective, season = currentSeason)
      entities          <- clickhouseRequest.execute(
        orderingKeyPath = OrderingKeyPath(leagueId = leagueId, divisionLevel = divisionLevel),
        currentRound = currentRound,
        currentSeason = currentSeason)
    } yield Response.json(entities.toJson)
  }
  
  private def worldDataHandler = handler { (req: Request) =>
    for {
      leagueInfoService             <- ZIO.service[LeagueInfoServiceZIO]
      idToStringCountry             <- leagueInfoService.idToStringCountryMap
      processedCountriesNumber      <- leagueInfoService.getProcessedCountriesNumber
      (nextCountry, currentCountry) <- leagueInfoService.getNextAndCurrentCountry
      countries                     <- leagueInfoService.countriesNumber
      seasonRoundInfo               <- leagueInfoService.seasonRoundInfo(CommonData.LAST_SERIES_LEAGUE_ID)
    } yield {
      val worldData = WorldData(
        countries = idToStringCountry,
        seasonOffset = 0,
        seasonRoundInfo = seasonRoundInfo,
        currency = "$",
        currencyRate = 10.0d,
        loadingInfo = if (countries == processedCountriesNumber) None
        else Some(WorldLoadingInfo(processedCountriesNumber, nextCountry, currentCountry)),
        isWorldData = "true"
      )
      Response.json(worldData.toJson)
    }
  }
  
  private def totalOverviewHandler = handler { (req: Request) =>
    for {
      season        <- req.intParam("season")
      round         <- req.intParam("round")
      leagueId      <- req.intParamOpt("leagueId")
      divisionLevel <- req.intParamOpt("divisionLevel")
      restOverviewStatsService     <- ZIO.service[RestOverviewStatsService]
      leagueSetButNotExists        <- leagueSetButNotExists(leagueId)
      divisionLevelSetButNotExists <- divisionLevelSetButNotExists(leagueId, divisionLevel, season, round)
      //in case divisionLevel or league is Empty - return nothing
      res                          <- if (leagueSetButNotExists || divisionLevelSetButNotExists) ZIO.succeed(TotalOverview.empty())
                             else restOverviewStatsService.totalOverview(season, round, leagueId, divisionLevel)

    } yield Response.json(res.toJson)
  }

  private def leagueSetButNotExists(leagueId: Option[Int]): ZIO[LeagueInfoServiceZIO, Nothing, Boolean] =
    leagueId.map(id => ZIO.serviceWithZIO[LeagueInfoServiceZIO](serv => !serv.leagueExists(id))).getOrElse(ZIO.succeed(false))

  private def divisionLevelSetButNotExists(leagueId: Option[Int], divisionLevel: Option[Int], season: Int, round: Int): ZIO[LeagueInfoServiceZIO, Nothing, Boolean] =
    (leagueId, divisionLevel) match {
      case (Some(lId), Some(dLevel)) => ZIO.serviceWithZIO[LeagueInfoServiceZIO](serv => !serv.divisionLevelExists(lId, season, round, dLevel))
      case _ => ZIO.succeed(false)
    }
  
  private def overviewHandler[T : JsonEncoder](overviewFunc: (service: RestOverviewStatsService) => (season: Int, round: Int,
                                                                                                     leagueId: Option[Int], divisionLevel: Option[Int]) => ZIO[DBServices, HattidError, T]) 
  = handler { (req: Request) =>
    for {
      season        <- req.intParam("season")
      round         <- req.intParam("round")
      leagueId      <- req.intParamOpt("leagueId")
      divisionLevel <- req.intParamOpt("divisionLevel")
      service       <- ZIO.service[RestOverviewStatsService]
      result        <- overviewFunc(service)(season, round, leagueId, divisionLevel)
    } yield Response.json(result.toJson)
  }
}
