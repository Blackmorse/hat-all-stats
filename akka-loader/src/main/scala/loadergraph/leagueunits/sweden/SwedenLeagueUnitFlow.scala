package loadergraph.leagueunits.sweden

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.FlowShape
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, Merge}
import chpp.OauthTokens
import loadergraph.leagueunits.LeagueWithLevel
import models.stream.LeagueUnit

import scala.concurrent.ExecutionContext

object SwedenLeagueUnitFlow {
  def apply()(implicit oauthTokens: OauthTokens, system: ActorSystem,
              executionContext: ExecutionContext): Flow[LeagueWithLevel, LeagueUnit, NotUsed] = {
    Flow.fromGraph(
      GraphDSL.create() { implicit builder =>
        import GraphDSL.Implicits._

        val broadcast = builder.add(Broadcast[LeagueWithLevel](3))

        val Ifilter = builder.add(Flow[LeagueWithLevel].filter(_.level == 2))
        val IIfilter = builder.add(Flow[LeagueWithLevel].filter(_.level == 3))
        val othersFilter = builder.add(Flow[LeagueWithLevel].filter(_.level >= 4))

        val Iflow = builder.add(SwedenIorIIFlow(2))
        val IIflow = builder.add(SwedenIorIIFlow(3))
        val otherFlow = builder.add(SwedenIIIorGreaterFlow())

        val merge = builder.add(Merge[LeagueUnit](3))

        broadcast ~> Ifilter      ~> Iflow     ~> merge
        broadcast ~> IIfilter     ~> IIflow    ~> merge
        broadcast ~> othersFilter ~> otherFlow ~> merge

        FlowShape(broadcast.in, merge.out)
      }
    )
  }
}
