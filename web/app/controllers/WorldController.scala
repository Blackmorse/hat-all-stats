package controllers

import chpp.search.SearchRequest
import chpp.search.models.{Search, SearchType}
import chpp.teamdetails.TeamDetailsRequest
import chpp.teamdetails.models.TeamDetails
import databases.dao.RestClickhouseDAO
import databases.requests.OrderingKeyPath
import databases.requests.matchdetails.{MatchSurprisingRequest, MatchTopHatstatsRequest, TeamHatstatsRequest}
import databases.requests.playerstats.dreamteam.DreamTeamRequest
import databases.requests.playerstats.player.{PlayerRatingsRequest, PlayerSalaryTSIRequest}
import databases.requests.teamdetails.OldestTeamsRequest
import webclients.ChppClient
import models.web.{PlayersParameters, RestStatisticsParameters, StatsType}
import play.api.cache.AsyncCacheApi
import play.api.i18n.I18nSupport
import play.api.libs.json.{Json, OWrites}
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import service.leagueinfo.LeagueInfoService
import service.RequestCounterService

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class TeamSearchResult(teamId: Long, teamName: String)

object TeamSearchResult {
  implicit val writes: OWrites[TeamSearchResult] = Json.writes[TeamSearchResult]
}

@Singleton
class WorldController @Inject() (val controllerComponents: ControllerComponents,
             implicit val restClickhouseDAO: RestClickhouseDAO,
             val leagueInfoService: LeagueInfoService,
             val chppClient: ChppClient,
             val requestCounterService: RequestCounterService,
             val cache: AsyncCacheApi)
        extends RestController with I18nSupport with MessageSupport {


  def searchByName(name: String): Action[AnyContent] = Action.async { implicit request =>
    chppClient.execute[Search, SearchRequest](SearchRequest(searchType = Some(SearchType.TEAMS), searchString = Some(name)))
      .map(results => results.searchResults.map(result => TeamSearchResult(result.resultId, result.resultName)))
      .map(r => Ok(Json.toJson(r)))
  }

  def searchById(id: Long): Action[AnyContent] = Action.async { implicit request =>
    chppClient.execute[TeamDetails, TeamDetailsRequest](TeamDetailsRequest(teamId = Some(id)))
      .map(result => {
        result.teams
          .filter(_.teamId == id)
          .map(team => TeamSearchResult(team.teamId, team.teamName))
      })
      .map(r => Ok(Json.toJson(r)))
  }

  def health(): Action[AnyContent] = Action.async { implicit request =>
    Future(Ok(Json.toJson("")))
  }

  def hoRequests(): Action[AnyContent] = Action.async{ implicit request =>
    val requests = requestCounterService.getHoRequests
    Future(Ok(Json.toJson(requests)))
  }

  def teamHatstats(restStatisticsParameters: RestStatisticsParameters): Action[AnyContent] = Action.async{ implicit request =>
    TeamHatstatsRequest.execute(OrderingKeyPath(), restStatisticsParameters)
      .map(entities => restTableDataJson(entities, restStatisticsParameters.pageSize))
  }

  def playersTsiSalary(restStatisticsParameters: RestStatisticsParameters, playersParameters: PlayersParameters): Action[AnyContent] =
    Action.async{ implicit request =>
      PlayerSalaryTSIRequest.execute(OrderingKeyPath(), restStatisticsParameters, playersParameters)
        .map(entities => restTableDataJson(entities, restStatisticsParameters.pageSize))
  }

  def playerRatings(restStatisticsParameters: RestStatisticsParameters, playersParameters: PlayersParameters): Action[AnyContent] =
    Action.async { implicit request =>
      PlayerRatingsRequest.execute(OrderingKeyPath(), restStatisticsParameters, playersParameters)
        .map(entities => restTableDataJson(entities, restStatisticsParameters.pageSize))
    }

  def topMatches(restStatisticsParameters: RestStatisticsParameters): Action[AnyContent] =
    Action.async { implicit request =>
      MatchTopHatstatsRequest.execute(OrderingKeyPath(), restStatisticsParameters)
        .map(entities => restTableDataJson(entities, restStatisticsParameters.pageSize))
    }

  def surprisingMatches(restStatisticsParameters: RestStatisticsParameters): Action[AnyContent] = Action.async { implicit request =>
    MatchSurprisingRequest.execute(OrderingKeyPath(), restStatisticsParameters)
      .map(entities => restTableDataJson(entities, restStatisticsParameters.pageSize))
  }

  def oldestTeams(restStatisticsParameters: RestStatisticsParameters): Action[AnyContent] = Action.async { implicit request =>
    OldestTeamsRequest.execute(OrderingKeyPath(), restStatisticsParameters)
      .map(entities => restTableDataJson(entities, restStatisticsParameters.pageSize))
  }

  def dreamTeam(season: Int, sortBy: String, statsType: StatsType): Action[AnyContent] =
    Action.async {implicit request =>
      cache.getOrElseUpdate(s"worldDreamTeam_s${season}_r${statsType.toString}")(
        DreamTeamRequest.execute(OrderingKeyPath(season = Some(season)),
          statsType,
          sortBy)
      )
        .map(players => Ok(Json.toJson(players)))
    }
}
