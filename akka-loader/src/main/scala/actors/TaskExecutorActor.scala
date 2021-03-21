package actors

import actors.TaskExecutorActor.{ScheduleTask, TaskFinished, TryToExecute}
import akka.Done
import akka.actor.Actor
import akka.stream.scaladsl.{Keep, Sink, Source}
import clickhouse.HattidLoaderClickhouseClient
import com.typesafe.scalalogging.Logger
import org.slf4j.LoggerFactory

import java.util.Date
import scala.concurrent.Future
import scala.util.{Failure, Success}

object TaskExecutorActor {
  trait Message

  case class ScheduleTask(leagueId: Int, time: Date) extends Message

  case object TryToExecute

  case object TaskFinished
}

class TaskExecutorActor(graph: Sink[Int, Future[Done]],
                        hattidClient: HattidLoaderClickhouseClient)
    extends Actor {
  import context.{dispatcher, system}

  private val logger = Logger(LoggerFactory.getLogger(this.getClass))

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
        if(tasks.isEmpty) {
          logger.info("No tasks running and no tasks in the queue. Turning off!")
          System.exit(0)
        } else {
          val task = tasks.head
          if (task.time.before(new Date())) {
            tasks = tasks.drop(1)
            running = true
            logger.info(s"Started league ${task.leagueId}")
            Source.single(task.leagueId).toMat(graph)(Keep.right).run()
              .onComplete {
                case Failure(exception) =>
                  logger.error(s"Failed to upload ${task.leagueId}", exception)
                  self ! ScheduleTask(task.leagueId,
                    new Date(System.currentTimeMillis() + 3 * 60 * 1000))
                  self ! TaskFinished
                case Success(_) =>
                  hattidClient.join(task.leagueId)
                  logger.info(s"${task.leagueId} successfully loaded")
                  self ! TaskFinished
              }
          }
        }
      } else {
        logger.debug("Some task is running!")
      }
  }
}
