package controllers

import cache.ZioCacheModule.HattidEnv
import chpp.leaguedetails.models.LeagueDetails
import chpp.search.SearchRequest
import chpp.search.models.SearchType
import databases.requests.matchdetails.chart.TeamHatstatsChartRequest
import databases.requests.matchdetails.{MatchSpectatorsRequest, MatchSurprisingRequest, MatchTopHatstatsRequest, TeamHatstatsRequest}
import databases.requests.model.Chart
import databases.requests.model.promotions.PromotionWithType
import databases.requests.playerstats.dreamteam.DreamTeamRequest
import databases.requests.playerstats.player.stats.*
import databases.requests.playerstats.team.*
import databases.requests.playerstats.team.chart.{TeamAgeInjuryChartRequest, TeamCardsChartRequest, TeamRatingsChartRequest, TeamSalaryTSIChartRequest}
import databases.requests.promotions.PromotionsRequest
import databases.requests.teamdetails.chart.{TeamFanclubFlagsChartRequest, TeamPowerRatingsChartRequest, TeamStreakTrophiesChartRequest}
import databases.requests.teamdetails.{OldestTeamsRequest, TeamFanclubFlagsRequest, TeamPowerRatingsRequest, TeamStreakTrophiesRequest}
import databases.requests.teamrankings.ClickhouseChartRequest
import databases.requests.{ClickhouseStatisticsRequest, OrderingKeyPath}
import hattid.CommonData
import models.web.*
import models.web.leagueUnit.RestLeagueUnitData
import play.api.libs.json.{JsValue, Json, OWrites, Writes}
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import service.ChppService
import service.leagueinfo.LeagueInfoServiceZIO
import service.leagueunit.{LeagueUnitCalculatorService, LeagueUnitTeamStat, LeagueUnitTeamStatHistoryInfo, LeagueUnitTeamStatsWithPositionDiff}
import utils.{LeagueNameParser, Romans}
import zio.ZIO

import javax.inject.Inject


