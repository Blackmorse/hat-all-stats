package loadergraph.matchdetails

import akka.actor.ActorSystem
import akka.stream.scaladsl.{Flow, Source}
import chpp.OauthTokens
import chpp.leaguedetails.models.LeagueDetails
import flows.LogProgressFlow
import chpp.leaguedetails.{LeagueDetailsHttpFlow, LeagueDetailsRequest}
import chpp.matchdetails.{MatchDetailsHttpFlow, MatchDetailsRequest}
import chpp.matchesarchive.models.{MatchType, MatchesArchive}
import chpp.matchesarchive.{MatchesArchiveHttpFlow, MatchesArchiveRequest}
import models.stream.{LeagueUnit, Match, StreamMatchDetails, StreamTeam}

import scala.concurrent.ExecutionContext

object MatchDetailsFlow {
  def apply()(implicit oauthTokens: OauthTokens, system: ActorSystem,
              executionContext: ExecutionContext): Flow[StreamTeam, StreamMatchDetails, _] = {
    Flow[StreamTeam]
      .filter(_.userId != 0)
      .map(team => (MatchesArchiveRequest(teamId = Some(team.id)), team))
      .async
      .via(MatchesArchiveHttpFlow())
      .map{case(matchesArchive, team) => lastMatch(matchesArchive, team)}
      .flatMapConcat(matchOpt => matchOpt.map(matc => Source.single(matc)).getOrElse(Source.empty[Match]))
      .map(matc => (MatchDetailsRequest(matchId = Some(matc.id)), matc))
      .async
      .via(MatchDetailsHttpFlow())
      .map{case(matchDetails, matc) => {
        StreamMatchDetails(matc = matc, matchDetails = matchDetails)
      }}
      .via(LogProgressFlow("Match Details", Some(_.matc.team.leagueUnit.league.activeTeams)))
  }

  private def lastMatch(matchesArchive: MatchesArchive, team: StreamTeam) = {
    Option(matchesArchive.team.matchList)
      .flatMap(matchList =>
        matchList.filter(_.matchType == MatchType.LEAGUE_MATCH)
          .sortBy(_.matchDate).reverse
          .headOption
          .map(matc =>
            Match(id = matc.matchId,
              round = team.leagueUnit.league.nextRound - 1,
              date = matc.matchDate,
              season = team.leagueUnit.league.season,
              team = team))
      )
  }
}
