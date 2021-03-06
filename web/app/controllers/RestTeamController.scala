package controllers

import chpp.commonmodels.MatchType
import chpp.matches.MatchesRequest
import chpp.matches.models.Matches
import chpp.teamdetails.TeamDetailsRequest
import chpp.teamdetails.models.{Team, TeamDetails}
import chpp.worlddetails.WorldDetailsRequest
import chpp.worlddetails.models.WorldDetails
import databases.dao.{ClickhouseDAO, RestClickhouseDAO}
import databases.requests.matchdetails.{MatchSpectatorsRequest, MatchSurprisingRequest, MatchTopHatstatsRequest, TeamMatchesRequest}
import databases.requests.model.promotions.PromotionWithType
import databases.requests.playerstats.player._
import databases.requests.promotions.PromotionsRequest
import databases.requests.teamrankings.TeamRankingsRequest
import databases.requests.{ClickhouseStatisticsRequest, OrderingKeyPath}
import hattrick.ChppClient
import models.clickhouse.{NearestMatch, TeamRankings}
import models.web.rest.CountryLevelData
import models.web.rest.LevelData.Rounds
import models.web.{PlayersParameters, RestStatisticsParameters}
import play.api.libs.json.{Json, OWrites, Writes}
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import service.{HattrickPeriod, TeamsService}
import service.leagueinfo.{LeagueInfoService, LoadingInfo}
import utils.{CurrencyUtils, Romans}

import java.util.Date
import javax.inject.Inject
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class RestTeamData(leagueId: Int,
                        leagueName: String,
                        divisionLevel: Int,
                        divisionLevelName: String,
                        leagueUnitId: Long,
                        leagueUnitName: String,
                        teamId: Long,
                        teamName: String,
                        foundedDate: Long,
                        seasonOffset: Int,
                        seasonRoundInfo: Seq[(Int, Rounds)],
                        currency: String,
                        currencyRate: Double,
                        loadingInfo: LoadingInfo,
                        countries: Seq[(Int, String)]) extends CountryLevelData

object RestTeamData {
  implicit val writes: OWrites[RestTeamData] = Json.writes[RestTeamData]
}

object RestTeamRankings {
  implicit val writes: OWrites[RestTeamRankings] = Json.writes[RestTeamRankings]
}

case class RestTeamRankings(teamRankings: Seq[TeamRankings],
                            leagueTeamsCount: Int,
                            divisionLevelTeamsCount: Int,
                            currencyRate: Double,
                            currencyName: String)

case class NearestMatches(playedMatches: Seq[NearestMatch], upcomingMatches: Seq[NearestMatch])

object NearestMatches {
  implicit val writes: OWrites[NearestMatches] = Json.writes[NearestMatches]
}

