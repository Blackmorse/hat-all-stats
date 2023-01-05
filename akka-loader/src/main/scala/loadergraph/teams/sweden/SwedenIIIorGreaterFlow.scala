package loadergraph.teams.sweden

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.scaladsl.{Flow, Source}
import chpp.OauthTokens
import httpflows.SearchHttpFlow
import hattid.CommonData.arabToRomans
import loadergraph.teams.{LeagueWithLevel, StandardLeagueFlow}
import models.stream.LeagueUnit

import scala.concurrent.ExecutionContext

object SwedenIIIorGreaterFlow {
  def apply()(implicit oauthTokens: OauthTokens, system: ActorSystem,
              executionContext: ExecutionContext): Flow[LeagueWithLevel, LeagueUnit, NotUsed] = {
    Flow[LeagueWithLevel].map(leagueWithLevel => {
      val romanLevel = arabToRomans(leagueWithLevel.level - 1)
      StandardLeagueFlow.searchRequest(leagueWithLevel, romanLevel)
    })
      .via(SearchHttpFlow())
      .flatMapConcat{case(search, leagueWithLevel) =>
        val romanLevel = arabToRomans(leagueWithLevel.level - 1)
        val pagesRequests = (0 until search.pages).map(page => {
          StandardLeagueFlow.searchRequest(leagueWithLevel, romanLevel, page)
        })
        Source(pagesRequests)
      }
      .async
      .via(SearchHttpFlow())
      .flatMapConcat {
        case(search, leagueWithLevel) => StandardLeagueFlow.leagueUnitsSourceFromResult(search, leagueWithLevel)
      }
  }
}
