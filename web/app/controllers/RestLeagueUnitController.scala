package controllers

import chpp.leaguedetails.LeagueDetailsRequest
import chpp.leaguedetails.models.LeagueDetails
import chpp.leaguefixtures.LeagueFixturesRequest
import chpp.leaguefixtures.models.LeagueFixtures
import chpp.search.SearchRequest
import chpp.search.models.{Search, SearchType}
import databases.dao.RestClickhouseDAO
import databases.requests.matchdetails.{MatchSpectatorsRequest, MatchSurprisingRequest, MatchTopHatstatsRequest, TeamHatstatsRequest}
import databases.requests.model.promotions.PromotionWithType
import databases.requests.playerstats.dreamteam.DreamTeamRequest
import databases.requests.playerstats.player.stats.*
import databases.requests.playerstats.team.{TeamAgeInjuryRequest, TeamCardsRequest, TeamRatingsRequest, TeamSalaryTSIRequest}
import databases.requests.promotions.PromotionsRequest
import databases.requests.teamdetails.{OldestTeamsRequest, TeamFanclubFlagsRequest, TeamPowerRatingsRequest, TeamStreakTrophiesRequest}
import databases.requests.{ClickhouseStatisticsRequest, OrderingKeyPath}
import hattid.CommonData
import models.web.*
import models.web.leagueUnit.RestLeagueUnitData
import play.api.libs.json.{JsValue, Json, OWrites, Writes}
import play.api.mvc.{Action, AnyContent, ControllerComponents, Result}
import service.leagueinfo.LeagueInfoService
import service.leagueunit.{LeagueUnitCalculatorService, LeagueUnitTeamStat, LeagueUnitTeamStatHistoryInfo, LeagueUnitTeamStatsWithPositionDiff}
import utils.{LeagueNameParser, Romans}
import webclients.ChppClient
import service.ChppService
import zio.ZLayer

