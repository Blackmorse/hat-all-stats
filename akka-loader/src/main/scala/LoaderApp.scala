import actors.TaskExecutorActor.TryToExecute
import actors.{CupExecutorActor, LeagueExecutorActor}
import akka.actor.{ActorSystem, Props}
import akka.stream.scaladsl.{Flow, Keep}
import chpp.OauthTokens
import chpp.matchesarchive.models.MatchType
import chpp.worlddetails.models.League
import clickhouse.PlayerStatsClickhouseClient
import com.crobox.clickhouse.ClickhouseClient
import com.crobox.clickhouse.stream.{ClickhouseSink, Insert}
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.Logger
import org.slf4j.LoggerFactory
import utils.WorldDetailsSingleRequest

import scala.concurrent.Await
import scala.concurrent.duration._

object LoaderApp extends  App {
  private val logger = Logger(LoggerFactory.getLogger(this.getClass))

  implicit val actorSystem = ActorSystem("LoaderActorSystem")
  import actorSystem.dispatcher

  val config = ConfigFactory.load()

  val authToken = config.getString("tokens.authToken")
  val authCustomerKey = config.getString("tokens.authCustomerKey")
  val clientSecret = config.getString("tokens.clientSecret")
  val tokenSecret = config.getString("tokens.tokenSecret")

  val databaseName = config.getString("database_name")

  implicit val oauthTokens: OauthTokens = OauthTokens(authToken, authCustomerKey, clientSecret, tokenSecret)

  val worldDetailsFuture = WorldDetailsSingleRequest.request(leagueId = None)

  private val worldDetails = Await.result(worldDetailsFuture, 30.seconds)
  val countryMap = worldDetails.leagueList
    .view
    .map(league => league.country.map(country => (country.countryId, league.leagueId)))
    .filter(_.isDefined)
    .map(_.get)
    .toMap

  val client = new ClickhouseClient(Some(config))
  val chSink = Flow[Insert].log("pipeline_log").toMat(ClickhouseSink.insertSink(config, client))(Keep.right)

  val hattidClient = new PlayerStatsClickhouseClient(config)

  val (taskExecutorActor, matchType, dateTimeFunc) = if (args(1) == "league") {
    val graph = LeagueMatchesFlow(config, countryMap).toMat(chSink)(Keep.both)
    val taskExecutorActor = actorSystem.actorOf(Props(new LeagueExecutorActor(graph, chSink, hattidClient, worldDetails, config)))
    (taskExecutorActor, MatchType.LEAGUE_MATCH, (league: League) => league.seriesMatchDate)
  } else if (args(1) == "cup") {
    val graph = CupMatchesFlow(config, countryMap).toMat(chSink)(Keep.right)
    val taskExecutorActor = actorSystem.actorOf(Props(new CupExecutorActor(graph, hattidClient, worldDetails)))
    (taskExecutorActor, MatchType.CUP_MATCH, (league: League) => league.cupMatchDate)
  } else {
    throw new Exception(s"Unknown/unsupported ${args(1)} match type")
  }

  val taskScheduler = new TaskScheduler(worldDetails, taskExecutorActor, matchType)


  if (args(0) == "schedule") {
    taskScheduler.schedule(dateTimeFunc)
  } else if (args(0) == "scheduleFrom") {
    taskScheduler.scheduleFrom(args(2), dateTimeFunc)
  } else if (args(0) == "load") {
    taskScheduler.load(args(2))
  } else {
    logger.error("Please specify one of available tasks: schedule, scheduleFrom, load")
    throw new IllegalArgumentException(s"Unknown args: $args")
  }

  actorSystem.scheduler.scheduleWithFixedDelay(0.second , 5.second)(() => taskExecutorActor ! TryToExecute)
}
