import actors.ExecutorActorFactory
import actors.TaskExecutorActor.TryToExecute
import akka.actor.ActorSystem
import chpp.OauthTokens
import chpp.matchesarchive.models.MatchType
import chpp.worlddetails.models.League
import com.google.inject.Guice
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.Logger
import guice.LoaderModule
import org.slf4j.LoggerFactory
import utils.WorldDetailsSingleRequest

import scala.concurrent.Await
import scala.concurrent.duration._

object LoaderApp extends  App {
  private val logger = Logger(LoggerFactory.getLogger(this.getClass))

  implicit val actorSystem = ActorSystem("LoaderActorSystem")
  import actorSystem.dispatcher

  val config = ConfigFactory.load()

  val injector = Guice.createInjector(new LoaderModule(config, actorSystem))

  implicit val oauthTokens = injector.getInstance(classOf[OauthTokens])

  val executorActorFactory: ExecutorActorFactory = injector
      .getInstance(classOf[ExecutorActorFactory])

  val worldDetailsFuture = WorldDetailsSingleRequest.request(leagueId = None)

  private val worldDetails = Await.result(worldDetailsFuture, 30.seconds)
  val countryMap = worldDetails.leagueList
    .view
    .map(league => league.country.map(country => (country.countryId, league.leagueId)))
    .filter(_.isDefined)
    .map(_.get)
    .toMap

  val (taskExecutorActor, matchType, dateTimeFunc) = if (args(1) == "league") {
    val taskExecutorActor = executorActorFactory.createLeagueExecutorActor(worldDetails)
    (taskExecutorActor, MatchType.LEAGUE_MATCH, (league: League) => league.seriesMatchDate)
  } else if (args(1) == "cup") {
    val taskExecutorActor = executorActorFactory.createCupExecutorActor(worldDetails)
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