class RestLeagueUnitController @Inject() (val controllerComponents: ControllerComponents,
                                          val leagueUnitCalculatorService: LeagueUnitCalculatorService,
                                          val hattidEnvironment: zio.ZEnvironment[HattidEnv]) extends RestController(hattidEnvironment) {
  case class LongWrapper(id: Long)
  implicit val writes: OWrites[LongWrapper] = Json.writes[LongWrapper]

  def leagueUnitIdByName(leagueUnitName: String, leagueId: Int): Action[AnyContent] = asyncZio {
    findLeagueUnitIdByName(leagueUnitName, leagueId)
      .map(id => LongWrapper(id))
  }

  private def findLeagueUnitIdByName(leagueUnitName: String, leagueId: Int): ZIO[ChppService, HattidError, Long] = {
    if(leagueUnitName == "I.1") {
      ZIO.succeed(CommonData.higherLeagueMap(leagueId).leagueUnitId): ZIO[Any, HattidError, Long]
    } else {
      val searchRequest = if (leagueId == 1) { //Sweden
        val (division, number) = LeagueNameParser.getLeagueUnitNumberByName(leagueUnitName)
        val actualDivision = Romans(Romans(division) - 1)
        val actualNumber = if (division == "II" || division == "III") {
          ('a' + number - 1).toChar.toString
        } else {
          "." + number.toString
        }
        SearchRequest(
          searchType = Some(SearchType.SERIES),
          searchLeagueId = Some(leagueId),
          searchString = Some(actualDivision + actualNumber)
        )
      } else {
        SearchRequest(
          searchType = Some(SearchType.SERIES),
          searchLeagueId = Some(leagueId),
          searchString = Some(leagueUnitName)
        )
      }

      for {
        chppService  <- ZIO.service[ChppService]
        searchResult <- chppService.search(searchRequest)
        leagueUnitId <- ZIO.fromOption(searchResult.searchResults.headOption.map(_.resultId))
          .mapError(_ => NotFoundError(
            entityType = NotFoundError.LEAGUE_UNIT,
            description = s"No $leagueUnitName in league $leagueId",
            entityId = s"$leagueUnitName"
          ))
      } yield leagueUnitId
    }
  }

  def getLeagueUnitData(leagueUnitId: Int): Action[AnyContent] = asyncZio {
    for {
      chppService       <- ZIO.service[ChppService]
      leagueInfoService <- ZIO.service[LeagueInfoServiceZIO]
      leagueDetails     <- chppService.leagueDetails(leagueUnitId)
      leagueState       <- leagueInfoService.leagueState(leagueDetails.leagueId)
    } yield RestLeagueUnitData(leagueDetails, leagueState, leagueUnitId)
  }

  private def stats[T : Writes](chRequest: ClickhouseStatisticsRequest[T],
                       leagueUnitId: Int,
                       restStatisticsParameters: RestStatisticsParameters): Action[AnyContent] = asyncZio {
    for {
      chppService   <- ZIO.service[ChppService]
      leagueDetails <- chppService.leagueDetails(leagueUnitId)
      entities      <- chRequest.execute(
        orderingKeyPath = OrderingKeyPath(leagueId = Some(leagueDetails.leagueId),
          divisionLevel = Some(leagueDetails.leagueLevel),
          leagueUnitId = Some(leagueUnitId)),
        parameters = restStatisticsParameters)
    } yield restTableData(entities, restStatisticsParameters.pageSize)
  }

  private def playersRequest[T](plRequest: ClickhousePlayerStatsRequest[T],
                                leagueUnitId: Int,
                                restStatisticsParameters: RestStatisticsParameters,
                                playersParameters: PlayersParameters)(implicit writes: Writes[T]) = asyncZio {
    for {
      chppService   <- ZIO.service[ChppService]
      leagueDetails <- chppService.leagueDetails(leagueUnitId)
      entities      <- plRequest.execute(
        OrderingKeyPath(leagueId = Some(leagueDetails.leagueId),
          divisionLevel = Some(leagueDetails.leagueLevel),
          leagueUnitId = Some(leagueUnitId)),
        restStatisticsParameters,
        playersParameters
      )
    } yield restTableData(entities, restStatisticsParameters.pageSize)
  }

  private def teamChart[T <: Chart : Writes](chartRequest: ClickhouseChartRequest[T], leagueUnitId: Int, season: Int): Action[AnyContent] = asyncZio {
    for {
      chppService   <- ZIO.service[ChppService]
      leagueDetails <- chppService.leagueDetails(leagueUnitId)
      entities <- chartRequest.execute(
        orderingKeyPath = OrderingKeyPath(
          leagueId = Some(leagueDetails.leagueId),
          divisionLevel = Some(leagueDetails.leagueLevel),
          leagueUnitId = Some(leagueUnitId)),
        season = season)
    } yield entities
  }

  def teamHatstatsChart(leagueUnitId: Int, season: Int): Action[AnyContent] =
    teamChart(TeamHatstatsChartRequest, leagueUnitId, season)

  def teamHatstats(leagueUnitId: Int, restStatisticsParameters: RestStatisticsParameters): Action[AnyContent] =
    stats(TeamHatstatsRequest, leagueUnitId, restStatisticsParameters)

  def playerGoalGames(leagueUnitId: Int, restStatisticsParameters: RestStatisticsParameters, playersParameters: PlayersParameters): Action[AnyContent] =
    playersRequest(PlayerGamesGoalsRequest, leagueUnitId, restStatisticsParameters, playersParameters)

  def playerCards(leagueUnitId: Int, restStatisticsParameters: RestStatisticsParameters,
                  playersParameters: PlayersParameters): Action[AnyContent] =
    playersRequest(PlayerCardsRequest, leagueUnitId, restStatisticsParameters, playersParameters)

  def playerTsiSalary(leagueUnitId: Int, restStatisticsParameters: RestStatisticsParameters, playersParameters: PlayersParameters): Action[AnyContent] =
    playersRequest(PlayerSalaryTSIRequest, leagueUnitId, restStatisticsParameters, playersParameters)

  def playerRatings(leagueUnitId: Int, restStatisticsParameters: RestStatisticsParameters, playersParameters: PlayersParameters): Action[AnyContent] =
    playersRequest(PlayerRatingsRequest, leagueUnitId, restStatisticsParameters, playersParameters)

  def playerInjuries(leagueUnitId: Int, restStatisticsParameters: RestStatisticsParameters): Action[AnyContent] =
    stats(PlayerInjuryRequest, leagueUnitId, restStatisticsParameters)

  def teamSalaryTsi(leagueUnitId: Int, restStatisticsParameters: RestStatisticsParameters, playedInLastMatch: Boolean, excludeZeroTsi: Boolean): Action[AnyContent] = asyncZio {
    for {
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
    } yield restTableData(entities, restStatisticsParameters.pageSize)
  }

  def teamSalaryTsiChart(leagueUnitId: Int, season: Int, playedInLastMatch: Boolean, excludeZeroTsi: Boolean): Action[AnyContent] = asyncZio {
    for {
      chppService   <- ZIO.service[ChppService]
      leagueDetails <- chppService.leagueDetails(leagueUnitId)
      entities      <- TeamSalaryTSIChartRequest.execute(
        orderingKeyPath = OrderingKeyPath(
          leagueId = Some(leagueDetails.leagueId),
          divisionLevel = Some(leagueDetails.leagueLevel),
          leagueUnitId = Some(leagueUnitId)
        ),
        season = season,
        playedInLastMatch = playedInLastMatch,
        excludeZeroTsi = excludeZeroTsi)
    } yield entities
  }

  def teamCards(leagueUnitId: Int, restStatisticsParameters: RestStatisticsParameters): Action[AnyContent] = asyncZio {
    for {
      chppService   <- ZIO.service[ChppService]
      leagueDetails <- chppService.leagueDetails(leagueUnitId)
      entities      <- TeamCardsRequest.execute(
        orderingKeyPath = OrderingKeyPath(leagueId = Some(leagueDetails.leagueId),
          divisionLevel = Some(leagueDetails.leagueLevel),
          leagueUnitId = Some(leagueUnitId)),
        parameters = restStatisticsParameters)
    } yield restTableData(entities, restStatisticsParameters.pageSize)
  }

  def teamCardsChart(leagueUnitId: Int, season: Int): Action[AnyContent] =
    teamChart(TeamCardsChartRequest, leagueUnitId, season)

  def teamRatings(leagueUnitId: Int, restStatisticsParameters: RestStatisticsParameters): Action[AnyContent] =
    stats(TeamRatingsRequest, leagueUnitId, restStatisticsParameters)
    
  def teamRatingsChart(leagueUnitId: Int, season: Int): Action[AnyContent] =
    teamChart(TeamRatingsChartRequest, leagueUnitId, season)

  def teamAgeInjuries(leagueUnitId: Int, restStatisticsParameters: RestStatisticsParameters): Action[AnyContent] =
    stats(TeamAgeInjuryRequest, leagueUnitId, restStatisticsParameters)

  def teamAgeInjuriesChart(leagueUnitId: Int, season: Int): Action[AnyContent] =
    teamChart(TeamAgeInjuryChartRequest, leagueUnitId, season)
  
  def teamPowerRatings(leagueUnitId: Int, restStatisticsParameters: RestStatisticsParameters): Action[AnyContent] =
    stats(TeamPowerRatingsRequest, leagueUnitId, restStatisticsParameters)
    
  def teamPowerRatingsChart(leagueUnitId: Int, season: Int): Action[AnyContent] =
    teamChart(TeamPowerRatingsChartRequest, leagueUnitId, season)

  def teamFanclubFlags(leagueUnitId: Int, restStatisticsParameters: RestStatisticsParameters): Action[AnyContent] =
    stats(TeamFanclubFlagsRequest, leagueUnitId, restStatisticsParameters)
    
  def teamFanclubFlagsChart(leagueUnitId: Int, season: Int): Action[AnyContent] =
    teamChart(TeamFanclubFlagsChartRequest, leagueUnitId, season)

  def teamStreakTrophies(leagueUnitId: Int, restStatisticsParameters: RestStatisticsParameters): Action[AnyContent] =
    stats(TeamStreakTrophiesRequest, leagueUnitId, restStatisticsParameters)
    
  def teamStreakTrophiesChart(leagueUnitId: Int, season: Int): Action[AnyContent] =
    teamChart(TeamStreakTrophiesChartRequest, leagueUnitId, season)

  def topMatches(leagueUnitId: Int, restStatisticsParameters: RestStatisticsParameters): Action[AnyContent] =
    stats(MatchTopHatstatsRequest, leagueUnitId, restStatisticsParameters)

  def surprisingMatches(leagueUnitId: Int, restStatisticsParameters: RestStatisticsParameters): Action[AnyContent] =
    stats(MatchSurprisingRequest, leagueUnitId, restStatisticsParameters)

  def matchSpectators(leagueUnitId: Int, restStatisticsParameters: RestStatisticsParameters): Action[AnyContent] =
    stats(MatchSpectatorsRequest, leagueUnitId, restStatisticsParameters)

  def oldestTeams(leagueUnitId: Int, restStatisticsParameters: RestStatisticsParameters): Action[AnyContent] =
    stats(OldestTeamsRequest, leagueUnitId, restStatisticsParameters)


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

  def teamPositions(leagueUnitId: Int, restStatisticsParameters: RestStatisticsParameters): Action[AnyContent] = asyncZio {
    val season = restStatisticsParameters.season
    teamPositionsInternal(leagueUnitId, season, _.teamsLastRoundWithPositionsDiff, lastRoundPositionsDiffFromLeagueDetails)
      .map { positionsWithDiff => RestTableData(positionsWithDiff, isLastPage = true) }
  }

  def teamPositionsHistory(leagueUnitId: Int, season: Int): Action[AnyContent] = asyncZio {
    teamPositionsInternal(leagueUnitId, season, _.positionsHistory, _ => Seq())
  }

  private def teamPositionsInternal[T](leagueUnitId: Int,
                                       season: Int,
                                       resultFunc: LeagueUnitTeamStatHistoryInfo => Seq[T],
                                       fallbackResultFunc: LeagueDetails => Seq[T]): ZIO[ChppService & LeagueInfoServiceZIO, HattidError, Seq[T]] = {
    for {
      chppService   <- ZIO.service[ChppService]
      leagueDetails <- chppService.leagueDetails(leagueUnitId)
      history       <- historyInfo(leagueDetails, leagueUnitId, season).map(resultFunc)
        .onError(_ => ZIO.succeed(fallbackResultFunc(leagueDetails)))
    } yield history
  }

  private def historyInfo[T](leagueDetails: LeagueDetails, leagueUnitId: Int, season: Int): ZIO[ChppService & LeagueInfoServiceZIO, HattidError, LeagueUnitTeamStatHistoryInfo] = {
    val leagueId = leagueDetails.leagueId
    for {
      chppService       <- ZIO.service[ChppService]
      leagueInfoService <- ZIO.service[LeagueInfoServiceZIO]
      offsettedSeason   <- leagueInfoService.getRelativeSeasonFromAbsolute(season, leagueId)
      leagueFixture     <- chppService.leagueFixtures(leagueUnitId, offsettedSeason)
      currentSeason     <- leagueInfoService.currentSeason(leagueId)
      round             <- if (currentSeason == season) leagueInfoService.lastRound(leagueId, season) else ZIO.succeed(14)
      leagueUnitTeamHistoryInfo <- ZIO.fromEither(leagueUnitCalculatorService.calculateSafe(leagueFixture, Some(round),
          /* doesn't matter what parameters are there */"points", Desc))
        .mapError(u => HattidInternalError("Unable to calculate league unit team history info"))
    } yield leagueUnitTeamHistoryInfo
  }

  def promotions(leagueUnitId: Int): Action[AnyContent] = asyncZio {
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
    } yield PromotionWithType.convert(promotions)
  }

  def dreamTeam(season: Int, leagueUnitId: Int, sortBy: String, statsType: StatsType): Action[AnyContent] = asyncZio {
    for {
      chppService   <- ZIO.service[ChppService]
      leagueDetails <- chppService.leagueDetails(leagueUnitId)
      players       <- DreamTeamRequest.execute(
        OrderingKeyPath(season = Some(season),
          leagueId = Some(leagueDetails.leagueId),
          divisionLevel = Some(leagueDetails.leagueLevel),
          leagueUnitId = Some(leagueUnitId)),
        statsType,
        sortBy
      )
    } yield players
  }
}
