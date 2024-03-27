package loadergraph.teams.sweden

import org.apache.pekko.NotUsed
import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.stream.scaladsl.{Flow, Source}
import chpp.OauthTokens
import loadergraph.teams.LeagueWithLevel
import chpp.search.SearchRequest
import chpp.search.models.SearchType
import httpflows.SearchHttpFlow
import hattid.CommonData.{arabToRomans, leagueLevelNumberTeams}
import models.stream.LeagueUnit

import scala.concurrent.ExecutionContext

object SwedenIorIIFlow {
  def apply(level: Int)(implicit oauthTokens: OauthTokens, system: ActorSystem,
              executionContext: ExecutionContext): Flow[LeagueWithLevel, LeagueUnit, NotUsed] = {
    if(level != 2 && level != 3) throw new RuntimeException()

    val romanLevel = arabToRomans(level - 1)
    val leagueNumber = leagueLevelNumberTeams(level)

    Flow[LeagueWithLevel].map(leagueWithLevel => {
      val searchRequest = SearchRequest(searchType = Some(SearchType.SERIES),
        searchLeagueId = Some(leagueWithLevel.league.leagueId),
        pageIndex = Some(0),
        searchString = Some(s"${romanLevel}a"))
      (searchRequest, leagueWithLevel)
    })
      .via(SearchHttpFlow())
      .flatMapConcat{case(search, leagueWithLevel: LeagueWithLevel) => {
        val baseId = search.searchResults.head.resultId
        val leagueUnits = (0 until leagueNumber).map(i => LeagueUnit(leagueUnitId = baseId.toInt + i,
          leagueUnitName = romanLevel + ('a' + i).toChar,
          level = leagueWithLevel.level,
          league = leagueWithLevel.league)
        )
        Source(leagueUnits.toList)
      }}
  }
}
