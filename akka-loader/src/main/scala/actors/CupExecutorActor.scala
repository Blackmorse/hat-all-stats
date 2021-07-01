package actors

import akka.Done
import akka.stream.scaladsl.Sink
import chpp.OauthTokens
import chpp.matchesarchive.models.MatchType
import chpp.worlddetails.models.{League, WorldDetails}
import clickhouse.PlayerStatsClickhouseClient

import scala.concurrent.Future

object CupExecutorActor {
  type CupMat = Future[Done]
}

class CupExecutorActor[CupMat, Done](graph: Sink[Int, Future[Done]],
                                     playerStatsClickhouseClient: PlayerStatsClickhouseClient,
                                     worldDetails: WorldDetails
                                    ) (implicit oauthTokens: OauthTokens)
        extends TaskExecutorActor(graph, worldDetails, (m => m): Future[Done] => Future[Done]) {
  override def postProcessLoadedResults(league: League, matValue: Done): Future[_] = {
    playerStatsClickhouseClient.join(league, MatchType.CUP_MATCH)
  }

  override def notifyStarted(league: League): Unit = ()

  override def notifyFinished(league: League): Unit = ()
}
