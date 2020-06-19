package controllers

import com.blackmorse.hattrick.api.teamdetails.model.{Team, TeamDetails}
import com.blackmorse.hattrick.api.worlddetails.model.League
import databases.ClickhouseDAO
import databases.clickhouse.{Accumulated, OnlyRound, StatisticsCHRequest}
import hattrick.Hattrick
import javax.inject.{Inject, Singleton}
import models.web._
import play.api.mvc.{BaseController, Call, ControllerComponents}
import service.{DefaultService, LeagueInfo}

import scala.concurrent.ExecutionContext.Implicits.global
import collection.JavaConverters._



case class WebTeamDetails(teamId: Long, teamName: String, leagueInfo: LeagueInfo, season: Int,
                          divisionLevel: Int, leagueUnitId: Long, leagueUnitName: String) extends AbstractWebDetails

@Singleton
class TeamController @Inject()(val controllerComponents: ControllerComponents,
                               implicit val clickhouseDAO: ClickhouseDAO,
                               val hattrick: Hattrick,
                               val defaultService: DefaultService,
                               val matchController: MatchController,
                               val viewDataFactory: ViewDataFactory) extends BaseController with MessageSupport {

  private def fetchTeamDetails(teamId: Long) =
    hattrick.api.teamDetails().teamID(teamId).execute().getTeams.asScala.filter(_.getTeamId == teamId).head

  private def fetchWebTeamDetails(team: Team, season: Int) =
    WebTeamDetails(teamId = team.getTeamId,
      teamName = team.getTeamName,
      leagueInfo = defaultService.leagueInfo(team.getLeague.getLeagueId),
      season = season,
      divisionLevel = team.getLeagueLevelUnit.getLeagueLevel,
      leagueUnitId = team.getLeagueLevelUnit.getLeagueLevelUnitId,
      leagueUnitName = team.getLeagueLevelUnit.getLeagueLevelUnitName)

  def teamRankings(teamId: Long) = Action.async { implicit request =>
    val teamDetails = fetchTeamDetails(teamId)
    val season = defaultService.leagueInfo.currentSeason(teamDetails.getLeague.getLeagueId)

    val teamRankingsFuture = clickhouseDAO.teamRankings(season = season,
      leagueId = teamDetails.getLeague.getLeagueId,
      divisionLevel = teamDetails.getLeagueLevelUnit.getLeagueLevel,
      leagueUnitId = teamDetails.getLeagueLevelUnit.getLeagueLevelUnitId,
      teamId = teamId)

    val webTeamDetails = fetchWebTeamDetails(teamDetails, season)

    teamRankingsFuture.map {teamRankings => {
      val divisionLevelTeamRankings = teamRankings.filter(_.rank_type == "division_level").sortBy(_.round).reverse
      val leagueTeamRankings = teamRankings.filter(_.rank_type == "league_id").sortBy(_.round).reverse
      Ok(views.html.team.teamRankings(leagueTeamRankings, divisionLevelTeamRankings, webTeamDetails)(messages))
    }}
  }

  def matches(teamId: Long) = Action.async { implicit request =>
    val teamDetails = fetchTeamDetails(teamId)
    val season = defaultService.leagueInfo.currentSeason(teamDetails.getLeague.getLeagueId)

    matchController.matchesFuture(teamDetails, season) map (matches => {
      val details = fetchWebTeamDetails(teamDetails, season)

      Ok(views.html.team.matches(matches, details)(messages))
    })
  }

  def playerStats(teamId: Long, statisticsParametersOpt: Option[StatisticsParameters]) = Action.async { implicit request =>
    val func: StatisticsParameters => Call = sp => routes.TeamController.playerStats(teamId, Some(sp))

    val teamDetails = fetchTeamDetails(teamId)

    val leagueId = teamDetails.getLeague.getLeagueId
    val divisionLevel = teamDetails.getLeagueLevelUnit.getLeagueLevel
    val leagueUnitId = teamDetails.getLeagueLevelUnit.getLeagueLevelUnitId

    val statisticsParameters =
      statisticsParametersOpt.getOrElse(StatisticsParameters(defaultService.leagueInfo.currentSeason(leagueId), 0, Accumulate, "scored", DefaultService.PAGE_SIZE, Desc))

    val details = fetchWebTeamDetails(teamDetails, statisticsParameters.season)

   StatisticsCHRequest.playerStatsRequest.execute(leagueId = Some(leagueId),
     divisionLevel = Some(divisionLevel),
     leagueUnitId = Some(leagueUnitId),
     teamId = Some(teamId),
     statisticsParameters = statisticsParameters)
       .map(playerStats => {
         val viewData = viewDataFactory.create(details = details,
           func = func,
           statisticsType = Accumulated,
           statisticsParameters = statisticsParameters,
           statisticsCHRequest = StatisticsCHRequest.playerStatsRequest,
           entities = playerStats)
         Ok(views.html.team.playerStats(viewData)(messages))

       })
  }

  def playerState(teamId: Long, statisticsParametersOpt: Option[StatisticsParameters]) = Action.async { implicit request =>

    val func: StatisticsParameters => Call = sp => routes.TeamController.playerState(teamId, Some(sp))

    val teamDetails = fetchTeamDetails(teamId)

    val leagueId = teamDetails.getLeague.getLeagueId
    val divisionLevel = teamDetails.getLeagueLevelUnit.getLeagueLevel
    val leagueUnitId = teamDetails.getLeagueLevelUnit.getLeagueLevelUnitId

    val currentRound = defaultService.leagueInfo.currentRound(leagueId)
    val statisticsParameters =
      statisticsParametersOpt.getOrElse(StatisticsParameters(defaultService.leagueInfo.currentSeason(leagueId), 0, Round(currentRound), "rating", DefaultService.PAGE_SIZE, Desc))

    val details = fetchWebTeamDetails(teamDetails, statisticsParameters.season)

    StatisticsCHRequest.playerStateRequest.execute(leagueId = Some(leagueId),
      divisionLevel = Some(divisionLevel),
      leagueUnitId = Some(leagueUnitId),
      teamId = Some(teamId),
      statisticsParameters = statisticsParameters)
        .map(playerStates => {
          viewDataFactory.create(details = details,
            func = func,
            statisticsType = OnlyRound,
            statisticsParameters = statisticsParameters,
            statisticsCHRequest = StatisticsCHRequest.playerStateRequest,
            entities = playerStates)
        }).map(viewData => Ok(views.html.team.playerState(viewData)(messages)))
  }
}


