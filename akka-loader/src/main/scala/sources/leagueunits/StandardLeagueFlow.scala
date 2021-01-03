package sources.leagueunits

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.scaladsl.{Flow, Source}
import com.blackmorse.hattrick.common.CommonData
import flows.SearchFlow
import models.OauthTokens
import models.chpp.search.{Search, SearchType}
import models.stream.LeagueUnit
import requests.SearchRequest

import scala.concurrent.ExecutionContext

object StandardLeagueFlow {
  def searchRequest(leagueWithLevel: LeagueWithLevel, romanLevel: String,
                    page: Int = 0) = {
    val searchRequest = SearchRequest(searchType = Some(SearchType.SERIES),
      searchLeagueId = Some(leagueWithLevel.league.leagueId),
      pageIndex = Some(page),
      searchString = Some(s"$romanLevel.")
    )
    (searchRequest, leagueWithLevel)
  }

  def leagueUnitsSourceFromResult(search: Search, leagueWithLevel: LeagueWithLevel) = {
    val leagueUnits = search.searchResults.map(result =>
      LeagueUnit(leagueUnitId = result.resultId.toInt,
        leagueUnitName = result.resultName,
        level = leagueWithLevel.level,
        league = leagueWithLevel.league
      ))
    Source(leagueUnits.toList)
  }

  def apply()(implicit oauthTokens: OauthTokens, system: ActorSystem,
              executionContext: ExecutionContext): Flow[LeagueWithLevel, LeagueUnit, NotUsed] = {
    Flow[LeagueWithLevel].map(leagueWithLevel => {
      val romanLevel = CommonData.arabToRomans.get(leagueWithLevel.level)
      searchRequest(leagueWithLevel, romanLevel)
    })
      .via(SearchFlow())
      .flatMapConcat{case(search, leagueWithLevel) =>
        val romanLevel = CommonData.arabToRomans.get(leagueWithLevel.level)
        val pagesRequests = (0 until search.pages).map(page => {
          searchRequest(leagueWithLevel, romanLevel, page)
        })
        Source(pagesRequests)
      }
      .async
      .via(SearchFlow())
      .flatMapConcat {
        case(search, leagueWithLevel) => leagueUnitsSourceFromResult(search, leagueWithLevel)
      }
  }
}
