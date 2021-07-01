package actors

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.stream.scaladsl.{Flow, Keep}
import alltid.AlltidClient
import chpp.OauthTokens
import chpp.worlddetails.models.WorldDetails
import clickhouse.PlayerStatsClickhouseClient
import com.crobox.clickhouse.ClickhouseClient
import com.crobox.clickhouse.stream.{ClickhouseSink, Insert}
import com.typesafe.config.Config
import flows.{CupMatchesFlow, LeagueMatchesFlow}

import javax.inject.{Inject, Singleton}

@Singleton
class ExecutorActorFactory @Inject()
    (implicit val actorSystem: ActorSystem,
     implicit val oauthTokens: OauthTokens,
     val clickhouseClient: ClickhouseClient,
     val config: Config,
     val hattidClient: PlayerStatsClickhouseClient,
     val alltidClient: AlltidClient) {
  import actorSystem.dispatcher

  private val chSink = Flow[Insert].log("pipeline_log").toMat(ClickhouseSink.insertSink(config, clickhouseClient))(Keep.right)

  def createLeagueExecutorActor(worldDetails: WorldDetails): ActorRef = {
    val countryMap = getCountryMap(worldDetails)
    val graph = LeagueMatchesFlow.apply(config, countryMap).toMat(chSink)(Keep.both)
    actorSystem.actorOf(Props(new LeagueExecutorActor(graph, chSink, hattidClient, worldDetails, config, alltidClient)))
  }

  def createCupExecutorActor(worldDetails: WorldDetails): ActorRef = {
    val countryMap = getCountryMap(worldDetails)
    val graph = CupMatchesFlow(config, countryMap).toMat(chSink)(Keep.right)
    actorSystem.actorOf(Props(new CupExecutorActor(graph, hattidClient, worldDetails)))
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
