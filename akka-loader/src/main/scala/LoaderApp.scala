import org.apache.pekko.actor.{ActorRef, ActorSystem}
import chpp.OauthTokens
import chpp.worlddetails.models.WorldDetails
import cli.{CommandLine, LoadConfig, LoadScheduledConfig, ScheduleConfig, TeamRankingsConfig}
import clickhouse.TeamRankJoiner
import com.google.inject.Guice
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.Logger
import executors.ExecutorActorFactory
import executors.TaskExecutorActor.TryToExecute
import guice.LoaderModule
import org.slf4j.LoggerFactory
import scheduler.{AbstractScheduler, CupScheduler, LeagueScheduler}
import utils.WorldDetailsSingleRequest

import java.util.Calendar
import scala.concurrent.Await
import scala.concurrent.duration._


object LoaderApp extends App {
  private val logger = Logger(LoggerFactory.getLogger(this.getClass))

  implicit val actorSystem: ActorSystem = ActorSystem("LoaderActorSystem")
  import actorSystem.dispatcher

  try {
    val cliConfig = new CommandLine(args).toCliConfig
    val config = ConfigFactory.load()

    val injector = Guice.createInjector(new LoaderModule(config, actorSystem))

    implicit val oauthTokens: OauthTokens = injector.getInstance(classOf[OauthTokens])

    val executorActorFactory: ExecutorActorFactory = injector
      .getInstance(classOf[ExecutorActorFactory])

    val worldDetailsFuture = WorldDetailsSingleRequest.request(leagueId = None)

    val worldDetails = Await.result(worldDetailsFuture, 30.seconds)

    cliConfig match {
      case ScheduleConfig(fromOpt, entity, lastMatchWindow) =>
        val (taskExecutorActor, scheduler) = executorAndScheduler(entity, lastMatchWindow, executorActorFactory, worldDetails)
        fromOpt match {
          case Some(from) => scheduler.scheduleFrom(from)
          case None => scheduler.schedule()
        }
        actorSystem.scheduler.scheduleWithFixedDelay(0.second, 5.second)(() => taskExecutorActor ! TryToExecute)
      case LoadConfig(leagues, entity, lastMatchWindow) =>
        val (taskExecutorActor, scheduler) = executorAndScheduler(entity, lastMatchWindow, executorActorFactory, worldDetails)
        scheduler.load(leagues)
        actorSystem.scheduler.scheduleWithFixedDelay(0.second, 5.second)(() => taskExecutorActor ! TryToExecute)
      case TeamRankingsConfig(Some(league)) =>
        Await.result(TeamRankJoiner.joinTeamRankings(config, worldDetails.leagueList.find(_.leagueName == league).get), 3.minute)
      case TeamRankingsConfig(None) =>
        worldDetails.leagueList.foreach(league => {
          Await.result(TeamRankJoiner.joinTeamRankings(config, league), 3.minute)
        })
      case LoadScheduledConfig(entity, lastMatchWindow) =>
        val realEntity = if (entity == "auto") {
          val c = Calendar.getInstance()
          val dayOfWeek = c.get(Calendar.DAY_OF_WEEK)
          if (Set(Calendar.FRIDAY, Calendar.SATURDAY, Calendar.SUNDAY, Calendar.MONDAY).contains(dayOfWeek)) {
            "league"
          } else {
            "cup"
          }
        } else {
          entity
        }
        logger.info(s"Entity type: $entity")
        val (taskExecutorActor, scheduler) = executorAndScheduler(realEntity, lastMatchWindow, executorActorFactory, worldDetails)
        scheduler.loadScheduled()
        actorSystem.scheduler.scheduleWithFixedDelay(0.second, 5.second)(() => taskExecutorActor ! TryToExecute)
    }
  } catch {
    case e: Throwable =>
      logger.error(e.getMessage, e)
      actorSystem.terminate()
  }

  private def executorAndScheduler(entity: String, lastMatchesWindow: Int, executorActorFactory: ExecutorActorFactory, worldDetails: WorldDetails): (ActorRef, AbstractScheduler) = {
    if (entity== "league") {
      val taskExecutorActor = executorActorFactory.createLeagueExecutorActor(worldDetails, lastMatchesWindow)
      (taskExecutorActor, new LeagueScheduler(worldDetails, taskExecutorActor))
    } else if (entity == "cup") {
      val taskExecutorActor = executorActorFactory.createCupExecutorActor(worldDetails, lastMatchesWindow)
      (taskExecutorActor, new CupScheduler(worldDetails, taskExecutorActor))
    } else {
      throw new Exception(s"Unknown/unsupported ${args(1)} match type")
    }
  }
}
