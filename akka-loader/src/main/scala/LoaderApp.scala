import actors.TaskExecutorActor
import actors.TaskExecutorActor.{ScheduleTask, TryToExecute}
import akka.actor.{ActorSystem, Props}
import akka.stream.scaladsl.{Flow, Keep, Sink, Source}
import chpp.OauthTokens
import clickhouse.HattidLoaderClickhouseClient
import com.crobox.clickhouse.ClickhouseClient
import com.crobox.clickhouse.stream.{ClickhouseSink, Insert}
import com.typesafe.config.ConfigFactory
import utils.WorldDetailsSingleRequest

import java.util.Date
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

object LoaderApp extends  App {
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

  val hattidClient = new HattidLoaderClickhouseClient(config)

  val graph = FullLoaderFlow(config, countryMap).toMat(chSink)(Keep.both)


  val taskExecutorActor = actorSystem.actorOf(Props(new TaskExecutorActor(graph, chSink, hattidClient)))

  val taskScheduler = new TaskScheduler(worldDetails, taskExecutorActor)

  taskExecutorActor ! ScheduleTask(100, new Date())

  taskScheduler.schedule()

  actorSystem.scheduler.scheduleWithFixedDelay(5.second , 5.second)(() => taskExecutorActor ! TryToExecute)
}
