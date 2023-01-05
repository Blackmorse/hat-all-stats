package loadergraph

import akka.stream.scaladsl.Flow
import com.crobox.clickhouse.stream.Insert
import spray.json._

object ClickhouseFlow {
  def apply[Model](database: String, table: String)(implicit writes: JsonWriter[Model]): Flow[Model, Insert, _] = {
    Flow[Model]
      .map(model => Insert(s"$database.$table", model.toJson.prettyPrint))
  }
}
