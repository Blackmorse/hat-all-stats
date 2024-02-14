package executors

import akka.stream.scaladsl.Sink
import chpp.OauthTokens
import chpp.commonmodels.MatchType
import chpp.worlddetails.models.{League, WorldDetails}
import clickhouse.HattidClickhouseClient
import telegram.LoaderTelegramClient

import scala.concurrent.Future

class CupExecutorActor[CupMat, Done](graph: Sink[Int, Future[Done]],
                                     hattidClickhouseClient: HattidClickhouseClient,
                                     worldDetails: WorldDetails,
                                     telegramClient: LoaderTelegramClient
                                    ) (implicit oauthTokens: OauthTokens)
        extends TaskExecutorActor(graph, worldDetails, (m => m): Future[Done] => Future[Done], telegramClient) {
  override def postProcessLoadedResults(league: League, matValue: Done): Future[_] = {
    hattidClickhouseClient.join(league, MatchType.CUP_MATCH)
  }

  override def notifyLeagueStarted(league: League): Unit = ()

  override def notifyLeagueFinished(league: League): Unit = ()

  override def notifyScheduled(tasks: List[TaskExecutorActor.ScheduleTask]): Unit = ()

  override def checkTaskAlreadyDone(league: League): Boolean = {
    hattidClickhouseClient.checkDataInMatchDetails(league, MatchType.CUP_MATCH)
  }
}
