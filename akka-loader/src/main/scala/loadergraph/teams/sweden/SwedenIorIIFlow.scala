package loadergraph.teams.sweden

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.scaladsl.{Flow, Source}
import chpp.OauthTokens
import com.blackmorse.hattrick.common.CommonData
import loadergraph.teams.LeagueWithLevel
import chpp.search.SearchRequest
import chpp.search.models.{SearchHttpFlow, SearchType}
import models.stream.LeagueUnit

import scala.concurrent.ExecutionContext

object SwedenIorIIFlow {
  def apply(level: Int)(implicit oauthTokens: OauthTokens, system: ActorSystem,
              executionContext: ExecutionContext): Flow[LeagueWithLevel, LeagueUnit, NotUsed] = {
    if(level != 2 && level != 3) throw new RuntimeException()

    val romanLevel = CommonData.arabToRomans.get(level - 1)
    val leagueNumber = CommonData.leagueLevelNumberTeams.get(level)

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
