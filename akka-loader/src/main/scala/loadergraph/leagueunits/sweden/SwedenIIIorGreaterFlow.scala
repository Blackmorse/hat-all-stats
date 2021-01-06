package loadergraph.leagueunits.sweden

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.scaladsl.{Flow, Source}
import chpp.OauthTokens
import chpp.search.models.SearchHttpFlow
import com.blackmorse.hattrick.common.CommonData
import loadergraph.leagueunits.{LeagueWithLevel, StandardLeagueFlow}
import models.stream.LeagueUnit

import scala.concurrent.ExecutionContext

object SwedenIIIorGreaterFlow {
  def apply()(implicit oauthTokens: OauthTokens, system: ActorSystem,
              executionContext: ExecutionContext): Flow[LeagueWithLevel, LeagueUnit, NotUsed] = {
    Flow[LeagueWithLevel].map(leagueWithLevel => {
      val romanLevel = CommonData.arabToRomans.get(leagueWithLevel.level - 1)
      StandardLeagueFlow.searchRequest(leagueWithLevel, romanLevel)
    })
      .via(SearchHttpFlow())
      .flatMapConcat{case(search, leagueWithLevel) =>
        val romanLevel = CommonData.arabToRomans.get(leagueWithLevel.level - 1)
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
