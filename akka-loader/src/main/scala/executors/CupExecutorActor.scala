package executors

import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.stream.scaladsl.Sink
import chpp.OauthTokens
import chpp.commonmodels.MatchType
import chpp.worlddetails.models.{League, WorldDetails}
import clickhouse.HattidClickhouseClient
import telegram.LoaderTelegramClient

import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

class CupExecutorActor[CupMat, Done](graph: Sink[Int, Future[Done]],
                                     hattidClickhouseClient: HattidClickhouseClient,
                                     worldDetails: WorldDetails,
                                     telegramClient: LoaderTelegramClient
                                    ) (implicit oauthTokens: OauthTokens)
        extends TaskExecutorActor(graph, worldDetails, (m => m): Future[Done] => Future[Done], telegramClient) {

  implicit val actorSystem: ExecutionContext = context.system.dispatcher

  def postProcessLoadedResults(league: League, matValue: Done): Future[_] = {
    hattidClickhouseClient.join(league, MatchType.CUP_MATCH)
  }

  override def notifyLeagueStarted(league: League): Unit = ()

  override def notifyLeagueFinished(league: League): Unit = ()

  override def notifyScheduled(tasks: List[TaskExecutorActor.ScheduleTask]): Unit = ()


  override def checkTaskAlreadyDoneAndTryToFix(league: League): Boolean = {
    if(hattidClickhouseClient.checkUploaded(league, MatchType.CUP_MATCH)) {
      return true
    } else {
      val someDataWasUploaded = !hattidClickhouseClient.checkInMatchDetailsAndPlayerStatsAreEmpty(league, MatchType.CUP_MATCH)
      if (someDataWasUploaded) {
        // Something wrong. Should be fixed
        val future = hattidClickhouseClient.tryToFixCupData(league)

        Try { Await.result(future, 3.minutes) } match {
          case Failure(exception) =>
            // Smth wrong with the data that can't be fixed. Report and skip the task
            telegramClient.sendException(s"Corrupted uploaded data can't be fixed. League: ${league.leagueId} ${league.englishName}", exception)
            return true
          case Success(_) => return false
        }
      } else {
        return false
      }
    }
  }

  override def logTaskFinished(league: League): Future[_] =
    hattidClickhouseClient.logUploadEntry(league, MatchType.CUP_MATCH)

  override def preCleanupTables(league: League): Unit = {

    val f = for {
      _ <- hattidClickhouseClient.truncateTable("player_info", league, MatchType.CUP_MATCH)
      r <- hattidClickhouseClient.truncateTable("player_events", league, MatchType.CUP_MATCH)
    } yield r
    Await.result(f, 30.second)
  }
}
