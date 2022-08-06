package controllers

import databases.dao.RestClickhouseDAO
import databases.requests.OrderingKeyPath
import databases.requests.matchdetails.{MatchSurprisingRequest, MatchTopHatstatsRequest, TeamHatstatsRequest}
import databases.requests.playerstats.dreamteam.DreamTeamRequest
import databases.requests.playerstats.player.stats.{PlayerRatingsRequest, PlayerSalaryTSIRequest}
import databases.requests.teamdetails.OldestTeamsRequest
import models.web.{PlayersParameters, RestStatisticsParameters, StatsType}
import play.api.cache.AsyncCacheApi
import play.api.i18n.I18nSupport
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import service.leagueinfo.LeagueInfoService
import webclients.ChppClient

import javax.inject.{Inject, Singleton}
//TODO
import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class WorldController @Inject() (val controllerComponents: ControllerComponents,
             implicit val restClickhouseDAO: RestClickhouseDAO,
             val leagueInfoService: LeagueInfoService,
             val chppClient: ChppClient,
             val cache: AsyncCacheApi)
        extends RestController with I18nSupport with MessageSupport {

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
