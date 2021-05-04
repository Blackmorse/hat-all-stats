package controllers

import com.blackmorse.hattrick.api.teamdetails.model.Team
import com.blackmorse.hattrick.model.enums.MatchType
import databases.dao.{ClickhouseDAO, RestClickhouseDAO}
import databases.requests.matchdetails.{MatchSpectatorsRequest, MatchSurprisingRequest, MatchTopHatstatsRequest, TeamMatchesRequest}
import databases.requests.model.promotions.PromotionWithType
import databases.requests.playerstats.player._
import databases.requests.promotions.PromotionsRequest
import databases.requests.teamrankings.TeamRankingsRequest
import databases.requests.{ClickhouseStatisticsRequest, OrderingKeyPath}
import hattrick.Hattrick
import models.clickhouse.{NearestMatch, TeamRankings}
import models.web.rest.CountryLevelData
import models.web.rest.LevelData.Rounds
import models.web.{PlayersParameters, RestStatisticsParameters}
import play.api.libs.json.{Json, Writes}
import play.api.mvc.ControllerComponents
import service.{HattrickPeriod, TeamsService}
import service.leagueinfo.{LeagueInfoService, LoadingInfo}
import utils.Romans

import java.util.Date
import javax.inject.Inject
import scala.collection.JavaConverters._
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
  implicit val writes = Json.writes[RestTeamData]
}

object RestTeamRankings {
  implicit val writes = Json.writes[RestTeamRankings]
}

case class RestTeamRankings(teamRankings: Seq[TeamRankings],
                            leagueTeamsCount: Int,
                            divisionLevelTeamsCount: Int,
                            currencyRate: Double,
                            currencyName: String)

case class NearestMatches(playedMatches: Seq[NearestMatch], upcomingMatches: Seq[NearestMatch])

object NearestMatches {
  implicit val writes = Json.writes[NearestMatches]
}

