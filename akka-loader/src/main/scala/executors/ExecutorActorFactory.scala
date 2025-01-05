package executors

import org.apache.pekko.actor.{ActorRef, ActorSystem, Props}
import org.apache.pekko.stream.scaladsl.{Flow, Keep}
import alltid.AlltidClient
import chpp.OauthTokens
import chpp.worlddetails.models.WorldDetails
import clickhouse.HattidClickhouseClient
import com.crobox.clickhouse.ClickhouseClient
import com.crobox.clickhouse.internal.QuerySettings
import com.crobox.clickhouse.stream.{ClickhouseSink, Insert}
import com.typesafe.config.Config
import loadergraph.{CupMatchesFlow, LeagueMatchesFlow}
import telegram.LoaderTelegramClient

import javax.inject.{Inject, Singleton}

@Singleton
class ExecutorActorFactory @Inject()
    (implicit val actorSystem: ActorSystem,
     implicit val oauthTokens: OauthTokens,
     val clickhouseClient: ClickhouseClient,
     val config: Config,
     val hattidClient: HattidClickhouseClient,
     val alltidClient: AlltidClient,
     val telegramClient: LoaderTelegramClient) {
  import actorSystem.dispatcher

  private implicit val querySettings: QuerySettings = QuerySettings(authentication = Some((
    config.getString("crobox.clickhouse.client.authentication.user"),
    config.getString("crobox.clickhouse.client.authentication.password"))))

  private val chSink = Flow[Insert].log("pipeline_log").toMat(ClickhouseSink.insertSink(config, clickhouseClient))(Keep.right)

  def createLeagueExecutorActor(worldDetails: WorldDetails, lastMatchesWindow: Int): ActorRef = {
    val countryMap = getCountryMap(worldDetails)
    val graph = LeagueMatchesFlow.apply(config, countryMap, lastMatchesWindow).toMat(chSink)(Keep.both)
    actorSystem.actorOf(Props(new LeagueExecutorActor(graph, chSink, hattidClient, worldDetails, config, alltidClient, telegramClient)))
  }

  def createCupExecutorActor(worldDetails: WorldDetails, lastMatchesWindow: Int): ActorRef = {
    val countryMap = getCountryMap(worldDetails)
    val graph = CupMatchesFlow(config, countryMap, lastMatchesWindow).toMat(chSink)(Keep.right)
    actorSystem.actorOf(Props(new CupExecutorActor(graph, hattidClient, worldDetails, telegramClient)))
  }

  private def getCountryMap(worldDetails: WorldDetails): Map[Int, Int] = {
    worldDetails.leagueList
      .view
      .map(league => league.country.map(country => (country.countryId, league.leagueId)))
      .filter(_.isDefined)
      .map(_.get)
      .toMap
  }
}
