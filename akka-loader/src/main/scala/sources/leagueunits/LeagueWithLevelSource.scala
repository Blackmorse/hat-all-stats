package sources.leagueunits

import akka.actor.ActorSystem
import akka.stream.scaladsl.Source
import flows.http.WorldDetailsFlow
import models.OauthTokens
import models.stream.League
import requests.WorldDetailsRequest

import scala.concurrent.ExecutionContext

object LeagueWithLevelSource {
  def apply(leagueId: Int)
            (implicit oauthTokens: OauthTokens, system: ActorSystem,
             executionContext: ExecutionContext) = {
    Source.single(leagueId)
      .map(leagueId => (WorldDetailsRequest(leagueId = Some(leagueId)), leagueId))
      .via(WorldDetailsFlow())
      .map(_._1)
      .flatMapConcat(worldDetails => {
        val league = worldDetails.leagueList.head
        Source(
          (1 to worldDetails.leagueList.head.numberOfLevels)
            .map(level => LeagueWithLevel(league = League(leagueId = league.leagueId,
                seasonOffset = league.seasonOffset,
                nextRound = league.matchRound),
              level = level))
        )})
  }
}
