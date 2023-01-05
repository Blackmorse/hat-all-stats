package loadergraph.teams

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.FlowShape
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, Merge}
import chpp.OauthTokens
import chpp.leaguedetails.models.LeagueDetails
import chpp.leaguedetails.LeagueDetailsRequest
import httpflows.LeagueDetailsHttpFlow
import loadergraph.LogProgressFlow
import loadergraph.teams.sweden.SwedenLeagueUnitFlow
import models.stream.LeagueUnit

import scala.concurrent.ExecutionContext

object LeagueUnitDetailsFlow {

  def apply()(implicit oauthTokens: OauthTokens, system: ActorSystem,
                           executionContext: ExecutionContext): Flow[Int, (LeagueDetails, LeagueUnit), NotUsed] = {
    val leagueWithLevelFlow = LeagueWithLevelFlow()

    val flow = Flow.fromGraph {
      GraphDSL.create(){ implicit builder =>
        import GraphDSL.Implicits._

        val sourceShape = builder.add(leagueWithLevelFlow)

        val broadcast = builder.add(Broadcast[LeagueWithLevel](3))

        val filterHighest = builder.add(Flow[LeagueWithLevel]
          .filter(_.level == 1))
        val filterSweden = builder.add(Flow[LeagueWithLevel]
          .filter(leagueWithLevel => leagueWithLevel.level > 1 && leagueWithLevel.league.leagueId == 1))
        val filterStandard = builder.add(Flow[LeagueWithLevel]
          .filter(leagueWithLevel => leagueWithLevel.level > 1 && leagueWithLevel.league.leagueId != 1))

        val merge = builder.add(Merge[LeagueUnit](3))

        val highestLeagueFlow = builder.add(HighestLeagueFlow())
        val swedenLeagueFlow = builder.add(SwedenLeagueUnitFlow())
        val standardLeagueFlow = builder.add(StandardLeagueFlow())


        sourceShape ~> broadcast ~> filterHighest  ~> highestLeagueFlow  ~> merge
                       broadcast ~> filterSweden   ~> swedenLeagueFlow   ~> merge
                       broadcast ~> filterStandard ~> standardLeagueFlow ~> merge

        FlowShape(sourceShape.in, merge.out)
      }
    }

    val loggingFlow: Flow[Int, LeagueUnit, NotUsed] = flow.async.via(LogProgressFlow("league units", None))
    loggingFlow
      .map((leagueUnit: LeagueUnit) => (LeagueDetailsRequest(leagueUnitId = Some(leagueUnit.leagueUnitId)), leagueUnit))
      .async
      .via(LeagueDetailsHttpFlow())
  }
}
