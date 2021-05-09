package clickhouse

import akka.Done
import akka.actor.ActorSystem
import akka.stream.scaladsl.{Keep, Sink, Source}
import com.crobox.clickhouse.stream.Insert
import flows.ClickhouseFlow
import spray.json.JsonFormat

import scala.concurrent.Future

object ClickhouseWriter {
  def writeToCh[T](entities: List[T], chSink: Sink[Insert, Future[Done]], settings: ActorSystem.Settings)
                  (implicit format: JsonFormat[T], system: ActorSystem): Future[Done] = {
    Source(entities).log("pipeline_log")
      .via(ClickhouseFlow[T](settings.config.getString("database_name"), "promotions"))
      .toMat(chSink)(Keep.right)
      .run()
  }
}
