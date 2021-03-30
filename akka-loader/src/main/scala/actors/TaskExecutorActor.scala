package actors

import actors.TaskExecutorActor.{ScheduleTask, TaskFinished, TryToExecute}
import akka.Done
import akka.actor.Actor
import akka.stream.scaladsl.{Flow, Keep, Sink, Source}
import chpp.OauthTokens
import clickhouse.HattidLoaderClickhouseClient
import com.crobox.clickhouse.stream.Insert
import com.typesafe.scalalogging.Logger
import flows.ClickhouseFlow
import models.clickhouse.{MatchDetailsCHModel, PromotionModelCH}
import models.stream.{StreamMatchDetails, StreamTeam}
import org.slf4j.LoggerFactory
import promotions.PromotionsCalculator
import utils.WorldDetailsSingleRequest

import java.util.Date
import scala.concurrent.Future
import scala.util.{Failure, Success}

object TaskExecutorActor {
  trait Message

  case class ScheduleTask(leagueId: Int, time: Date) extends Message

  case object TryToExecute

  case object TaskFinished
}

class TaskExecutorActor(graph: Sink[Int, (Future[List[StreamTeam]], Future[Done])], chSink: Sink[Insert, Future[Done]],
                        hattidClient: HattidLoaderClickhouseClient)(implicit oauthTokens: OauthTokens)
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
            val (promotionsFuture, roundFuture) = Source.single(task.leagueId).toMat(graph)(Keep.right).run()
            roundFuture.zip(promotionsFuture)
              .onComplete {
              case Failure(exception) =>
                logger.error(s"Failed to upload ${task.leagueId}", exception)
                self ! ScheduleTask(task.leagueId,
                  new Date(System.currentTimeMillis() + 30 * 60 * 1000))
                self ! TaskFinished
              case
                Success((_, promotionsTeams)) =>

                val result = for {
                 leagueWorldDetails <- WorldDetailsSingleRequest.request(Some(task.leagueId))
                 _ <- hattidClient.join(task.leagueId)
                 promotions <- Future(PromotionsCalculator.calculatePromotions(
                   new PromotionsCalculator(leagueWorldDetails.leagueList.head, promotionsTeams),
                   leagueWorldDetails.leagueList.head.numberOfLevels))
                r <- {
                  println(promotions)

                  Source(promotions).log("pipeline_log").via(ClickhouseFlow[PromotionModelCH](context.system.settings.config.getString("database_name"), "promotions")).toMat(chSink)(Keep.right).run()
                }
                } yield {
                  logger.info(s"${task.leagueId} successfully loaded")
                  self ! TaskFinished
                  r
                }

                  result.onComplete{
                    case Failure(exception) =>
                      logger.error(exception.getMessage, exception)
                    case Success(_) =>
                      logger.info("Success")
                  }

            }
          }
        }
      } else {
        logger.debug("Some task is running!")
      }
  }
}
