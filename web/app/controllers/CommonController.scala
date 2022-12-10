package controllers

import chpp.search.SearchRequest
import chpp.search.models.{Search, SearchType}
import chpp.teamdetails.TeamDetailsRequest
import chpp.teamdetails.models.TeamDetails
import models.web.teams.TeamSearchResult
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents}
import service.{RequestCounterService, TranslationsService}
import webclients.ChppClient
//TODO
import scala.concurrent.ExecutionContext.Implicits.global

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

@Singleton
class CommonController @Inject() (val controllerComponents: ControllerComponents,
                                  val chppClient: ChppClient,
                                  val requestCounterService: RequestCounterService,
                                  val translationsService: TranslationsService) extends BaseController {

  def searchByName(name: String): Action[AnyContent] = Action.async { implicit request =>
    chppClient.executeUnsafe[Search, SearchRequest](SearchRequest(searchType = Some(SearchType.TEAMS), searchString = Some(name)))
      .map(results => results.searchResults.map(result => TeamSearchResult(result.resultId, result.resultName)))
      .map(r => Ok(Json.toJson(r)))
  }

  def searchById(id: Long): Action[AnyContent] = Action.async { implicit request =>
    chppClient.executeUnsafe[TeamDetails, TeamDetailsRequest](TeamDetailsRequest(teamId = Some(id)))
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
}
