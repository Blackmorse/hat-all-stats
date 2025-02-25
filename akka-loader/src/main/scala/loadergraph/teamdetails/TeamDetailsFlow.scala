package loadergraph.teamdetails

import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.stream.scaladsl.Flow
import chpp.OauthTokens
import chpp.teamdetails.TeamDetailsRequest
import com.crobox.clickhouse.stream.Insert
import httpflows.TeamDetailsHttpFlow
import loadergraph.matchlineup.StreamMatchDetailsWithLineup
import loadergraph.{ClickhouseFlow, LogProgressFlow}
import models.clickhouse.TeamDetailsModelCH
import models.stream.StreamMatchDetails

import scala.concurrent.ExecutionContext

object TeamDetailsFlow {
  def apply(databaseName: String)(implicit oauthTokens: OauthTokens, system: ActorSystem,
              executionContext: ExecutionContext): Flow[StreamMatchDetailsWithLineup, Insert, _] = {
    Flow[StreamMatchDetailsWithLineup]
      .map(matchDetails => (TeamDetailsRequest(teamId = Some(matchDetails.matc.team.id), includeFlags = Some(true), includeDomesticFlags = Some(true)), matchDetails))
      .async
      .via(TeamDetailsHttpFlow())
      .via(LogProgressFlow("Team Details", Some(_._2.matc.team.leagueUnit.league.activeTeams)))
      .map{case(teamDetails, matchDetails) =>
        val teams = teamDetails.teams
          .filter(t => t.teamId == matchDetails.matc.team.id)

        val team = teams
          .head
        TeamDetailsModelCH.convert(team, matchDetails)
      }
      .via(ClickhouseFlow[TeamDetailsModelCH](databaseName, "team_details"))
  }
}
