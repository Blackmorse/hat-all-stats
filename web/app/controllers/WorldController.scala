package controllers

import com.blackmorse.hattrick.model.enums.SearchType
import hattrick.Hattrick
import javax.inject.{Inject, Singleton}
import play.api.mvc.ControllerComponents
import play.api.i18n.I18nSupport
import play.api.libs.json.Json
import play.api.mvc.BaseController
import service.leagueinfo.LeagueInfoService

import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext.Implicits.global
import service.{DefaultService, OverviewStatsService, RequestCounterService}

import scala.concurrent.Future

case class TeamSearchResult(teamId: Long, teamName: String)

object TeamSearchResult {
  implicit val writes = Json.writes[TeamSearchResult]
}

@Singleton
class WorldController @Inject() (val controllerComponents: ControllerComponents,
            val overviewStatsService: OverviewStatsService,
             val leagueInfoService: LeagueInfoService,
             val hattrick: Hattrick,
             val requestCounterService: RequestCounterService)
        extends BaseController with I18nSupport with MessageSupport {

  def overview() = Action.async {implicit request =>
    val pageSize = request.cookies.get("hattid_page_size").map(_.value.toInt).getOrElse(DefaultService.PAGE_SIZE)
    overviewStatsService.overviewStatistics().map(overviewStatistics => {
      Ok(views.html.world.worldOverview(overviewStatistics,
        leagueInfoService.leagueInfo(1000).league,
        Some(leagueInfoService.leagueInfo),
        pageSize)(messages))
    })
  }

  def searchByName(name: String) = Action.async {implicit request =>
    Future(hattrick.api.search().searchType(SearchType.TEAMS)
      .searchString(name).execute())
      .map(search => search.getSearchResults.asScala
          .map(result => TeamSearchResult(result.getResultId, result.getResultName)))
      .map(r => Ok(Json.toJson(r)))
  }

  def health() = Action.async {implicit request =>
    Future(Ok(Json.toJson("")))
  }

  def hoRequests() = Action.async{ implicit request =>
    val requests = requestCounterService.getHoRequests
    Future(Ok(Json.toJson(requests)))
  }
}
