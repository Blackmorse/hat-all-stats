package executors

import executors.LeagueExecutorActor.LeagueMat
import akka.Done
import akka.stream.scaladsl.Sink
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

import scala.concurrent.Future

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

  override def checkTaskAlreadyDone(league: League): Boolean = {
    hattidClickhouseClient.checkDataInMatchDetails(league, MatchType.LEAGUE_MATCH)
  }
}
