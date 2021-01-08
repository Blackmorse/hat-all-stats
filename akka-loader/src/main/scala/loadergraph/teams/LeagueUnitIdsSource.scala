package loadergraph.teams

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.SourceShape
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, Merge, Sink, Source}
import chpp.OauthTokens
import flows.LogProgressFlow
import loadergraph.teams.sweden.SwedenLeagueUnitFlow
import models.stream.LeagueUnit

import scala.concurrent.ExecutionContext

object LeagueUnitIdsSource {

  def apply(leagueId: Int)(implicit oauthTokens: OauthTokens, system: ActorSystem,
                           executionContext: ExecutionContext): Source[LeagueUnit, NotUsed] = {
    val source = LeagueWithLevelSource(leagueId)

    val flow = Source.fromGraph {
      GraphDSL.create(){ implicit builder =>
        import GraphDSL.Implicits._

        val sourceShape = builder.add(source)

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
                       broadcast ~> filterStandard ~> standardLeagueFlow ~> merge// ~> broadcastCounter.in

        SourceShape(merge.out)
      }
    }

    flow.via(LogProgressFlow("league units")).async
  }
}
