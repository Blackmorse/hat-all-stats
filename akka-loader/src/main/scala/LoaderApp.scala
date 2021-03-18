import akka.actor.ActorSystem
import akka.stream.RestartSettings
import akka.stream.scaladsl.{Flow, Keep, RestartSource, Source}
import chpp.OauthTokens
import clickhouse.HattidLoaderClickhouseClient
import com.crobox.clickhouse.ClickhouseClient
import com.crobox.clickhouse.stream.{ClickhouseSink, Insert}
import com.typesafe.config.ConfigFactory
import utils.WorldDetailsSingleRequest

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.{Failure, Success}

object LoaderApp extends  App {
  implicit val actorSystem = ActorSystem("LoaderActorSystem")
  import actorSystem.dispatcher
//  implicit val executionContext = actorSystem.dispatchers.lookup("my-dispatcher")

  val config = ConfigFactory.load()

  val authToken = config.getString("tokens.authToken")
  val authCustomerKey = config.getString("tokens.authCustomerKey")
  val clientSecret = config.getString("tokens.clientSecret")
  val tokenSecret = config.getString("tokens.tokenSecret")

  val databaseName = config.getString("database_name")

  implicit val oauthTokens: OauthTokens = OauthTokens(authToken, authCustomerKey, clientSecret, tokenSecret)

  val worldDetails = WorldDetailsSingleRequest.request(leagueId = None)

  val countryMap = Await.result(worldDetails, 30 seconds).leagueList
    .view
    .map(league => league.country.map(country => (country.countryId, league.leagueId)))
    .filter(_.isDefined)
    .map(_.get)
    .toMap


  val settings = RestartSettings(
    minBackoff = 1 minute,
    maxBackoff = 10 minutes,
    randomFactor = 0
  ).withMaxRestarts(10, 10 minutes)


  val client = new ClickhouseClient(Some(config))
  val chSink = Flow[Insert].log("pipeline_log").toMat(ClickhouseSink.insertSink(config, client))(Keep.right)

  private val leagueIdNumber = 99

  val hattidClient = new HattidLoaderClickhouseClient(config)

  val graph = FullLoaderFlow(config, countryMap)

  actorSystem.scheduler.scheduleOnce(0.second)(RestartSource.onFailuresWithBackoff(settings)(() => {
    Source.single(leagueIdNumber).via(graph)
  }).toMat(chSink)(Keep.right).run().onComplete{
    case Failure(exception) =>
      println("Due  to some error : " + exception)
      throw new Exception(exception)
    case Success(_) =>
      println("SUCCESS!")
      hattidClient.join(leagueIdNumber)
    }
  )

}