class RestTeamController @Inject() (val controllerComponents: ControllerComponents,
                                    val chppClient: ChppClient,
                                    val leagueInfoService: LeagueInfoService,
                                    val teamsService: TeamsService,
                                    implicit val clickhouseDAO: ClickhouseDAO,
                                    implicit val restClickhouseDAO: RestClickhouseDAO) extends RestController {

  private def getTeamById(teamId: Long): Future[Either[Team, Team]] = {
    chppClient.execute[TeamDetails, TeamDetailsRequest](TeamDetailsRequest(teamId = Some(teamId)))
      .map(teamDetails => {
        val team = teamDetails.teams.filter(_.teamId == teamId).head
        if (teamDetails.user.userId == 0L) Left(team)
        else Right(team)
      })
  }

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
    getTeamById(teamId)
      .map(teamEither => teamEither.map(team => {
        getRestTeamData(team)
      })).map( {
      case Right(data) => Ok(Json.toJson(data))
      case Left(data) => Ok(Json.toJson(getRestTeamData(data)))
    })
  }

  private def getDivisionLevelAndLeagueUnit(team: Team, season: Int): Future[(Int, Long)] = {
    chppClient.execute[WorldDetails, WorldDetailsRequest](WorldDetailsRequest(leagueId = Some(team.league.leagueId)))
      .map(_.leagueList.head)
      .map(league => {
        val htRound = league.matchRound

        if(htRound == 16
          || leagueInfoService.leagueInfo.currentSeason(team.league.leagueId) > season
          || league.season - league.seasonOffset > season) {
          val infoOpt = clickhouseDAO.historyTeamLeagueUnitInfo(season, team.league.leagueId, team.teamId)
          infoOpt.map(info => (info.divisionLevel, info.leagueUnitId))
            .getOrElse((team.leagueLevelUnit.leagueLevel, team.leagueLevelUnit.leagueLevelUnitId))
        } else {
          (team.leagueLevelUnit.leagueLevel, team.leagueLevelUnit.leagueLevelUnitId.toLong)
        }
      })
  }



  private def stats[T](chRequest: ClickhouseStatisticsRequest[T],
               teamId: Long,
               restStatisticsParameters: RestStatisticsParameters)
              (implicit writes: Writes[T]) = Action.async{ implicit request =>
    getTeamById(teamId).flatMap(teamEither => teamEither.map(team => {

      getDivisionLevelAndLeagueUnit(team, restStatisticsParameters.season)
        .flatMap { case (divisionLevel: Int, leagueUnitId: Long) =>
          chRequest.execute(
            OrderingKeyPath(
              leagueId = Some(team.league.leagueId),
              divisionLevel = Some(divisionLevel),
              leagueUnitId = Some(leagueUnitId),
              teamId = Some(teamId)
            ), restStatisticsParameters)
        }
    }) match {
      case Right(statList) => statList.map(entities => restTableDataJson(entities, restStatisticsParameters.pageSize))
      case Left(_) => Future(NoContent)
    })
  }

  private def playersRequest[T](plRequest: ClickhousePlayerRequest[T],
                                teamId: Long,
                                restStatisticsParameters: RestStatisticsParameters,
                                playersParameters: PlayersParameters)(implicit writes: Writes[T]) =
    Action.async{ implicit request =>
      getTeamById(teamId).flatMap(teamEither => teamEither.map(team => {

        getDivisionLevelAndLeagueUnit(team, restStatisticsParameters.season)
          .flatMap{  case (divisionLevel: Int, leagueUnitId: Long) =>
            plRequest.execute(
              OrderingKeyPath(
                leagueId = Some(team.league.leagueId),
                divisionLevel = Some(divisionLevel),
                leagueUnitId = Some(leagueUnitId),
                teamId = Some(teamId)
              ), restStatisticsParameters, playersParameters)
          }
      }) match {
        case Right(statList) => statList.map(entities => restTableDataJson(entities, restStatisticsParameters.pageSize))
        case Left(_) => Future(NoContent)
      })
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


  def teamRankings(teamId: Long): Action[AnyContent] = Action.async { implicit request =>
    getTeamById(teamId).flatMap(teamEither => teamEither.map(team => {
      val leagueId = team.league.leagueId
      val season = leagueInfoService.leagueInfo.currentSeason(leagueId)

      TeamRankingsRequest.execute(OrderingKeyPath(
        season = Some(season),
        leagueId = Some(leagueId),
        teamId = Some(teamId),
      )).map(teamRankings => {
          val round = leagueInfoService.leagueInfo.currentRound(leagueId)
          val leagueInfo = leagueInfoService.leagueInfo(leagueId)
          val leagueTeamsCount = leagueInfo.seasonInfo(season).roundInfo(round).divisionLevelInfo.values.map(_.count).sum
          val divisionLevel = teamRankings.map(_.divisionLevel).headOption.getOrElse(team.leagueLevelUnit.leagueLevel.toInt)
          val divisionLevelTeamsCount = leagueInfo.seasonInfo(season).roundInfo(round).divisionLevelInfo(divisionLevel).count
          val currencyRate = CurrencyUtils.currencyRate(leagueInfo.league.country)
          val currencyName = CurrencyUtils.currencyName(leagueInfo.league.country)

          RestTeamRankings(teamRankings = teamRankings,
            leagueTeamsCount = leagueTeamsCount,
            divisionLevelTeamsCount = divisionLevelTeamsCount,
            currencyRate = currencyRate,
            currencyName = currencyName)

        })
    }) match {
      case Right(rankings) => rankings.map(r => Ok(Json.toJson(r)))
      case Left(_) => Future(NoContent)
    })
  }

  def nearestMatches(teamId: Long): Action[AnyContent] = Action.async { implicit request =>
    chppClient.execute[Matches, MatchesRequest](MatchesRequest(teamId = Some(teamId)))
    .map(response => {
      val matches = response.team.matchList
        .filter(_.matchType == MatchType.LEAGUE_MATCH)
        .map(matc => NearestMatch(matc.matchDate, matc.status,
          matc.homeTeam.homeTeamId, matc.homeTeam.homeTeamName,
          matc.homeGoals, matc.awayGoals,
          matc.awayTeam.awayTeamName, matc.awayTeam.awayTeamId,
          matc.matchId))

      val playedMatches = matches
        .filter(_.status == "FINISHED")
        .sortBy(_.matchDate)
        .takeRight(3)

      val upcomingMatches = matches.filter(_.status == "UPCOMING")
        .sortBy(_.matchDate)
        .take(3)
      Ok(Json.toJson(NearestMatches(playedMatches.toSeq, upcomingMatches.toSeq)))
    })
  }

  def promotions(teamId: Long): Action[AnyContent] = Action.async{ implicit request =>
    getTeamById(teamId).flatMap(teamEither => teamEither.map(team => {
      val season = leagueInfoService.leagueInfo.currentSeason(team.league.leagueId)

      getDivisionLevelAndLeagueUnit(team, season)
        .flatMap { case (divisionLevel: Int, leagueUnitId: Long) =>
          PromotionsRequest.execute(
            OrderingKeyPath(leagueId = Some(team.league.leagueId),
              divisionLevel = Some(divisionLevel),
              leagueUnitId = Some(leagueUnitId),
              teamId = Some(teamId)), season
          )
        }
    }) match {
      case Right(promotions) => promotions.map(PromotionWithType.convert).map(result => Ok(Json.toJson(result)))
      case Left(_) => Future(NoContent)
    })
  }

  def teamMatches(teamId: Long, season: Int): Action[AnyContent] = Action.async(implicit request =>
    getTeamById(teamId).flatMap(teamEither => teamEither.map(team => {
      getDivisionLevelAndLeagueUnit(team, season)
        .flatMap { case (divisionLevel: Int, leagueUnitId: Long) =>

          TeamMatchesRequest.execute(season, OrderingKeyPath(leagueId = Some(team.league.leagueId),
            divisionLevel = Some(divisionLevel),
            leagueUnitId = Some(leagueUnitId),
            teamId = Some(team.teamId)))
        }
    }) match {
      case Right(matches) => matches.map(result => Ok(Json.toJson(result)))
      case Left(_) => Future(NoContent)
    })
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
