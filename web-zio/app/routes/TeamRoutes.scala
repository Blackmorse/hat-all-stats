package routes

import chpp.commonmodels.MatchType
import chpp.teamdetails.models.Team
import databases.requests.matchdetails.{MatchSpectatorsRequest, MatchSurprisingRequest, MatchTopHatstatsRequest, TeamMatchesRequest}
import databases.requests.model.promotions.PromotionWithType
import databases.requests.{ClickhouseStatisticsRequest, OrderingKeyPath}
import databases.requests.playerstats.player.stats.{ClickhousePlayerStatsRequest, PlayerCardsRequest, PlayerGamesGoalsRequest, PlayerInjuryRequest, PlayerRatingsRequest, PlayerSalaryTSIRequest}
import databases.requests.promotions.PromotionsRequest
import databases.requests.teamrankings.TeamRankingsRequest
import models.web.rest.RestTeamData
import service.{ChppService, TeamsService}
import service.leagueinfo.LeagueInfoServiceZIO
import utils.{CurrencyUtils, Romans}
import zio.ZIO
import zio.http.*
import zio.json.*
import hattid.zio.*
import models.clickhouse.NearestMatch
import models.web.HattidError
import models.web.teams.RestTeamRankings
import webclients.{AuthConfig, ChppClient}

import java.util.Date

object TeamRoutes {
  private val GetTeam = Method.GET / "api" / "team" / long("teamId")

  val routes = Seq(
    GetTeam -> teamDataHandler,
    GetTeam / "teamRankings" -> teamRankingsHandler,
    GetTeam / "teamRankingsRange" -> teamRankingsRangeHandler,
    GetTeam / "playerGoalGames" -> playerStatsHandler(PlayerGamesGoalsRequest),
    GetTeam / "playerCards" -> playerStatsHandler(PlayerCardsRequest),
    GetTeam / "playerTsiSalary" -> playerStatsHandler(PlayerSalaryTSIRequest),
    GetTeam / "playerRatings" -> playerStatsHandler(PlayerRatingsRequest),
    GetTeam / "playerInjuries" -> statisticsHandler(PlayerInjuryRequest),
    GetTeam / "topMatches" -> statisticsHandler(MatchTopHatstatsRequest),
    GetTeam / "surprisingMatches" -> statisticsHandler(MatchSurprisingRequest),
    GetTeam / "matchSpectators" -> statisticsHandler(MatchSpectatorsRequest),
    GetTeam / "promotions" -> promotionsHandler,
    GetTeam / "teamMatches" -> teamMatchesHandler,
    Method.GET / "api" / "team" / "stats" / "teamsFoundedSameDate" -> teamsFoundedSameDateHandler,
    Method.GET / "api" / "team" / "stats" / "compareTeams" -> compareTeamsHandler,
  )
  
  

  private def compareTeamsHandler = handler { (req: Request) => 
    for {
      teamId1      <- req.longParam("teamId1")
      teamId2      <- req.longParam("teamId2")
      teamsService <- ZIO.service[TeamsService]
      comparison   <- teamsService.compareTwoTeams(teamId1, teamId2)
    } yield Response.json(comparison.toJson)
  }
  
  private def teamsFoundedSameDateHandler = handler { (req: Request) =>
    for {
      period       <- req.hattrickPeriod()
      leagueId     <- req.intParam("leagueId")
      foundedDate  <- req.longParam("foundedDate")
      teamsService <- ZIO.service[TeamsService]
      teams        <- teamsService.teamsCreatedSamePeriod(period, new Date(foundedDate), leagueId)
    } yield Response.json(teams.toJson)
  }
  
  private def teamMatchesHandler = handler { (teamId: Long, req: Request) =>
    for {
      season      <- req.intParam("season")
      chppService <- ZIO.service[ChppService]
      (team, _)   <- chppService.getTeamById(teamId)

      (divisionLevel, leagueUnitId) <- chppService.getDivisionLevelAndLeagueUnit(team, season)
      matches                       <- TeamMatchesRequest.execute(
        season = season,
        orderingKeyPath = orderingKeyPathFromTeam(
          team = team,
          divisionLevel = divisionLevel,
          leagueUnitId = leagueUnitId)
        )
    } yield Response.json(matches.toJson)
  }
  
  private def promotionsHandler = handler { (teamId: Long, req: Request) =>
    for {
      chppService       <- ZIO.service[ChppService]
      leagueInfoService <- ZIO.service[LeagueInfoServiceZIO]
      (team, _)         <- chppService.getTeamById(teamId)
      season            <- leagueInfoService.currentSeason(team.league.leagueId)

      (divisionLevel, leagueUnitId) <- chppService.getDivisionLevelAndLeagueUnit(team, season)
      promotions                    <- PromotionsRequest.execute(
        orderingKeyPath = orderingKeyPathFromTeam(team, divisionLevel, leagueUnitId),
        season = season)
    } yield Response.json(PromotionWithType.convert(promotions).toJson)
  }
  
  private def statisticsHandler[T : JsonEncoder](clickhouseRequest: ClickhouseStatisticsRequest[T]) = handler { (teamId: Long, req: Request) =>
    for {
      restStatisticsParameters      <- req.restStatisticsParameters()
      chppService                   <- ZIO.service[ChppService]
      (team, _)                     <- chppService.getTeamById(teamId)
      (divisionLevel, leagueUnitId) <- chppService.getDivisionLevelAndLeagueUnit(team, restStatisticsParameters.season)
      statList <- clickhouseRequest.execute(
        orderingKeyPath = orderingKeyPathFromTeam(team, divisionLevel, leagueUnitId),
        parameters = restStatisticsParameters)
    } yield Response.json(restTableData(statList, restStatisticsParameters.pageSize).toJson)
  }
  
