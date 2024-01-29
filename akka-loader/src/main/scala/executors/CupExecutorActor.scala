package executors

import akka.stream.scaladsl.Sink
import chpp.OauthTokens
import chpp.commonmodels.MatchType
import chpp.worlddetails.models.{League, WorldDetails}
import clickhouse.PlayerStatsClickhouseClient

import scala.concurrent.Future

class CupExecutorActor[CupMat, Done](graph: Sink[Int, Future[Done]],
                                     playerStatsClickhouseClient: PlayerStatsClickhouseClient,
                                     worldDetails: WorldDetails
                                    ) (implicit oauthTokens: OauthTokens)
        extends TaskExecutorActor(graph, worldDetails, (m => m): Future[Done] => Future[Done]) {
  override def postProcessLoadedResults(league: League, matValue: Done): Future[_] = {
    playerStatsClickhouseClient.join(league, MatchType.CUP_MATCH)
  }

  override def notifyLeagueStarted(league: League): Unit = ()

  override def notifyLeagueFinished(league: League): Unit = ()

  override def notifyScheduled(tasks: List[TaskExecutorActor.ScheduleTask]): Unit = ()
}
