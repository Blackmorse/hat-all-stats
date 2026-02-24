package controllers

import cache.ZioCacheModule.HattidEnv
import chpp.teamdetails.models.Team
import databases.requests.matchdetails.{MatchSpectatorsRequest, MatchSurprisingRequest, MatchTopHatstatsRequest, TeamMatchesRequest}
import databases.requests.model.promotions.PromotionWithType
import databases.requests.playerstats.player.stats.*
import databases.requests.promotions.PromotionsRequest
import databases.requests.teamrankings.TeamRankingsRequest
import databases.requests.{ClickhouseStatisticsRequest, OrderingKeyPath}
import models.clickhouse.NearestMatch
import models.web.rest.RestTeamData
import models.web.teams.RestTeamRankings
import models.web.{PlayersParameters, RestStatisticsParameters}
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import service.leagueinfo.LeagueInfoServiceZIO
import service.{ChppService, HattrickPeriod, TeamsService}
import utils.{CurrencyUtils, Romans}
import zio.ZIO

import java.util.Date
import javax.inject.Inject
import play.api.libs.json.Writes.*
import play.api.libs.json.*
import zio.json.{DeriveJsonEncoder, JsonEncoder}

case class NearestMatches(playedMatches: Seq[NearestMatch], upcomingMatches: Seq[NearestMatch])

object NearestMatches {
  implicit val writes: OWrites[NearestMatches] = Json.writes[NearestMatches]
  implicit val jsonEncoder: JsonEncoder[NearestMatches] = DeriveJsonEncoder.gen[NearestMatches]
}

