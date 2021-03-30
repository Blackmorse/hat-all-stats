package loadergraph.teams

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.scaladsl.{Flow, Source}
import chpp.OauthTokens
import chpp.leaguedetails.models.LeagueDetails
import models.stream.{LeagueUnit, StreamTeam}

import scala.concurrent.ExecutionContext

object TeamsFlow {
  def apply()(implicit oauthTokens: OauthTokens, system: ActorSystem,
                            executionContext: ExecutionContext): Flow[(LeagueDetails, LeagueUnit), StreamTeam, NotUsed] = {
    Flow[(LeagueDetails, LeagueUnit)]
      .flatMapConcat{case(leagueDetails, leagueUnit) =>
        Source(teamsFromLeagueUnit(leagueDetails, leagueUnit))
      }
      .async
  }

  private def teamsFromLeagueUnit(leagueDetails: LeagueDetails, leagueUnit: LeagueUnit): List[StreamTeam] = {
    if(leagueDetails.teams == null)
      List[StreamTeam]()
    else {
      leagueDetails.teams
        .map(team =>
          StreamTeam(leagueUnit = leagueUnit,
            userId = team.userId,
            id = team.teamId,
            name = team.teamName,
            position = team.position,
            points = team.points,
            diff = team.goalsFor - team.goalsAgainst,
            scored = team.goalsFor
          ))
        .toList
    }
  }
}
