package alltid
import executors.TaskExecutorActor.ScheduleTask
import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.http.scaladsl.Http
import org.apache.pekko.http.scaladsl.model.{ContentTypes, HttpEntity, HttpMethods, HttpRequest}
import chpp.worlddetails.models.League
import com.typesafe.config.Config
import com.typesafe.scalalogging.Logger
import org.slf4j.LoggerFactory
import spray.json.DefaultJsonProtocol.immSeqFormat
import spray.json.{JsNumber, JsObject, JsValue, JsonFormat}

import java.time.{ZoneId, ZonedDateTime}
import spray.json._

import javax.inject.{Inject, Singleton}

@Singleton
class AlltidClient @Inject()(implicit val system: ActorSystem,
                             val config: Config) {
  private val hattidUrl = config.getString("hattid_web_url")

  case class RestScheduleTask(leagueId: Int, time: Long)

  object RestScheduleTask {
    implicit val format: JsonFormat[RestScheduleTask] = new JsonFormat[RestScheduleTask] {
      override def read(json: JsValue): RestScheduleTask = null

      override def write(obj: RestScheduleTask): JsValue = {
        JsObject(
          ("leagueId", JsNumber(obj.leagueId)),
          ("time", JsNumber(obj.time))
        )
      }
    }
  }

  private val logger = Logger(LoggerFactory.getLogger(this.getClass))

  def notifyScheduleInfo(tasks: Seq[ScheduleTask]): Unit = {
    import system.dispatcher

    val resTasks = tasks.map(task => {
      RestScheduleTask(task.leagueId, ZonedDateTime.ofInstant(task.time.toInstant, ZoneId.systemDefault()).toInstant.getEpochSecond * 1000L)
    }).sortBy(_.time)
    Http().singleRequest(
      HttpRequest(
        uri = s"$hattidUrl/loader/scheduleInfo",
        entity = HttpEntity(ContentTypes.`application/json`, resTasks.toJson.toString()),
        method = HttpMethods.POST
      )).onComplete(r => logger.info(s"Notification about scheduling has been sent. Result of notification: $r"))
  }

  def notifyCountryLoadingStarted(league: League): Unit = {
    import system.dispatcher
    Http().singleRequest(HttpRequest(
      uri = s"$hattidUrl/loader/loadingStarted?leagueId=${league.leagueId}",
      method = HttpMethods.POST)).onComplete(r => logger.info(s"Alltid has been notified about start of ${league.leagueId} (${league.leagueName}) league loading." +
      s" Result of notification: $r"))
  }

  def notifyCountryLoadingFinished(league: League): Unit = {
    import system.dispatcher
    Http().singleRequest(HttpRequest(
      uri = s"$hattidUrl/loader/leagueRound?season=${league.season - league.seasonOffset}&leagueId=${league.leagueId}&round=${league.matchRound - 1}",
      method = HttpMethods.POST
    )).onComplete(r => logger.info(s"Alltid has been notified about round loading finished for ${league.leagueId} (${league.leagueName}). Result of notification: $r"))
  }
}
