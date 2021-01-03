package sources.leagueunits

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.SourceShape
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, Merge, Source}
import models.OauthTokens
import models.stream.LeagueUnit

import scala.concurrent.ExecutionContext

object LeagueUnitIdsSource {
  def apply(leagueId: Int)(implicit oauthTokens: OauthTokens, system: ActorSystem,
                           executionContext: ExecutionContext): Source[LeagueUnit, NotUsed] = {
    val source = LeagueWithLevelSource(leagueId)

    Source.fromGraph {
      GraphDSL.create(){ implicit builder =>
        import GraphDSL.Implicits._

        val sourceShape = builder.add(source)

        val broadcast = builder.add(Broadcast[LeagueWithLevel](2))
        val filterHighest = builder.add(Flow[LeagueWithLevel].filter(_.level == 1))
        val filterNotHighest = builder.add(Flow[LeagueWithLevel].filter(_.level > 1))

        val merge = builder.add(Merge[LeagueUnit](2))

        val highestLeagueFlow = builder.add(HighestLeagueFlow())
        val standartLeagueFlow = builder.add(StandardLeagueFlow())

        sourceShape ~> broadcast ~> filterHighest ~> highestLeagueFlow ~> merge
                       broadcast ~> filterNotHighest ~> standartLeagueFlow ~> merge

        SourceShape(merge.out)
      }
    }
  }
}
