package loadergraph.promotions

import akka.stream.scaladsl.{Flow, Keep, Sink}
import chpp.matchesarchive.models.MatchType
import models.stream.StreamTeam

import scala.concurrent.{ExecutionContext, Future}

object PromotionsSink {
  def apply(matchType: MatchType.Value)(implicit executionContext: ExecutionContext): Sink[StreamTeam, Future[List[StreamTeam]]] = {

    val flow = Flow[StreamTeam].filter(team => matchType == MatchType.LEAGUE_MATCH && team.leagueUnit.league.nextRound > 14)

    val sink = Sink.fold[List[StreamTeam], StreamTeam](List[StreamTeam]())((list, streamTeam) => streamTeam :: list)

    flow.toMat(sink)(Keep.right)
  }
}
