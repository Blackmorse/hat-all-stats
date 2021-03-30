package loadergraph.promotions

import akka.stream.scaladsl.{Flow, Keep, Sink}
import models.stream.StreamTeam

import scala.concurrent.{ExecutionContext, Future}

object PromotionsSink {
  def apply()(implicit executionContext: ExecutionContext): Sink[StreamTeam, Future[List[StreamTeam]]] = {

    val flow = Flow[StreamTeam].filter(team => team.leagueUnit.league.nextRound > 14)

    val sink = Sink.fold[List[StreamTeam], StreamTeam](List[StreamTeam]())((list, streamTeam) => streamTeam :: list)

    flow.toMat(sink)(Keep.right)
  }
}
