package loadergraph.matchdetails

import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.stream.scaladsl.{Flow, Source}
import chpp.OauthTokens
import chpp.commonmodels.MatchType
import chpp.matchdetails.MatchDetailsRequest
import chpp.matches.MatchesRequest
import chpp.matches.models.Matches
import httpflows.{MatchDetailsHttpFlow, MatchesHttpFlow}
import loadergraph.LogProgressFlow
import loadergraph.matchlineup.{MatchLineupFlow, StreamMatchDetailsWithLineup}
import models.stream.{Match, StreamMatchDetails, StreamTeam}

import java.util.Date
import scala.concurrent.ExecutionContext

object MatchDetailsFlow {
  def apply(matchType: MatchType.Value, lastMatchesWindow: Int)(implicit oauthTokens: OauthTokens, system: ActorSystem,
              executionContext: ExecutionContext): Flow[StreamTeam, StreamMatchDetailsWithLineup, _] = {
    Flow[StreamTeam]
      .filter(_.userId != 0)
      .map(team => (MatchesRequest(teamId = Some(team.id)), team))
      .async
      .via(MatchesHttpFlow())
      .map{case (matchesArchive, team) => lastMatch(matchesArchive, team, matchType, lastMatchesWindow)}
      .flatMapConcat(matchOpt => matchOpt.map(matc => Source.single(matc)).getOrElse(Source.empty[Match]))
      .map(matc => (MatchDetailsRequest(matchId = Some(matc.id)), matc))
      .async
      .via(MatchDetailsHttpFlow())
      .map{case (matchDetails, matc) => {
        StreamMatchDetails(matc = matc, matchDetails = matchDetails)
      }}
      .via(MatchLineupFlow())
      .via(LogProgressFlow("Match Details", Some(_.matc.team.leagueUnit.league.activeTeams)))
  }

  private def lastMatch(matchesArchive: Matches, team: StreamTeam, matchType: MatchType.Value, lastMatchesWindow: Int): Option[Match] = {
    val sevenDaysAgo = new Date(System.currentTimeMillis() - 1000L * 3600 * 24 * lastMatchesWindow) // 7 days by default
    Option(matchesArchive.team.matchList)
      .flatMap(matchList =>
        matchList.filter(_.matchType == matchType)
          .filter(_.status == "FINISHED")
          .filter(matc => matc.matchDate.after(sevenDaysAgo))
          .sortBy(_.matchDate).reverse
          .headOption
          .map(matc =>
            Match(id = matc.matchId,
              round = if(matchType == MatchType.LEAGUE_MATCH) team.leagueUnit.league.nextRound - 1
                else if (matchType == MatchType.CUP_MATCH) team.leagueUnit.league.nextRound //cup is playing before league match
                else throw new IllegalArgumentException(matchType.toString),
              date = matc.matchDate,
              season = team.leagueUnit.league.season,
              team = team))
      )
  }
}