  private def playerStatsHandler[T : JsonEncoder](clickhouseRequest: ClickhousePlayerStatsRequest[T]) = handler { (teamId: Long, req: Request) =>
    for {
      restStatisticsParameters       <- req.restStatisticsParameters()
      playersParameters              <- req.playersParameters()
      chppService                    <- ZIO.service[ChppService]
      (team, _)                      <- chppService.getTeamById(teamId)
      (divisionLevel, leagueUnitId)  <- chppService.getDivisionLevelAndLeagueUnit(team, restStatisticsParameters.season)
      statList                       <- clickhouseRequest.execute(
        orderingKeyPath = orderingKeyPathFromTeam(team, divisionLevel, leagueUnitId),
        parameters = restStatisticsParameters,
        playersParameters = playersParameters)
    } yield Response.json(restTableData(statList, restStatisticsParameters.pageSize).toJson)
  }

  private def orderingKeyPathFromTeam(team: Team, divisionLevel: Int, leagueUnitId: Long): OrderingKeyPath =
    OrderingKeyPath(
      leagueId = Some(team.league.leagueId),
      divisionLevel = Some(divisionLevel),
      leagueUnitId = Some(leagueUnitId),
      teamId = Some(team.teamId)
    )
  
  private def nearestMatchesHandler = handler { (teamId: Long, req: Request) =>
    for {
      chppService <- ZIO.service[ChppService]
      matches <- chppService.nearestMatches(teamId)
    } yield Response.json(matches.toJson)
  }
  
  private def teamRankingsRangeHandler = handler { (teamId: Long, req: Request) =>
    for {
      fromSeason <- req.intParam("fromSeason")
      toSeason           <- req.intParam("toSeason")
      chppService        <- ZIO.service[ChppService]
      leagueInfoService  <- ZIO.service[LeagueInfoServiceZIO]
      (team, _)          <- chppService.getTeamById(teamId)
      (league, rankings) <- leagueInfoService.leagueData(team.league.leagueId) <&>
        TeamRankingsRequest.execute(Some(fromSeason), Some(toSeason), team.league.leagueId, teamId)
    } yield {
      val currencyRate = CurrencyUtils.currencyRate(league.country)
      val currencyName = CurrencyUtils.currencyName(league.country)

      Response.json(
        RestTeamRankings(teamRankings = rankings,
          leagueTeamsCounts = Seq(),
          divisionLevelTeamsCounts = Seq(),
          currencyRate = currencyRate,
          currencyName = currencyName).toJson
        )
    }
  }
  
  private def teamRankingsHandler = handler { (teamId: Long, req: Request) =>
    for {
      chppService       <- ZIO.service[ChppService]
      leagueInfoService <- ZIO.service[LeagueInfoServiceZIO]
      (team, _)         <- chppService.getTeamById(teamId)
      leagueState       <- leagueInfoService.leagueState(team.league.leagueId)
      season            <- req.intParamOpt("season")
      currentSeason     <- leagueInfoService.currentSeason(team.league.leagueId)
      teamRankings      <- TeamRankingsRequest.execute(season, season, team.league.leagueId, teamId)

      selectedSeason           = season.getOrElse(currentSeason)
      divisionLevel            = teamRankings.map(_.divisionLevel).headOption.getOrElse(team.leagueLevelUnit.leagueLevel)
      leagueTeamsRoundToCounts <- leagueInfoService.numberOfTeamsForLeaguePerRound(team.league.leagueId, None, selectedSeason)
      divisionLevelTeamsCounts <- leagueInfoService.numberOfTeamsForLeaguePerRound(team.league.leagueId, Some(divisionLevel), selectedSeason)
    } yield {
      val currencyRate = CurrencyUtils.currencyRate(leagueState.league.country)
      val currencyName = CurrencyUtils.currencyName(leagueState.league.country)

      Response.json(
        RestTeamRankings(teamRankings = teamRankings,
          leagueTeamsCounts = leagueTeamsRoundToCounts,
          divisionLevelTeamsCounts = divisionLevelTeamsCounts,
          currencyRate = currencyRate,
          currencyName = currencyName).toJson
        )
    }
  }
  
  private def teamDataHandler = handler { (teamId: Long, req: Request) =>
    for {
      chppService  <- ZIO.service[ChppService]
      (team, _)    <- chppService.getTeamById(teamId)
      restTeamData <- getRestTeamData(team)
    } yield Response.json(restTeamData.toJson)
  }

  private def getRestTeamData(team: Team) = {
    for {
      leagueInfoService <- ZIO.service[LeagueInfoServiceZIO]
      leagueState <- leagueInfoService.leagueState(team.league.leagueId)
    } yield RestTeamData(
      leagueId = team.league.leagueId,
      leagueName = leagueState.league.englishName,
      divisionLevel = team.leagueLevelUnit.leagueLevel,
      divisionLevelName = Romans(team.leagueLevelUnit.leagueLevel),
      leagueUnitId = team.leagueLevelUnit.leagueLevelUnitId,
      leagueUnitName = team.leagueLevelUnit.leagueLevelUnitName,
      teamId = team.teamId,
      teamName = team.teamName,
      foundedDate = team.foundedDate.getTime,
      seasonOffset = leagueState.league.seasonOffset,
      seasonRoundInfo = leagueState.seasonRoundInfo,
      currency = CurrencyUtils.currencyName(leagueState.league.country),
      currencyRate = CurrencyUtils.currencyRate(leagueState.league.country),
      loadingInfo = leagueState.loadingInfo,
      countries = leagueState.idToCountryName
    )
  }
}
