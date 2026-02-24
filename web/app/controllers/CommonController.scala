package controllers

import cache.ZioCacheModule.HattidEnv
import chpp.search.SearchRequest
import chpp.search.models.SearchType
import models.web.teams.TeamSearchResult
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import service.{ChppService, RequestCounterService}
import zio.ZIO
import javax.inject.{Inject, Singleton}

@Singleton
class CommonController @Inject() (val controllerComponents: ControllerComponents,
                                  val requestCounterService: RequestCounterService,
                                  val hattidEnvironment: zio.ZEnvironment[HattidEnv]) extends RestController(hattidEnvironment) {

  def searchByName(name: String): Action[AnyContent] = asyncZio {
    for {
      chppService <- ZIO.service[ChppService]
      results <- chppService.search(SearchRequest(searchType = Some(SearchType.TEAMS), searchString = Some(name)))
    } yield results.searchResults
      .map(result => TeamSearchResult(result.resultId, result.resultName))
  }

  def searchById(id: Long): Action[AnyContent] = asyncZio {
    for {
      chppService <- ZIO.service[ChppService]
      result      <- chppService.getTeamsSimple(id)
    } yield result.teams
      .filter(_.teamId == id)
      .map(team => TeamSearchResult(team.teamId, team.teamName))
  }

  def health(): Action[AnyContent] = asyncZio { 
    ZIO.succeed("")
  }

  def hoRequests(): Action[AnyContent] = asyncZio {
    val requests = requestCounterService.getHoRequests
    ZIO.succeed(requests)
  }
}