class RestTeamController @Inject() (val controllerComponents: ControllerComponents,
                                    val hattrick: Hattrick,
                                    val leagueInfoService: LeagueInfoService,
                                    val teamsService: TeamsService,
                                    implicit val clickhouseDAO: ClickhouseDAO,
                                    implicit val restClickhouseDAO: RestClickhouseDAO) extends RestController {
  private def getTeamById(teamId: Long): Future[Either[Team, Team]] = Future {
    val user = hattrick.api.teamDetails().teamID(teamId).execute()
    val team = user.getTeams.asScala.filter(_.getTeamId == teamId).head
    if(user.getUser.getUserId == 0L) Left(team)
    else {
      Right(team)
    }
  }

  private def getRestTeamData(team: Team) = {
    val league = leagueInfoService.leagueInfo(team.getLeague.getLeagueId).league

    RestTeamData(
      leagueId = team.getLeague.getLeagueId,
      leagueName = league.getEnglishName,
      divisionLevel = team.getLeagueLevelUnit.getLeagueLevel,
      divisionLevelName = Romans(team.getLeagueLevelUnit.getLeagueLevel),
      leagueUnitId = team.getLeagueLevelUnit.getLeagueLevelUnitId,
      leagueUnitName = team.getLeagueLevelUnit.getLeagueLevelUnitName,
      teamId = team.getTeamId,
      teamName = team.getTeamName,
      foundedDate = team.getFoundedDate.getTime,
      seasonOffset = league.getSeasonOffset,
      seasonRoundInfo = leagueInfoService.leagueInfo.seasonRoundInfo(team.getLeague.getLeagueId),
      currency = if (league.getCountry.getCurrencyName == null) "$" else league.getCountry.getCurrencyName,
      currencyRate = if (league.getCountry.getCurrencyRate == null) 10.0d else league.getCountry.getCurrencyRate,
      loadingInfo = leagueInfoService.leagueInfo(team.getLeague.getLeagueId).loadingInfo,
      countries = leagueInfoService.idToStringCountryMap
    )
  }

  def getTeamData(teamId: Long) = Action.async {
    getTeamById(teamId)
      .map(teamEither => teamEither.map(team => {
        getRestTeamData(team)
      })).map( {
      case Right(data) => Ok(Json.toJson(data))
      case Left(data) => Ok(Json.toJson(getRestTeamData(data)))
    })
  }

  private def getDivisionLevelAndLeagueUnit(team: Team, season: Int): (Int, Long) = {
    val league = hattrick.api.worldDetails().leagueId(team.getLeague.getLeagueId)
      .execute()
      .getLeagueList.get(0)

    val htRound = league.getMatchRound

     if(htRound == 16
      || leagueInfoService.leagueInfo.currentSeason(team.getLeague.getLeagueId) > season
      || league.getSeason - league.getSeasonOffset > season) {
      val infoOpt = clickhouseDAO.historyTeamLeagueUnitInfo(season, team.getLeague.getLeagueId, team.getTeamId)
      infoOpt.map(info => (info.divisionLevel, info.leagueUnitId))
        .getOrElse((team.getLeagueLevelUnit.getLeagueLevel, team.getLeagueLevelUnit.getLeagueLevelUnitId))
    } else {
      (team.getLeagueLevelUnit.getLeagueLevel.toInt, team.getLeagueLevelUnit.getLeagueLevelUnitId.toLong)
    }
  }

  private def stats[T](chRequest: ClickhouseStatisticsRequest[T],
               teamId: Long,
               restStatisticsParameters: RestStatisticsParameters)
              (implicit writes: Writes[T]) = Action.async{ implicit request =>
    getTeamById(teamId).flatMap(teamEither => teamEither.map(team => {

      val (divisionLevel: Int, leagueUnitId: Long) = getDivisionLevelAndLeagueUnit(team, restStatisticsParameters.season)

      chRequest.execute(
        OrderingKeyPath(
          leagueId = Some(team.getLeague.getLeagueId),
          divisionLevel = Some(divisionLevel),
          leagueUnitId = Some(leagueUnitId),
          teamId = Some(teamId)
        ), restStatisticsParameters)
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

        val (divisionLevel: Int, leagueUnitId: Long) = getDivisionLevelAndLeagueUnit(team, restStatisticsParameters.season)

        plRequest.execute(
          OrderingKeyPath(
            leagueId = Some(team.getLeague.getLeagueId),
            divisionLevel = Some(divisionLevel),
            leagueUnitId = Some(leagueUnitId),
            teamId = Some(teamId)
          ), restStatisticsParameters, playersParameters)
      }) match {
        case Right(statList) => statList.map(entities => restTableDataJson(entities, restStatisticsParameters.pageSize))
        case Left(_) => Future(NoContent)
      })
    }

  def playerGoalGames(teamId: Long, restStatisticsParameters: RestStatisticsParameters, playersParameters: PlayersParameters) =
    playersRequest(PlayerGamesGoalsRequest, teamId, restStatisticsParameters, playersParameters)

  def playerCards(teamId: Long, restStatisticsParameters: RestStatisticsParameters,
                  playersParameters: PlayersParameters) =
    playersRequest(PlayerCardsRequest, teamId, restStatisticsParameters, playersParameters)

  def playerTsiSalary(teamId: Long, restStatisticsParameters: RestStatisticsParameters, playersParameters: PlayersParameters) =
    playersRequest(PlayerSalaryTSIRequest, teamId, restStatisticsParameters, playersParameters)

  def playerRatings(teamId: Long, restStatisticsParameters: RestStatisticsParameters, playersParameters: PlayersParameters) =
    playersRequest(PlayerRatingsRequest, teamId, restStatisticsParameters, playersParameters)

  def playerInjuries(teamId: Long, restStatisticsParameters: RestStatisticsParameters) =
    stats(PlayerInjuryRequest, teamId, restStatisticsParameters)

  def topMatches(teamId: Long, restStatisticsParameters: RestStatisticsParameters) =
    stats(MatchTopHatstatsRequest, teamId, restStatisticsParameters)

  def surprisingMatches(teamId: Long, restStatisticsParameters: RestStatisticsParameters) =
    stats(MatchSurprisingRequest, teamId, restStatisticsParameters)

  def matchSpectators(teamId: Long, restStatisticsParameters: RestStatisticsParameters) =
    stats(MatchSpectatorsRequest, teamId, restStatisticsParameters)


  def teamRankings(teamId: Long) = Action.async { implicit request =>
    getTeamById(teamId).flatMap(teamEither => teamEither.map(team => {
      val leagueId = team.getLeague.getLeagueId
      val season = leagueInfoService.leagueInfo.currentSeason(leagueId)

      TeamRankingsRequest.execute(OrderingKeyPath(
        season = Some(season),
        leagueId = Some(leagueId),
        teamId = Some(teamId),
      )).map(teamRankings => {
          val round = leagueInfoService.leagueInfo.currentRound(leagueId)//teamRankings.maxBy(_.round).round
          val leagueInfo = leagueInfoService.leagueInfo(leagueId)
          val leagueTeamsCount = leagueInfo.seasonInfo(season).roundInfo(round).divisionLevelInfo.values.map(_.count).sum
          val divisionLevel = teamRankings.map(_.divisionLevel).headOption.getOrElse(team.getLeagueLevelUnit.getLeagueLevel.toInt)
          val divisionLevelTeamsCount = leagueInfo.seasonInfo(season).roundInfo(round).divisionLevelInfo(divisionLevel).count
          val currencyRate = leagueInfo.league.getCountry.getCurrencyRate
          val currencyName = leagueInfo.league.getCountry.getCurrencyName

          RestTeamRankings(teamRankings = teamRankings,
            leagueTeamsCount = leagueTeamsCount,
            divisionLevelTeamsCount = divisionLevelTeamsCount,
            currencyRate = if(currencyRate == null) 10.0d else currencyRate,
            currencyName = if(currencyName == null) "$" else currencyName)

        })
    }) match {
      case Right(rankings) => rankings.map(r => Ok(Json.toJson(r)))
      case Left(_) => Future(NoContent)
    })
  }

  def nearestMatches(teamId: Long) = Action.async { implicit request =>
    Future(hattrick.api.matches().teamId(teamId)
      .execute()
      .getTeam.getMatchList)
    .map(matchList => {
      val matches = matchList.asScala
        .filter(_.getMatchType == MatchType.LEAGUE_MATCH)
        .map(matc => NearestMatch(matc.getMatchDate, matc.getStatus,
          matc.getHomeTeam.getHomeTeamId, matc.getHomeTeam.getHomeTeamName,
          matc.getHomeGoals, matc.getAwayGoals,
          matc.getAwayTeam.getAwayTeamName, matc.getAwayTeam.getAwayTeamId,
          matc.getMatchId))

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

  def promotions(teamId: Long) = Action.async{implicit request =>
    getTeamById(teamId).flatMap(teamEither => teamEither.map(team => {
      val season = leagueInfoService.leagueInfo.currentSeason(team.getLeague.getLeagueId)

      val (divisionLevel: Int, leagueUnitId: Long) = getDivisionLevelAndLeagueUnit(team, season)

      PromotionsRequest.execute(
        OrderingKeyPath(leagueId = Some(team.getLeague.getLeagueId),
          divisionLevel = Some(divisionLevel),
          leagueUnitId = Some(leagueUnitId),
          teamId = Some(teamId)), season
      )
    }) match {
      case Right(promotions) => promotions.map(PromotionWithType.convert).map(result => Ok(Json.toJson(result)))
      case Left(_) => Future(NoContent)
    })
  }

  def teamMatches(teamId: Long, season: Int) = Action.async( implicit request =>
    getTeamById(teamId).flatMap(teamEither => teamEither.map(team => {
      val (divisionLevel: Int, leagueUnitId: Long) = getDivisionLevelAndLeagueUnit(team, season)

      TeamMatchesRequest.execute(season, OrderingKeyPath(leagueId = Some(team.getLeague.getLeagueId),
        divisionLevel = Some(divisionLevel),
        leagueUnitId = Some(leagueUnitId),
        teamId = Some(team.getTeamId)))
    }) match {
      case Right(matches) => matches.map(result => Ok(Json.toJson(result)))
      case Left(_) => Future(NoContent)
    })
  )

  def teamsFoundedSameDate(period: HattrickPeriod, leagueId: Int, foundedDate: Long) = Action.async { implicit request =>
    teamsService.teamsCreatedSamePeriod(period, new Date(foundedDate), leagueId)
      .map(teams => Ok(Json.toJson(teams)))
  }

  def compareTeams(team1: Long, team2: Long) = Action.async{ implicit request =>
    teamsService.compareTwoTeams(team1, team2)
      .map(rankings => Ok(Json.toJson(rankings)))
  }
}