import javax.inject.Inject
//TODO execution context!
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class RestLeagueUnitController @Inject() (val chppClient: ChppClient,
                                          val chppService: ChppService,
                                          val controllerComponents: ControllerComponents,
                                          val leagueInfoService: LeagueInfoService,
                                          val restClickhouseDAO: RestClickhouseDAO,
                                          val leagueUnitCalculatorService: LeagueUnitCalculatorService) extends RestController {
  case class LongWrapper(id: Long)
  implicit val writes: OWrites[LongWrapper] = Json.writes[LongWrapper]

  def leagueUnitIdByName(leagueUnitName: String, leagueId: Int): Action[AnyContent] = Action.async {
    findLeagueUnitIdByName(leagueUnitName, leagueId)
      .map(id => Ok(Json.toJson(LongWrapper(id))))
  }

  private def findLeagueUnitIdByName(leagueUnitName: String, leagueId: Int): Future[Long] = {
    if(leagueUnitName == "I.1") {
      Future(CommonData.higherLeagueMap(leagueId).leagueUnitId)
    } else {
      if(leagueId == 1) { //Sweden
        val (division, number) = LeagueNameParser.getLeagueUnitNumberByName(leagueUnitName)
        val actualDivision = Romans(Romans(division) - 1)
        val actualNumber = if (division == "II" || division == "III") {
          ('a' + number - 1).toChar.toString
        } else {
          "." + number.toString
        }
        chppClient.executeUnsafe[Search, SearchRequest](SearchRequest(
          searchType = Some(SearchType.SERIES),
          searchLeagueId = Some(leagueId),
          searchString = Some(actualDivision + actualNumber)
        )).map(_.searchResults.head.resultId)
      } else {
        chppClient.executeUnsafe[Search, SearchRequest](SearchRequest(
          searchType = Some(SearchType.SERIES),
          searchLeagueId = Some(leagueId),
          searchString = Some(leagueUnitName)
        )).map(_.searchResults.head.resultId)
      }
    }
  }

  def getLeagueUnitData(leagueUnitId: Int): Action[AnyContent] = asyncZio {
    chppService.leagueUnitDataById(leagueUnitId)
  }

  private def stats[T : Writes](chRequest: ClickhouseStatisticsRequest[T],
                       leagueUnitId: Int,
                       restStatisticsParameters: RestStatisticsParameters): Action[AnyContent] = asyncZio {
    (for {
      leagueUnitData <- chppService.leagueUnitDataById(leagueUnitId)
      entities <- chRequest.execute(OrderingKeyPath(leagueId = Some(leagueUnitData.leagueId),
          divisionLevel = Some(leagueUnitData.divisionLevel),
          leagueUnitId = Some(leagueUnitId)),
        restStatisticsParameters)
    } yield restTableData(entities, restStatisticsParameters.pageSize))
      .provide(ZLayer.succeed(restClickhouseDAO))
  }

  private def playersRequest[T](plRequest: ClickhousePlayerStatsRequest[T],
                                leagueUnitId: Int,
                                restStatisticsParameters: RestStatisticsParameters,
                                playersParameters: PlayersParameters)(implicit writes: Writes[T]) = asyncZio {
    (for {
      leagueUnitData <- chppService.leagueUnitDataById(leagueUnitId)
      entities <- plRequest.execute(
        OrderingKeyPath(leagueId = Some(leagueUnitData.leagueId),
          divisionLevel = Some(leagueUnitData.divisionLevel),
          leagueUnitId = Some(leagueUnitId)),
        restStatisticsParameters,
        playersParameters
      )
    } yield restTableData(entities, restStatisticsParameters.pageSize))
      .provide(ZLayer.succeed(restClickhouseDAO))
  }

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
    (for {
      leagueUnitData <- chppService.leagueUnitDataById(leagueUnitId)
      entities <- TeamSalaryTSIRequest.execute(
        OrderingKeyPath(leagueId = Some(leagueUnitData.leagueId),
          divisionLevel = Some(leagueUnitData.divisionLevel),
          leagueUnitId = Some(leagueUnitId)),
        restStatisticsParameters,
        playedInLastMatch = playedInLastMatch,
        excludeZeroTsi = excludeZeroTsi
      )
    } yield restTableData(entities, restStatisticsParameters.pageSize))
      .provide(ZLayer.succeed(restClickhouseDAO))
  }

  def teamCards(leagueUnitId: Int, restStatisticsParameters: RestStatisticsParameters): Action[AnyContent] = asyncZio {
    (for {
      leagueUnitData <- chppService.leagueUnitDataById(leagueUnitId)
      entities <- TeamCardsRequest.execute(
        OrderingKeyPath(leagueId = Some(leagueUnitData.leagueId),
          divisionLevel = Some(leagueUnitData.divisionLevel),
          leagueUnitId = Some(leagueUnitId)),
        restStatisticsParameters)
    } yield restTableData(entities, restStatisticsParameters.pageSize))
      .provide(ZLayer.succeed(restClickhouseDAO))
  }

  def teamRatings(leagueUnitId: Int, restStatisticsParameters: RestStatisticsParameters): Action[AnyContent] =
    stats(TeamRatingsRequest, leagueUnitId, restStatisticsParameters)

  def teamAgeInjuries(leagueUnitId: Int, restStatisticsParameters: RestStatisticsParameters): Action[AnyContent] =
    stats(TeamAgeInjuryRequest, leagueUnitId, restStatisticsParameters)

  def teamPowerRatings(leagueUnitId: Int, restStatisticsParameters: RestStatisticsParameters): Action[AnyContent] =
    stats(TeamPowerRatingsRequest, leagueUnitId, restStatisticsParameters)

  def teamFanclubFlags(leagueUnitId: Int, restStatisticsParameters: RestStatisticsParameters): Action[AnyContent] =
    stats(TeamFanclubFlagsRequest, leagueUnitId, restStatisticsParameters)

  def teamStreakTrophies(leagueUnitId: Int, restStatisticsParameters: RestStatisticsParameters): Action[AnyContent] =
    stats(TeamStreakTrophiesRequest, leagueUnitId, restStatisticsParameters)

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

  def teamPositions(leagueUnitId: Int, restStatisticsParameters: RestStatisticsParameters): Action[AnyContent] = Action.async {
    val season = restStatisticsParameters.season
    teamPositionsInternal(leagueUnitId, season, _.teamsLastRoundWithPositionsDiff, lastRoundPositionsDiffFromLeagueDetails)
      .map { positionsWithDiff => Ok(Json.toJson(RestTableData(positionsWithDiff, isLastPage = true))) }
  }

  def teamPositionsHistory(leagueUnitId: Int, season: Int): Action[AnyContent] = Action.async {
    teamPositionsInternal(leagueUnitId, season, _.positionsHistory, _ => Seq())
      .map { positionsHistory => Ok(Json.toJson(positionsHistory)) }
  }

  private def teamPositionsInternal[T](leagueUnitId: Int,
                               season: Int,
                               resultFunc: LeagueUnitTeamStatHistoryInfo => Seq[T],
                               fallbackResultFunc: LeagueDetails => Seq[T]): Future[Seq[T]] = {
    chppClient.execute[LeagueDetails, LeagueDetailsRequest](LeagueDetailsRequest(leagueUnitId = Some(leagueUnitId)))
      .flatMap {
        case Right(leagueDetails) =>
          val offsettedSeason = leagueInfoService.getRelativeSeasonFromAbsolute(season, leagueDetails.leagueId)
          chppClient.execute[LeagueFixtures, LeagueFixturesRequest](
              LeagueFixturesRequest(leagueLevelUnitId = Some(leagueUnitId), season = Some(offsettedSeason)))
            .map {
              case Right(leagueFixture) =>
                val round = if(leagueInfoService.leagueInfo(leagueDetails.leagueId).currentSeason() == season) leagueInfoService.leagueInfo(leagueDetails.leagueId).currentRound() else 14
                leagueUnitCalculatorService.calculateSafe(leagueFixture, Some(round),
                  /* doesn't matter what parameters are there */"points", Desc) match {
                  case Right(leagueUnitTeamHistoryInfo) => resultFunc(leagueUnitTeamHistoryInfo)
                  case Left(_) => fallbackResultFunc(leagueDetails)
                }

              case Left(_) => fallbackResultFunc(leagueDetails)
            }
        case Left(_) => Future(Seq())
      }
  }

  def promotions(leagueUnitId: Int): Action[AnyContent] = asyncZio {
    (for {
      leagueUnitData <- chppService.leagueUnitDataById(leagueUnitId)
      promotions <- PromotionsRequest.execute(OrderingKeyPath(
          leagueId = Some(leagueUnitData.leagueId),
          divisionLevel = Some(leagueUnitData.divisionLevel),
          leagueUnitId = Some(leagueUnitData.leagueUnitId)), 
        leagueInfoService.leagueInfo.currentSeason(leagueUnitData.leagueId))
    } yield PromotionWithType.convert(promotions))
      .provide(ZLayer.succeed(restClickhouseDAO))
  }

  def dreamTeam(season: Int, leagueUnitId: Int, sortBy: String, statsType: StatsType): Action[AnyContent] = asyncZio {
    (for {
      leagueUnitData <- chppService.leagueUnitDataById(leagueUnitId)
      players <- DreamTeamRequest.execute(
        OrderingKeyPath(season = Some(season),
          leagueId = Some(leagueUnitData.leagueId),
          divisionLevel = Some(leagueUnitData.divisionLevel),
          leagueUnitId = Some(leagueUnitData.leagueUnitId)),
        statsType,
        sortBy
      )
    } yield players)
      .provide(ZLayer.succeed(restClickhouseDAO))
  }
}
