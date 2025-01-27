package controllers

import chpp.teamdetails.models.Team
import databases.dao.RestClickhouseDAO
import databases.requests.matchdetails.{MatchSpectatorsRequest, MatchSurprisingRequest, MatchTopHatstatsRequest, TeamMatchesRequest}
import databases.requests.model.promotions.PromotionWithType
import databases.requests.playerstats.player.stats._
import databases.requests.promotions.PromotionsRequest
import databases.requests.teamrankings.TeamRankingsRequest
import databases.requests.{ClickhouseStatisticsRequest, OrderingKeyPath}
import models.clickhouse.NearestMatch
import models.web.rest.RestTeamData
import models.web.teams.RestTeamRankings
import models.web.{PlayersParameters, RestStatisticsParameters}
import play.api.libs.json.{Json, OWrites, Writes}
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import service.leagueinfo.LeagueInfoService
import service.{ChppService, HattrickPeriod, TeamsService}
import utils.{CurrencyUtils, Romans}

import java.util.Date
import javax.inject.Inject
//TODO
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class NearestMatches(playedMatches: Seq[NearestMatch], upcomingMatches: Seq[NearestMatch])

object NearestMatches {
  implicit val writes: OWrites[NearestMatches] = Json.writes[NearestMatches]
}

class RestTeamController @Inject() (val controllerComponents: ControllerComponents,
                                    val leagueInfoService: LeagueInfoService,
                                    val teamsService: TeamsService,
                                    val chppService: ChppService,
                                    implicit val restClickhouseDAO: RestClickhouseDAO) extends RestController {

  private def getRestTeamData(team: Team) = {
    val league = leagueInfoService.leagueInfo(team.league.leagueId).league

    RestTeamData(
      leagueId = team.league.leagueId,
      leagueName = league.englishName,
      divisionLevel = team.leagueLevelUnit.leagueLevel,
      divisionLevelName = Romans(team.leagueLevelUnit.leagueLevel),
      leagueUnitId = team.leagueLevelUnit.leagueLevelUnitId,
      leagueUnitName = team.leagueLevelUnit.leagueLevelUnitName,
      teamId = team.teamId,
      teamName = team.teamName,
      foundedDate = team.foundedDate.getTime,
      seasonOffset = league.seasonOffset,
      seasonRoundInfo = leagueInfoService.leagueInfo.seasonRoundInfo(team.league.leagueId),
      currency = CurrencyUtils.currencyName(league.country),
      currencyRate = CurrencyUtils.currencyRate(league.country),
      loadingInfo = leagueInfoService.leagueInfo(team.league.leagueId).loadingInfo,
      countries = leagueInfoService.idToStringCountryMap
    )
  }

  def getTeamData(teamId: Long): Action[AnyContent] = Action.async {
    chppService.getTeamById(teamId).map {
      case Left(notFoundError) => NotFound(Json.toJson(notFoundError))
      case Right(team) => Ok(Json.toJson(getRestTeamData(team)))
    }
  }


  private def orderingKeyPathFromTeam(team: Team, divisionLevel: Int, leagueUnitId: Long): OrderingKeyPath =
    OrderingKeyPath(
      leagueId = Some(team.league.leagueId),
      divisionLevel = Some(divisionLevel),
      leagueUnitId = Some(leagueUnitId),
      teamId = Some(team.teamId)
    )

  private def stats[T](chRequest: ClickhouseStatisticsRequest[T],
                       teamId: Long,
                       restStatisticsParameters: RestStatisticsParameters)
              (implicit writes: Writes[T]): Action[AnyContent] = Action.async{ implicit request =>
    chppService.getTeamByIdUnsafe(teamId).flatMap(teamOpt => teamOpt.map(team => {
      for {
        (divisionLevel, leagueUnitId) <- chppService.getDivisionLevelAndLeagueUnit(team, restStatisticsParameters.season)
        statList <- chRequest.execute(orderingKeyPathFromTeam(team, divisionLevel, leagueUnitId), restStatisticsParameters)
      } yield restTableDataJson(statList, restStatisticsParameters.pageSize)
    }).getOrElse(Future(NotFound(s"TeamId: $teamId")))
    )
  }

  private def playersRequest[T](plRequest: ClickhousePlayerStatsRequest[T],
                                teamId: Long,
                                restStatisticsParameters: RestStatisticsParameters,
                                playersParameters: PlayersParameters)(implicit writes: Writes[T]) = Action.async{ implicit request =>
    chppService.getTeamByIdUnsafe(teamId).flatMap(teamOpt => teamOpt.map(team => {
      for {
        (divisionLevel, leagueUnitId) <- chppService.getDivisionLevelAndLeagueUnit(team, restStatisticsParameters.season)
        statList <- plRequest.execute(orderingKeyPathFromTeam(team, divisionLevel, leagueUnitId), restStatisticsParameters, playersParameters)
      } yield restTableDataJson(statList, restStatisticsParameters.pageSize)
    }).getOrElse(Future(NotFound(s"TeamId: $teamId")))
    )
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


  def teamRankings(teamId: Long, season: Option[Int]): Action[AnyContent] = Action.async { implicit request =>
    chppService.getTeamByIdUnsafe(teamId).flatMap(teamOpt => teamOpt.map(team => {
      val leagueId = team.league.leagueId

      TeamRankingsRequest.execute(OrderingKeyPath(
        season = season,
        leagueId = Some(leagueId),
        teamId = Some(teamId),
      )).map(teamRankings => {
          val leagueInfo = leagueInfoService.leagueInfo(leagueId)
         
          val selectedSeason = season.getOrElse(leagueInfo.currentSeason())

          val leagueTeamsRoundToCounts = leagueInfo.seasonInfo(selectedSeason).roundInfo
            .map{case (round, roundInfo) =>
              val leagueTeamsNumber = roundInfo.divisionLevelInfo.values.map(_.count).sum
              (round, leagueTeamsNumber)
            }.toSeq
          val divisionLevel = teamRankings.map(_.divisionLevel).headOption.getOrElse(team.leagueLevelUnit.leagueLevel)
          val divisionLevelTeamsCounts = leagueInfo.seasonInfo(selectedSeason).roundInfo
            .map{case (round, roundInfo) =>
              (round, roundInfo.divisionLevelInfo.get(divisionLevel).map(_.count).getOrElse(0))
            }.toSeq

          val currencyRate = CurrencyUtils.currencyRate(leagueInfo.league.country)
          val currencyName = CurrencyUtils.currencyName(leagueInfo.league.country)

          RestTeamRankings(teamRankings = teamRankings,
            leagueTeamsCounts = leagueTeamsRoundToCounts,
            divisionLevelTeamsCounts = divisionLevelTeamsCounts,
            currencyRate = currencyRate,
            currencyName = currencyName)

        }).map(tr => Ok(Json.toJson(tr)))
    }).getOrElse(Future(NotFound(s"TeamId: $teamId")))
    )
  }

  def nearestMatches(teamId: Long): Action[AnyContent] = Action.async { implicit request =>
    chppService.nearestMatches(teamId)
      .map(matches => Ok(Json.toJson(matches)))
  }

  def promotions(teamId: Long): Action[AnyContent] = Action.async{ implicit request =>
    chppService.getTeamByIdUnsafe(teamId).flatMap(teamOpt => teamOpt.map(team => {
      val season = leagueInfoService.leagueInfo.currentSeason(team.league.leagueId)
      for {
        (divisionLevel, leagueUnit) <- chppService.getDivisionLevelAndLeagueUnit(team, season)
        promotions <- PromotionsRequest.execute(orderingKeyPathFromTeam(team, divisionLevel, leagueUnit), season)
      } yield Ok(Json.toJson(PromotionWithType.convert(promotions)))
    }).getOrElse(Future(NotFound(s"TeamId: $teamId")))
    )
  }

  def teamMatches(teamId: Long, season: Int): Action[AnyContent] = Action.async(implicit request =>
    chppService.getTeamByIdUnsafe(teamId).flatMap(teamOpt => teamOpt.map(team => {
      for {
        (divisionLevel, leagueUnitId) <- chppService.getDivisionLevelAndLeagueUnit(team, season)
        matches <- TeamMatchesRequest.execute(season, orderingKeyPathFromTeam(team, divisionLevel, leagueUnitId))
      } yield Ok(Json.toJson(matches))
    }).getOrElse(Future(NotFound(s"TeamId: $teamId"))))
  )

  def teamsFoundedSameDate(period: HattrickPeriod, leagueId: Int, foundedDate: Long): Action[AnyContent] = Action.async { implicit request =>
    teamsService.teamsCreatedSamePeriod(period, new Date(foundedDate), leagueId)
      .map(teams => Ok(Json.toJson(teams)))
  }

  def compareTeams(team1: Long, team2: Long): Action[AnyContent] = Action.async{ implicit request =>
    teamsService.compareTwoTeams(team1, team2)
      .map(rankings => Ok(Json.toJson(rankings)))
  }
}
