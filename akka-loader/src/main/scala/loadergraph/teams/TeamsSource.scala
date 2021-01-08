package loadergraph.teams

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.scaladsl.Source
import chpp.OauthTokens
import chpp.leaguedetails.models.LeagueDetails
import chpp.leaguedetails.{LeagueDetailsHttpFlow, LeagueDetailsRequest}
import models.stream.{LeagueUnit, StreamTeam}

import scala.concurrent.ExecutionContext

object TeamsSource {
  def apply(leagueId: Int)(implicit oauthTokens: OauthTokens, system: ActorSystem,
                            executionContext: ExecutionContext): Source[StreamTeam, NotUsed] = {
    LeagueUnitIdsSource(leagueId)
      .map(leagueUnit => (LeagueDetailsRequest(leagueUnitId = Some(leagueUnit.leagueUnitId)), leagueUnit))
      .async
      .via(LeagueDetailsHttpFlow())
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
        .filter(_.userId != 0)
        .map(team =>
          StreamTeam(leagueUnit = leagueUnit,
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
