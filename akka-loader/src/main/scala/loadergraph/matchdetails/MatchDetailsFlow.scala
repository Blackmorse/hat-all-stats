package loadergraph.matchdetails

import akka.actor.ActorSystem
import akka.stream.scaladsl.{Flow, Source}
import chpp.OauthTokens
import chpp.commonmodels.MatchType
import flows.{LogProgressFlow, MatchDetailsHttpFlow, MatchesArchiveHttpFlow}
import chpp.matchdetails.MatchDetailsRequest
import chpp.matchesarchive.models.MatchesArchive
import chpp.matchesarchive.MatchesArchiveRequest
import models.stream.{Match, StreamMatchDetails, StreamTeam}

import java.util.Date
import scala.concurrent.ExecutionContext

object MatchDetailsFlow {
  def apply(matchType: MatchType.Value)(implicit oauthTokens: OauthTokens, system: ActorSystem,
              executionContext: ExecutionContext): Flow[StreamTeam, StreamMatchDetails, _] = {
    Flow[StreamTeam]
      .filter(_.userId != 0)
      .map(team => (MatchesArchiveRequest(teamId = Some(team.id)), team))
      .async
      .via(MatchesArchiveHttpFlow())
      .map{case(matchesArchive, team) => lastMatch(matchesArchive, team, matchType)}
      .flatMapConcat(matchOpt => matchOpt.map(matc => Source.single(matc)).getOrElse(Source.empty[Match]))
      .map(matc => (MatchDetailsRequest(matchId = Some(matc.id)), matc))
      .async
      .via(MatchDetailsHttpFlow())
      .map{case(matchDetails, matc) => {
        StreamMatchDetails(matc = matc, matchDetails = matchDetails)
      }}
      .via(LogProgressFlow("Match Details", Some(_.matc.team.leagueUnit.league.activeTeams)))
  }

  private def lastMatch(matchesArchive: MatchesArchive, team: StreamTeam, matchType: MatchType.Value) = {
    val sevenDaysAgo = new Date(System.currentTimeMillis() - 1000L * 3600 * 24 * 7)
    Option(matchesArchive.team.matchList)
      .flatMap(matchList =>
        matchList.filter(_.matchType == matchType)
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
