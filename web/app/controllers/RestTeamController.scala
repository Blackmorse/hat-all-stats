package controllers

import com.blackmorse.hattrick.api.teamdetails.model.Team
import com.blackmorse.hattrick.model.enums.MatchType
import databases.{ClickhouseDAO, RestClickhouseDAO}
import databases.clickhouse.StatisticsCHRequest
import databases.requests.OrderingKeyPath
import databases.requests.teamrankings.TeamRankingsRequest
import hattrick.Hattrick
import io.swagger.annotations.Api
import javax.inject.Inject
import models.clickhouse.{NearestMatch, TeamRankings}
import models.web.{RestStatisticsParameters, RestTableData, StatisticsParameters}
import models.web.rest.LevelData
import models.web.rest.LevelData.Rounds
import play.api.libs.json.Json
import play.api.mvc.{BaseController, ControllerComponents}
import service.LeagueInfoService
import utils.Romans

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import collection.JavaConverters._

case class RestTeamData(leagueId: Int,
                        leagueName: String,
                        divisionLevel: Int,
                        divisionLevelName: String,
                        leagueUnitId: Long,
                        leagueUnitName: String,
                        teamId: Long,
                        teamName: String,
                        seasonRoundInfo: Seq[(Int, Rounds)]) extends LevelData

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

@Api(produces = "application/json")
class RestTeamController @Inject() (val controllerComponents: ControllerComponents,
                                    val hattrick: Hattrick,
                                    val leagueInfoService: LeagueInfoService,
                                    implicit val clickhouseDAO: ClickhouseDAO,
                                    implicit val restClickhouseDAO: RestClickhouseDAO) extends BaseController {
  private def getTeamById(teamId: Long): Future[Team] = Future {
    hattrick.api.teamDetails().teamID(teamId).execute()
      .getTeams.asScala.filter(_.getTeamId == teamId).head
  }

  def getTeamData(teamId: Long) = Action.async {
    getTeamById(teamId)
      .map(team => {
        Ok(Json.toJson(
          RestTeamData(
            leagueId = team.getLeague.getLeagueId,
            leagueName = leagueInfoService.leagueInfo(team.getLeague.getLeagueId).league.getEnglishName,
            divisionLevel = team.getLeagueLevelUnit.getLeagueLevel,
            divisionLevelName = Romans(team.getLeagueLevelUnit.getLeagueLevel),
            leagueUnitId = team.getLeagueLevelUnit.getLeagueLevelUnitId,
            leagueUnitName = team.getLeagueLevelUnit.getLeagueLevelUnitName,
            teamId = teamId,
            teamName = team.getTeamName,
            seasonRoundInfo = leagueInfoService.leagueInfo.seasonRoundInfo(team.getLeague.getLeagueId)
          )
        ))
      })
  }

  def playerStats(teamId: Long, restStatisticsParameters: RestStatisticsParameters) = Action.async { implicit request =>
    val statisticsParameters =
      StatisticsParameters(season = restStatisticsParameters.season,
        page = restStatisticsParameters.page,
        statsType = restStatisticsParameters.statsType,
        sortBy = restStatisticsParameters.sortBy,
        pageSize = restStatisticsParameters.pageSize,
        sortingDirection = restStatisticsParameters.sortingDirection
      )

    getTeamById(teamId).flatMap(team => {
      val league = hattrick.api.worldDetails().leagueId(team.getLeague.getLeagueId)
        .execute()
        .getLeagueList.get(0)
      val htRound = league.getMatchRound

      val (divisionLevel: Int, leagueUnitId: Long) = if(htRound == 16
                || leagueInfoService.leagueInfo.currentSeason(team.getLeague.getLeagueId) > statisticsParameters.season
                || league.getSeason - league.getSeasonOffset > statisticsParameters.season) {
        val infoOpt = clickhouseDAO.historyTeamLeagueUnitInfo(statisticsParameters.season, team.getLeague.getLeagueId, teamId)
        infoOpt.map(info => (info.divisionLevel, info.leagueUnitId))
          .getOrElse((team.getLeagueLevelUnit.getLeagueLevel, team.getLeagueLevelUnit.getLeagueLevelUnitId))
      } else {
        (team.getLeagueLevelUnit.getLeagueLevel.toInt, team.getLeagueLevelUnit.getLeagueLevelUnitId.toLong)
      }

      StatisticsCHRequest.playerStatsRequest.execute(
        leagueId = Some(team.getLeague.getLeagueId),
        divisionLevel = Some(divisionLevel),
        leagueUnitId = Some(leagueUnitId),
        teamId = Some(teamId),
        statisticsParameters = statisticsParameters
      ).map(playerStats => {
        val isLastPage = playerStats.size <= statisticsParameters.pageSize

        val entities = if(!isLastPage) playerStats.dropRight(1) else playerStats
        val restTableData = RestTableData(entities, isLastPage)
        Ok(Json.toJson(restTableData))
      })
    })
  }

  def teamRankings(teamId: Long) = Action.async { implicit request =>
    getTeamById(teamId).flatMap(team => {
      val leagueId = team.getLeague.getLeagueId
      val season = leagueInfoService.leagueInfo.currentSeason(leagueId)

      TeamRankingsRequest.execute(OrderingKeyPath(
        season = Some(season),
        leagueId = Some(leagueId),
        teamId = Some(teamId),
      )).map(teamRankings => {
          val round = teamRankings.maxBy(_.round).round
          val leagueInfo = leagueInfoService.leagueInfo(leagueId)
          val leagueTeamsCount = leagueInfo.seasonInfo(season).roundInfo(round).divisionLevelInfo.values.map(_.count).sum
          val divisionLevelTeamsCount = leagueInfo.seasonInfo(season).roundInfo(round).divisionLevelInfo(team.getLeagueLevelUnit.getLeagueLevel).count
          val currencyRate = leagueInfo.league.getCountry.getCurrencyRate
          val currencyName = leagueInfo.league.getCountry.getCurrencyName

          val restTeamRankings = RestTeamRankings(teamRankings = teamRankings,
            leagueTeamsCount = leagueTeamsCount,
            divisionLevelTeamsCount = divisionLevelTeamsCount,
            currencyRate = if(currencyRate == null) 10.0d else currencyRate,
            currencyName = currencyName)
          Ok(Json.toJson(restTeamRankings))
        })
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
      Ok(Json.toJson(NearestMatches(playedMatches, upcomingMatches)))
    })
  }
}
