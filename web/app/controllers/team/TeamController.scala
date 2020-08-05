package controllers

import com.blackmorse.hattrick.api.teamdetails.model.{Team, TeamDetails}
import com.blackmorse.hattrick.model.enums.MatchType
import databases.ClickhouseDAO
import databases.clickhouse.{Accumulated, OnlyRound, StatisticsCHRequest}
import hattrick.Hattrick
import javax.inject.{Inject, Singleton}
import models.web._
import play.api.mvc.{BaseController, Call, ControllerComponents}
import service.{LeagueInfoService, DefaultService, LeagueInfo}

import collection.JavaConverters._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global



case class WebTeamDetails(teamId: Long, teamName: String, leagueInfo: LeagueInfo, season: Int,
                          divisionLevel: Int, leagueUnitId: Long, leagueUnitName: String) extends AbstractWebDetails

@Singleton
class TeamController @Inject()(val controllerComponents: ControllerComponents,
                               implicit val clickhouseDAO: ClickhouseDAO,
                               val hattrick: Hattrick,
                               val leagueInfoService: LeagueInfoService,
                               val defaultService: DefaultService,
                               val matchController: MatchController,
                               val viewDataFactory: ViewDataFactory) extends BaseController with MessageSupport {

  private def fetchWebTeamDetails(team: Team, season: Int) =
    WebTeamDetails(teamId = team.getTeamId,
      teamName = team.getTeamName,
      leagueInfo = leagueInfoService.leagueInfo(team.getLeague.getLeagueId),
      season = season,
      divisionLevel = team.getLeagueLevelUnit.getLeagueLevel,
      leagueUnitId = team.getLeagueLevelUnit.getLeagueLevelUnitId,
      leagueUnitName = team.getLeagueLevelUnit.getLeagueLevelUnitName)

  private def fetchWebTeamDetails(teamDetails: TeamDetails, teamId: Long): WebTeamDetails = {
    val team = teamDetails.getTeams.asScala.filter(_.getTeamId == teamId).head
    val season = leagueInfoService.leagueInfo.currentSeason(team.getLeague.getLeagueId)
    fetchWebTeamDetails(team, season)
  }

  def teamOverview(teamId: Long) = Action.async { implicit request =>
    val teamDetailsFuture = Future(hattrick.api.teamDetails().teamID(teamId).execute())

    val matchesFuture = Future(hattrick.api.matches().teamId(teamId)
      .execute()
      .getTeam.getMatchList)

    teamDetailsFuture.zipWith(matchesFuture) { case (teamDetails, matchList) =>
      val webTeamDetails = fetchWebTeamDetails(teamDetails, teamId)

      if(teamDetails.getUser.getUserId == 0L) {
        Future(Ok(views.html.team.bot(webTeamDetails)(messages)))
      } else {
        val matches = matchList.asScala
          .filter(_.getMatchType == MatchType.LEAGUE_MATCH)
        val teamRankingsFuture = clickhouseDAO.teamRankings(season = webTeamDetails.season,
          leagueId = webTeamDetails.leagueInfo.leagueId,
          divisionLevel = webTeamDetails.divisionLevel,
          leagueUnitId = webTeamDetails.leagueUnitId,
          teamId = teamId)

        val playedMatches = matches
          .filter(_.getStatus == "FINISHED")
          .sortBy(_.getMatchDate)
          .takeRight(3)

        val upcomingMatches = matches.filter(_.getStatus == "UPCOMING")
          .sortBy(_.getMatchDate)
          .take(3)

        val pageSize = request.cookies.get("hattid_page_size").map(_.value.toInt).getOrElse(DefaultService.PAGE_SIZE)

        teamRankingsFuture.map { teamRankings => {
          val divisionLevelTeamRankings = teamRankings.filter(_.rank_type == "division_level").sortBy(_.round).reverse
          val leagueTeamRankings = teamRankings.filter(_.rank_type == "league_id").sortBy(_.round).reverse
          Ok(views.html.team.teamOverview(leagueTeamRankings, divisionLevelTeamRankings,
            webTeamDetails, playedMatches, upcomingMatches, pageSize)(messages))
        }
        }
      }
    }.flatten
  }

  def matches(teamId: Long) = Action.async { implicit request =>
    val teamDetails = hattrick.api.teamDetails().teamID(teamId).execute()
    val details = fetchWebTeamDetails(teamDetails, teamId)

    if (teamDetails.getUser.getUserId == 0L) {
      Future(Ok(views.html.team.bot(details)(messages)))
    } else {
      matchController.matchesFuture(details, details.season) map (matches => {
        Ok(views.html.team.matches(matches, details)(messages))
      })
    }
  }

  def playerStats(teamId: Long, statisticsParametersOpt: Option[StatisticsParameters]) = Action.async { implicit request =>
    val func: StatisticsParameters => Call = sp => routes.TeamController.playerStats(teamId, Some(sp))

    val teamDetails = hattrick.api.teamDetails().teamID(teamId).execute()

    val webDetails = fetchWebTeamDetails(teamDetails, teamId)

    if(teamDetails.getUser.getUserId == 0L) {
      Future(Ok(views.html.team.bot(webDetails)(messages)))
    } else {

      val (statisticsParameters, cookies) = defaultService.statisticsParameters(statisticsParametersOpt,
          leagueId = webDetails.leagueInfo.league.getLeagueId,
          statsType = Accumulate,
          sortColumn = "scored")

      val details = fetchWebTeamDetails(teamDetails, teamId)

      StatisticsCHRequest.playerStatsRequest.execute(leagueId = Some(webDetails.leagueInfo.league.getLeagueId),
        divisionLevel = Some(webDetails.divisionLevel),
        leagueUnitId = Some(webDetails.leagueUnitId),
        teamId = Some(teamId),
        statisticsParameters = statisticsParameters)
        .map(playerStats => {
          val viewData = viewDataFactory.create(details = details,
            func = func,
            statisticsType = Accumulated,
            statisticsParameters = statisticsParameters,
            statisticsCHRequest = StatisticsCHRequest.playerStatsRequest,
            entities = playerStats)
          Ok(views.html.team.playerStats(viewData)(messages)).withCookies(cookies: _*)
        })
    }
  }

  def playerState(teamId: Long, statisticsParametersOpt: Option[StatisticsParameters]) = Action.async { implicit request =>

    val func: StatisticsParameters => Call = sp => routes.TeamController.playerState(teamId, Some(sp))

    val teamDetails = hattrick.api.teamDetails().teamID(teamId).execute()

    val team = teamDetails.getTeams.asScala.filter(_.getTeamId == teamId).head

    val leagueId = team.getLeague.getLeagueId
    val divisionLevel = team.getLeagueLevelUnit.getLeagueLevel
    val leagueUnitId = team.getLeagueLevelUnit.getLeagueLevelUnitId

    val currentRound = leagueInfoService.leagueInfo.currentRound(leagueId)

    val (statisticsParameters, cookies) = defaultService.statisticsParameters(statisticsParametersOpt,
        leagueId = leagueId,
        statsType = Round(currentRound),
        sortColumn = "rating")

    val details = fetchWebTeamDetails(team, statisticsParameters.season)

    if (teamDetails.getUser.getUserId == 0L) {
      Future(Ok(views.html.team.bot(details)(messages)))
    } else {
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
        }).map(viewData => Ok(views.html.team.playerState(viewData)(messages)).withCookies(cookies: _*))
    }
  }
}


