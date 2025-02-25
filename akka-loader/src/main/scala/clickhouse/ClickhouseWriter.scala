package clickhouse

import org.apache.pekko.Done
import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.stream.scaladsl.{Keep, Sink, Source}
import com.crobox.clickhouse.stream.Insert
import loadergraph.ClickhouseFlow
import org.slf4j.LoggerFactory
import spray.json.JsonFormat

import scala.concurrent.Future

object ClickhouseWriter {
  private val logger = LoggerFactory.getLogger(this.getClass)

  def writeToCh[T](entities: List[T], chSink: Sink[Insert, Future[Done]], settings: ActorSystem.Settings)
                  (implicit format: JsonFormat[T], system: ActorSystem): Future[Done] = {
    val databaseName = settings.config.getString("database_name")

    logger.info(s"Writing ${entities.size} promotion entries to $databaseName.promotions...")

    Source(entities).log("pipeline_log")
      .via(ClickhouseFlow[T](databaseName, "promotions"))
      .toMat(chSink)(Keep.right)
      .run()
  }
}
