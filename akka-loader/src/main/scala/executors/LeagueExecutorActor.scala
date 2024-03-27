package executors

import executors.LeagueExecutorActor.LeagueMat
import org.apache.pekko.Done
import org.apache.pekko.stream.scaladsl.Sink
import alltid.AlltidClient
import chpp.OauthTokens
import chpp.commonmodels.MatchType
import chpp.worlddetails.models.{League, WorldDetails}
import clickhouse.{ClickhouseWriter, HattidClickhouseClient, TeamRankJoiner}
import com.crobox.clickhouse.stream.Insert
import com.typesafe.config.Config
import models.stream.StreamTeam
import promotions.PromotionsCalculator
import telegram.LoaderTelegramClient

import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success, Try}

object LeagueExecutorActor {
  type LeagueMat = (Future[List[StreamTeam]], Future[Done])
}

class LeagueExecutorActor
(graph: Sink[Int, (Future[List[StreamTeam]], Future[Done])],
 chSink: Sink[Insert, Future[Done]],
 hattidClickhouseClient: HattidClickhouseClient,
 worldDetails: WorldDetails,
 config: Config,
 alltidClient: AlltidClient,
 telegramClient: LoaderTelegramClient)(implicit oauthTokens: OauthTokens)
  extends TaskExecutorActor[LeagueMat, (List[StreamTeam], Done)](graph, worldDetails, lm => lm._1.zip(lm._2), telegramClient) {

  import context.{dispatcher, system}

  override def postProcessLoadedResults(league: League, matValue: (List[StreamTeam], Done)): Future[_] = {
    for {
      _ <- hattidClickhouseClient.join(league, MatchType.LEAGUE_MATCH)
      _ <- TeamRankJoiner.joinTeamRankings(config, league)
      promotions <- PromotionsCalculator.calculatePromotions(league, matValue._1)
      finalFuture <- ClickhouseWriter.writeToCh(promotions, chSink, context.system.settings)
    } yield {
      finalFuture
    }
  }

  override def notifyLeagueStarted(league: League): Unit = {
    alltidClient.notifyCountryLoadingStarted(league)
  }

  override def notifyLeagueFinished(league: League): Unit = {
    alltidClient.notifyCountryLoadingFinished(league)
  }

  override def notifyScheduled(tasks: List[TaskExecutorActor.ScheduleTask]): Unit = {
    alltidClient.notifyScheduleInfo(tasks)
  }

  override def checkTaskAlreadyDoneAndTryToFix(league: League): Boolean = {
    if(hattidClickhouseClient.checkUploaded(league, MatchType.LEAGUE_MATCH)) {
      return true
    } else {
      val someDataWasUploaded = !hattidClickhouseClient.checkInMatchDetailsAndPlayerStatsAreEmpty(league, MatchType.LEAGUE_MATCH)
      if (someDataWasUploaded) {
        // Something wrong. Should be fixed
        val future = hattidClickhouseClient.tryToFixLeagueData(league)

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
    hattidClickhouseClient.logUploadEntry(league, MatchType.LEAGUE_MATCH)


  override def preCleanupTables(league: League): Unit = {

    val f = for {
      _ <- hattidClickhouseClient.truncateTable("player_info", league, MatchType.LEAGUE_MATCH)
      r <- hattidClickhouseClient.truncateTable("player_events", league, MatchType.LEAGUE_MATCH)
    } yield r
    Await.result(f, 30.second)
  }
}
