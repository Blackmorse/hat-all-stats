package loadergraph

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.FlowShape
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, Merge}
import chpp.OauthTokens
import chpp.commonmodels.MatchType
import chpp.leaguedetails.models.LeagueDetails
import com.crobox.clickhouse.stream.Insert
import com.typesafe.config.Config
import loadergraph.matchdetails.{MatchDetailsCHModelFlow, MatchDetailsFlow}
import loadergraph.matchlineup.StreamMatchDetailsWithLineup
import loadergraph.playerevents.PlayerEventsFlow
import loadergraph.playerinfos.PlayerInfoFlow
import loadergraph.teams.{LeagueUnitDetailsFlow, TeamsFlow}
import models.stream.{LeagueUnit, StreamMatchDetails, StreamTeam}

import scala.concurrent.ExecutionContext

object CupMatchesFlow {
  def apply(config: Config, countryMap: Map[Int, Int], lastMatchesWindow: Int)(implicit oauthTokens: OauthTokens, system: ActorSystem,
                                                       executionContext: ExecutionContext): Flow[Int, Insert, NotUsed] = {
    val databaseName = config.getString("database_name")

    Flow.fromGraph(
      GraphDSL.create() { implicit builder =>
        import GraphDSL.Implicits._

        val teamFlow: FlowShape[(LeagueDetails, LeagueUnit), StreamTeam] = builder.add(TeamsFlow())
        val leagueUnitDetailsFlow: FlowShape[Int, (LeagueDetails, LeagueUnit)] = builder.add(LeagueUnitDetailsFlow())
        val matchDetailsFlow: FlowShape[StreamTeam, StreamMatchDetailsWithLineup] = builder.add(MatchDetailsFlow(MatchType.CUP_MATCH, lastMatchesWindow))

        val broadcast = builder.add(Broadcast[StreamMatchDetailsWithLineup](3).async)

        val matchDetailsCHModelFlow: FlowShape[StreamMatchDetailsWithLineup, Insert] = builder.add(MatchDetailsCHModelFlow(databaseName))
        val playerEventsFlow: FlowShape[StreamMatchDetailsWithLineup, Insert] = builder.add(PlayerEventsFlow(databaseName))
        val playerInfosFlow: FlowShape[StreamMatchDetailsWithLineup, Insert] = builder.add(PlayerInfoFlow(databaseName, countryMap))

        val merge = builder.add(Merge[Insert](3))

        leagueUnitDetailsFlow ~> teamFlow ~> matchDetailsFlow ~> broadcast

        broadcast ~> matchDetailsCHModelFlow ~> merge
        broadcast ~> playerEventsFlow ~> merge
        broadcast ~> playerInfosFlow ~> merge
        FlowShape(leagueUnitDetailsFlow.in, merge.out)
      }
    )
  }
}
