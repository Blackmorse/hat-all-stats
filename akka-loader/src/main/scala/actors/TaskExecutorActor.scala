package actors

import actors.LeagueTaskExecutorActor.{ScheduleTask, TaskFinished, TryToExecute}
import akka.Done
import akka.actor.Actor
import akka.stream.scaladsl.{Keep, Sink, Source}
import chpp.matchesarchive.models.MatchType
import chpp.worlddetails.models.{League, WorldDetails}
import clickhouse.PlayerStatsClickhouseClient
import com.typesafe.scalalogging.Logger
import org.slf4j.LoggerFactory

import java.util.Date
import scala.concurrent.Future
import scala.util.{Failure, Success}

abstract class TaskExecutorActor[GraphMat, MatValue](graph: Sink[Int, GraphMat],
                        worldDetails: WorldDetails,
                        matToFuture: GraphMat => Future[MatValue]) extends Actor {
  private val logger = Logger(LoggerFactory.getLogger(this.getClass))

  import context.{dispatcher, system}

  private var tasks = List[ScheduleTask]()
  private var running = false

  override def receive: Receive = {
    case task @ ScheduleTask(_, time) =>
      val beforeTasks = tasks.takeWhile(_.time.compareTo(time) <= 0)
      val afterTasks = tasks.dropWhile(_.time.compareTo(time) <= 0)
      tasks = beforeTasks ++ List(task) ++ afterTasks
      logger.info(s"Scheduled loading of $task")
    case TaskFinished =>
      running = false
    case TryToExecute =>
      if(!running) {
        if (tasks.isEmpty) {
          logger.info("No tasks running and no tasks in the queue. Turning off!")
          System.exit(0)
        } else {
          val task = tasks.head
          if (task.time.before(new Date())) {
            tasks = tasks.drop(1)
            running = true
            val league = worldDetails.leagueList.filter(_.leagueId == task.leagueId).head
            logger.info(s"Started league (${task.leagueId}, ${league.leagueName})")

            val mat = Source.single(task.leagueId).toMat(graph)(Keep.right).run()

            matToFuture(mat).onComplete{
              case Failure(exception) =>
                logger.error(s"Failed to upload ${task.leagueId}", exception)
                self ! ScheduleTask(task.leagueId,
                  new Date(System.currentTimeMillis() + 30 * 60 * 1000))
                self ! TaskFinished
              case Success(matValue) =>
                val result = postProcessLoadedResults(league, matValue)
                result.onComplete{
                  case Failure(exception) =>
                    logger.error(exception.getMessage, exception)
                  case Success(_) =>
                    logger.info(s"(${league.leagueId}, ${league.leagueName}) successfully loaded")
                    self ! TaskFinished
                }
            }

          }
        }
      } else {
        logger.debug("Some task is running!")
      }
  }

  def postProcessLoadedResults(league: League, matValue: MatValue): Future[_]
}
