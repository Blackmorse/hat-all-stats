package loadergraph.matchlineup

import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.stream.scaladsl.Flow
import chpp.OauthTokens
import chpp.matchlineup.MatchLineupRequest
import httpflows.MatchLineupHttpFlow
import loadergraph.LogProgressFlow
import models.stream.StreamMatchDetails

import scala.concurrent.ExecutionContext

object MatchLineupFlow {
  def apply()(implicit oauthTokens: OauthTokens, system: ActorSystem,
              executionContext: ExecutionContext): Flow[StreamMatchDetails, StreamMatchDetailsWithLineup, _] = {
    Flow[StreamMatchDetails]
      .map(streamMatchDetails => (MatchLineupRequest(matchId = Some(streamMatchDetails.matc.id), teamId = Some(streamMatchDetails.matc.team.id)), streamMatchDetails))
      .via(MatchLineupHttpFlow())
      .map{case(matchLineup, streamMatchDetails) =>
        StreamMatchDetailsWithLineup(
          matc = streamMatchDetails.matc,
          matchDetails = streamMatchDetails.matchDetails,
          matchLineup = matchLineup
        )
      }.via(LogProgressFlow("Match Lineup", Some(_.matc.team.leagueUnit.league.activeTeams)))
  }
}