class RestTeamController @Inject() (val controllerComponents: ControllerComponents,
                                    val teamsService: TeamsService,
                                    val hattidEnvironment: zio.ZEnvironment[HattidEnv]) extends RestController(hattidEnvironment) {

  private def getRestTeamData(team: Team) = {
    for {
      leagueInfoService <- ZIO.service[LeagueInfoServiceZIO]
      leagueState       <- leagueInfoService.leagueState(team.league.leagueId)
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

  def getTeamData(teamId: Long): Action[AnyContent] = asyncZio {
    ZIO.serviceWithZIO[ChppService](_.getTeamById(teamId) map (_._1) flatMap getRestTeamData)
  }

  private def orderingKeyPathFromTeam(team: Team, divisionLevel: Int, leagueUnitId: Long): OrderingKeyPath =
    OrderingKeyPath(
      leagueId = Some(team.league.leagueId),
      divisionLevel = Some(divisionLevel),
      leagueUnitId = Some(leagueUnitId),
      teamId = Some(team.teamId)
    )

  private def stats[T : Writes](chRequest: ClickhouseStatisticsRequest[T],
                       teamId: Long,
                       restStatisticsParameters: RestStatisticsParameters): Action[AnyContent] = asyncZio {
    for {
      chppService <- ZIO.service[ChppService]
      (team, _)   <- chppService.getTeamById(teamId)
      (divisionLevel, leagueUnitId) <- chppService.getDivisionLevelAndLeagueUnit(team, restStatisticsParameters.season)
      statList <- chRequest.execute(orderingKeyPath = orderingKeyPathFromTeam(team, divisionLevel, leagueUnitId),
                     parameters = restStatisticsParameters)
    } yield restTableData(statList, restStatisticsParameters.pageSize)
  }

  private def playersRequest[T : Writes](plRequest: ClickhousePlayerStatsRequest[T],
                                teamId: Long,
                                restStatisticsParameters: RestStatisticsParameters,
                                playersParameters: PlayersParameters) = asyncZio {
    for {
      chppService <- ZIO.service[ChppService]
      (team, _)   <- chppService.getTeamById(teamId)
      (divisionLevel, leagueUnitId)  <- chppService.getDivisionLevelAndLeagueUnit(team, restStatisticsParameters.season)
      statList                       <- plRequest.execute(orderingKeyPath = orderingKeyPathFromTeam(team, divisionLevel, leagueUnitId),
                                            parameters = restStatisticsParameters,
                                            playersParameters = playersParameters)
    } yield restTableData(statList, restStatisticsParameters.pageSize)
  }

  def playerGoalGames(teamId: Long, restStatisticsParameters: RestStatisticsParameters, playersParameters: PlayersParameters): Action[AnyContent] =
    playersRequest(PlayerGamesGoalsRequest, teamId, restStatisticsParameters, playersParameters)

  def playerCards(teamId: Long, restStatisticsParameters: RestStatisticsParameters,
                  playersParameters: PlayersParameters): Action[AnyContent] =
    playersRequest(PlayerCardsRequest, teamId, restStatisticsParameters, playersParameters)

  def playerTsiSalary(teamId: Long, restStatisticsParameters: RestStatisticsParameters, playersParameters: PlayersParameters): Action[AnyContent] =
    playersRequest(PlayerSalaryTSIRequest, teamId, restStatisticsParameters, playersParameters)

  def playerRatings(teamId: Long, restStatisticsParameters: RestStatisticsParameters, playersParameters: PlayersParameters): Action[AnyContent] =
    playersRequest(PlayerRatingsRequest, teamId, restStatisticsParameters, playersParameters)

  def playerInjuries(teamId: Long, restStatisticsParameters: RestStatisticsParameters): Action[AnyContent] =
    stats(PlayerInjuryRequest, teamId, restStatisticsParameters)

  def topMatches(teamId: Long, restStatisticsParameters: RestStatisticsParameters): Action[AnyContent] =
    stats(MatchTopHatstatsRequest, teamId, restStatisticsParameters)

  def surprisingMatches(teamId: Long, restStatisticsParameters: RestStatisticsParameters): Action[AnyContent] =
    stats(MatchSurprisingRequest, teamId, restStatisticsParameters)

  def matchSpectators(teamId: Long, restStatisticsParameters: RestStatisticsParameters): Action[AnyContent] =
    stats(MatchSpectatorsRequest, teamId, restStatisticsParameters)

  def teamRankingsRange(teamId: Long, fromSeason: Int, toSeason: Int): Action[AnyContent] = asyncZio {
    for {
      chppService        <- ZIO.service[ChppService]
      leagueInfoService  <- ZIO.service[LeagueInfoServiceZIO]
      (team, _)          <- chppService.getTeamById(teamId)
      (league, rankings) <- leagueInfoService.leagueData(team.league.leagueId) <&>
        TeamRankingsRequest.execute(Some(fromSeason), Some(toSeason), team.league.leagueId, teamId)
    } yield {
      val currencyRate = CurrencyUtils.currencyRate(league.country)
      val currencyName = CurrencyUtils.currencyName(league.country)

      RestTeamRankings(teamRankings = rankings,
        leagueTeamsCounts = Seq(),
        divisionLevelTeamsCounts = Seq(),
        currencyRate = currencyRate,
        currencyName = currencyName)
    }
  }

  def teamRankings(teamId: Long, season: Option[Int]): Action[AnyContent] = asyncZio {
    for {
      chppService  <- ZIO.service[ChppService]
      leagueInfoService <- ZIO.service[LeagueInfoServiceZIO]
      (team, _)     <- chppService.getTeamById(teamId)
      leagueState   <- leagueInfoService.leagueState(team.league.leagueId)
      currentSeason <- leagueInfoService.currentSeason(team.league.leagueId)
      teamRankings  <- TeamRankingsRequest.execute(season, season, team.league.leagueId, teamId)
      
      selectedSeason = season.getOrElse(currentSeason)
      divisionLevel  = teamRankings.map(_.divisionLevel).headOption.getOrElse(team.leagueLevelUnit.leagueLevel)
      leagueTeamsRoundToCounts <- leagueInfoService.numberOfTeamsForLeaguePerRound(team.league.leagueId, None, selectedSeason)
      divisionLevelTeamsCounts <- leagueInfoService.numberOfTeamsForLeaguePerRound(team.league.leagueId, Some(divisionLevel), selectedSeason)
    } yield {
      val currencyRate = CurrencyUtils.currencyRate(leagueState.league.country)
      val currencyName = CurrencyUtils.currencyName(leagueState.league.country)

      RestTeamRankings(teamRankings = teamRankings,
        leagueTeamsCounts = leagueTeamsRoundToCounts,
        divisionLevelTeamsCounts = divisionLevelTeamsCounts,
        currencyRate = currencyRate,
        currencyName = currencyName)
    }
  }

  def nearestMatches(teamId: Long): Action[AnyContent] = asyncZio {
    for {
      chppService <- ZIO.service[ChppService]
      matches     <- chppService.nearestMatches(teamId)
    } yield matches
  }

  def promotions(teamId: Long): Action[AnyContent] = asyncZio {
    for {
      chppService       <- ZIO.service[ChppService]
      leagueInfoService <- ZIO.service[LeagueInfoServiceZIO]
      (team, _)         <- chppService.getTeamById(teamId)
      season            <- leagueInfoService.currentSeason(team.league.leagueId)
      
      (divisionLevel, leagueUnitId) <- chppService.getDivisionLevelAndLeagueUnit(team, season)
      promotions                    <- PromotionsRequest.execute(orderingKeyPathFromTeam(team, divisionLevel, leagueUnitId), season)
    } yield PromotionWithType.convert(promotions)
  }

  def teamMatches(teamId: Long, season: Int): Action[AnyContent] = asyncZio {
    for {
      chppService <- ZIO.service[ChppService]
      (team, _)   <- chppService.getTeamById(teamId)
      
      (divisionLevel, leagueUnitId)  <- chppService.getDivisionLevelAndLeagueUnit(team, season)
      matches                        <- TeamMatchesRequest.execute(season = season,
                                             orderingKeyPath = orderingKeyPathFromTeam(team = team,
                                               divisionLevel = divisionLevel,
                                               leagueUnitId = leagueUnitId))
    } yield matches
  }


  def teamsFoundedSameDate(period: HattrickPeriod, leagueId: Int, foundedDate: Long): Action[AnyContent] = asyncZio {
    teamsService.teamsCreatedSamePeriod(period, new Date(foundedDate), leagueId)
  }

  def compareTeams(team1: Long, team2: Long): Action[AnyContent] = asyncZio {
    teamsService.compareTwoTeams(team1, team2)
  }
}
  
