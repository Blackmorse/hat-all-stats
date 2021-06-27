package flows

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.FlowShape
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, Merge}
import chpp.OauthTokens
import chpp.matchesarchive.models.MatchType
import com.crobox.clickhouse.stream.Insert
import com.typesafe.config.Config
import loadergraph.matchdetails.{MatchDetailsCHModelFlow, MatchDetailsFlow}
import loadergraph.playerevents.PlayerEventsFlow
import loadergraph.playerinfos.PlayerInfoFlow
import loadergraph.teams.{LeagueUnitDetailsFlow, TeamsFlow}
import models.stream.StreamMatchDetails

import scala.concurrent.ExecutionContext

object CupMatchesFlow {
  def apply(config: Config, countryMap: Map[Int, Int])(implicit oauthTokens: OauthTokens, system: ActorSystem,
                                                       executionContext: ExecutionContext): Flow[Int, Insert, NotUsed] = {
    val databaseName = config.getString("database_name")

    Flow.fromGraph(
      GraphDSL.create() { implicit builder =>
        import GraphDSL.Implicits._

        val teamFlow = builder.add(TeamsFlow())
        val leagueUnitDetailsFlow = builder.add(LeagueUnitDetailsFlow())
        val matchDetailsFlow = builder.add(MatchDetailsFlow(MatchType.CUP_MATCH))

        val broadcast = builder.add(Broadcast[StreamMatchDetails](3).async)

        val matchDetailsCHModelFlow = builder.add(MatchDetailsCHModelFlow(databaseName))
        val playerEventsFlow = builder.add(PlayerEventsFlow(databaseName))
        val playerInfosFlow = builder.add(PlayerInfoFlow(databaseName, countryMap))

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
