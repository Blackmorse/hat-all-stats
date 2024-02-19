package executors

import executors.TaskExecutorActor.{ScheduleFinished, ScheduleTask, TaskFinished, TryToExecute}
import akka.actor.Actor
import akka.stream.scaladsl.{Keep, Sink, Source}
import chpp.OauthTokens
import chpp.worlddetails.models.{League, WorldDetails}
import clickhouse.HattidClickhouseClient
import com.typesafe.scalalogging.Logger
import org.slf4j.LoggerFactory
import telegram.LoaderTelegramClient
import utils.WorldDetailsSingleRequest

import java.util.Date
import scala.concurrent.Future
import scala.util.{Failure, Success}

object TaskExecutorActor {
  trait Message

  case class ScheduleTask(leagueId: Int, time: Date) extends Message

  case object ScheduleFinished

  case object TryToExecute

  case object TaskFinished
}

abstract class TaskExecutorActor[GraphMat, MatValue](graph: Sink[Int, GraphMat],
                        worldDetails: WorldDetails,
                        matToFuture: GraphMat => Future[MatValue],
                        telegramClient: LoaderTelegramClient)
                        (implicit oauthTokens: OauthTokens)
  extends Actor {
  private val logger = Logger(LoggerFactory.getLogger(this.getClass))

  import context.{dispatcher, system}

  private var tasks = List[ScheduleTask]()
  private var running = false

  override def receive: Receive = {
    case task @ ScheduleTask(_, time) =>
      val beforeTasks = tasks.takeWhile(_.time.compareTo(time) <= 0)
      val afterTasks = tasks.dropWhile(_.time.compareTo(time) <= 0)
      tasks = beforeTasks ++ List(task) ++ afterTasks
    case ScheduleFinished =>
      tasks.foreach(task => logger.info(s"Scheduled loading of $task"))
      notifyScheduled(tasks)
    case TaskFinished =>
      running = false
      val nextTaskOption = tasks.headOption

      nextTaskOption.foreach(nextTask => {
        val league = worldDetails.leagueList.filter(_.leagueId == nextTask.leagueId).head
        logger.info(s"Next task is (${league.leagueId}, ${league.leagueName}) scheduled for ${nextTask.time}")
      })
    case TryToExecute =>
      if(!running) {
        if (tasks.isEmpty) {
          logger.info("No tasks running and no tasks in the queue. Turning off!")
          System.exit(0)
        } else {
          val task = tasks.head
          if (task.time.before(new Date())) {
            tasks = tasks.drop(1)
            val league = worldDetails.leagueList.filter(_.leagueId == task.leagueId).head
            if(checkTaskAlreadyDoneAndTryToFix(league)) {
              logger.info(s"(${task.leagueId}, ${league.leagueName}) is already done!")
              self ! TryToExecute
            } else {
              running = true

              preCleanupTables(league)
              notifyLeagueStarted(league)
              logger.info(s"Started league (${task.leagueId}, ${league.leagueName})")

              val mat = Source.single(task.leagueId).toMat(graph)(Keep.right).run()

              matToFuture(mat).onComplete {
                case Failure(exception) =>
                  logger.error(s"Failed to upload ${task.leagueId}", exception)
                  self ! ScheduleTask(task.leagueId,
                    new Date(System.currentTimeMillis() + 30 * 60 * 1000))
                  telegramClient.sendException("Loader failed at streaming stage", exception)
                  self ! TaskFinished
                case Success(matValue) =>
                  val updatedLeagueFuture = WorldDetailsSingleRequest.request(leagueId = Some(league.leagueId)).map(_.leagueList.head);

                  updatedLeagueFuture.foreach(updatedLeague => {
                    val result = postProcessLoadedResults(updatedLeague, matValue)
                    result.onComplete {
                      case Failure(exception) =>
                        logger.error(exception.getMessage, exception)
                        telegramClient.sendException("Loader failed at post processing stage", exception)
                        self ! TaskFinished
                      case Success(_) =>
                        logger.info(s"(${updatedLeague.leagueId}, ${updatedLeague.leagueName}) successfully loaded")
                        logTaskFinished(updatedLeague).onComplete {
                          case Failure(e) =>
                            telegramClient.sendException("Loader failed at marking task with history log", e)
                            self ! TaskFinished
                          case Success(_) =>
                            notifyLeagueFinished(updatedLeague)
                            self ! TaskFinished
                        }
                    }
                  })
              }
            }
          }
        }
      } else {
        logger.debug("Some task is running!")
      }
  }

  def checkTaskAlreadyDoneAndTryToFix(league: League): Boolean

  def notifyScheduled(tasks: List[ScheduleTask])

  def notifyLeagueStarted(league: League)

  def notifyLeagueFinished(league: League)

  def postProcessLoadedResults(league: League, matValue: MatValue): Future[_]

  def logTaskFinished(league: League): Future[_]

  def preCleanupTables(league: League): Unit
}
