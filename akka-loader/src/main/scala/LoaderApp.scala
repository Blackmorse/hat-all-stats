import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import chpp.{ChppRequestExecutor, OauthTokens}
import chpp.playerdetails.PlayerDetailsRequest
import clickhouse.TeamRankJoiner
import com.google.inject.Guice
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.Logger
import executors.ExecutorActorFactory
import executors.TaskExecutorActor.TryToExecute
import guice.LoaderModule
import org.slf4j.LoggerFactory
import scheduler.{CupScheduler, LeagueScheduler}
import utils.WorldDetailsSingleRequest

import scala.concurrent.Await
import scala.concurrent.duration._

object LoaderApp extends  App {
  private val logger = Logger(LoggerFactory.getLogger(this.getClass))

  implicit val actorSystem: ActorSystem = ActorSystem("LoaderActorSystem")
  import actorSystem.dispatcher

  val config = ConfigFactory.load()

  val injector = Guice.createInjector(new LoaderModule(config, actorSystem))

  implicit val oauthTokens: OauthTokens = injector.getInstance(classOf[OauthTokens])

  val executorActorFactory: ExecutorActorFactory = injector
      .getInstance(classOf[ExecutorActorFactory])

  val worldDetailsFuture = WorldDetailsSingleRequest.request(leagueId = None)

  private val worldDetails = Await.result(worldDetailsFuture, 30.seconds)

  if (args(0) == "teamRankings") {
    if (args.length == 2) {
      Await.result(TeamRankJoiner.joinTeamRankings(config, worldDetails.leagueList.find(_.leagueName == args(1)).get), 3.minute)
    } else if (args.length == 1) {
      worldDetails.leagueList.foreach(league => {
        Await.result(TeamRankJoiner.joinTeamRankings(config, league), 3.minute)
      })
    }
    System.exit(0)
  }

  val (taskExecutorActor, scheduler) = if (args(1) == "league") {
    val taskExecutorActor = executorActorFactory.createLeagueExecutorActor(worldDetails)
    (taskExecutorActor, new LeagueScheduler(worldDetails, taskExecutorActor))
  } else if (args(1) == "cup") {
    val taskExecutorActor = executorActorFactory.createCupExecutorActor(worldDetails)
    (taskExecutorActor, new CupScheduler(worldDetails, taskExecutorActor))
  } else {
    throw new Exception(s"Unknown/unsupported ${args(1)} match type")
  }

  if (args(0) == "schedule") {
    scheduler.schedule()
  } else if (args(0) == "scheduleFrom") {
    scheduler.scheduleFrom(args(2))
  } else if (args(0) == "load") {
    scheduler.load(args(2))
  } else {
    logger.error("Please specify one of available tasks: schedule, scheduleFrom, load")
    throw new IllegalArgumentException(s"Unknown args: ${args.mkString("Array(", ", ", ")")}")
  }

  actorSystem.scheduler.scheduleWithFixedDelay(0.second , 5.second)(() => taskExecutorActor ! TryToExecute)
}
