package loadergraph.teams

import org.apache.pekko.NotUsed
import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.stream.scaladsl.{Flow, Source}
import chpp.OauthTokens
import chpp.worlddetails.WorldDetailsRequest
import httpflows.WorldDetailsHttpFlow
import models.stream.League

import scala.concurrent.ExecutionContext

object LeagueWithLevelFlow {
  def apply()(implicit oauthTokens: OauthTokens, system: ActorSystem,
             executionContext: ExecutionContext): Flow[Int, LeagueWithLevel, NotUsed] = {
   Flow[Int]
      .map(leagueId => (WorldDetailsRequest(leagueId = Some(leagueId)), leagueId))
      .via(WorldDetailsHttpFlow())
      .map(_._1)
      .flatMapConcat(worldDetails => {
        val league = worldDetails.leagueList.head
        Source(
          (1 to worldDetails.leagueList.head.numberOfLevels)
            .map(level => LeagueWithLevel(league = League(leagueId = league.leagueId,
                seasonOffset = league.seasonOffset,
                nextRound = league.matchRound,
                season = league.season - league.seasonOffset,
                activeTeams = league.activeTeams),
              level = level))
        )})
  }
}
