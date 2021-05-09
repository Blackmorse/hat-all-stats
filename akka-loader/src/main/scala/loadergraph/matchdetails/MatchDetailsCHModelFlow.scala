package loadergraph.matchdetails

import akka.actor.ActorSystem
import akka.stream.scaladsl.Flow
import com.crobox.clickhouse.stream.Insert
import flows.ClickhouseFlow
import models.clickhouse
import models.clickhouse.MatchDetailsCHModel
import models.stream.StreamMatchDetails

object MatchDetailsCHModelFlow {
  def apply(databaseName: String)(implicit system: ActorSystem): Flow[StreamMatchDetails, Insert, _] = {
    Flow[StreamMatchDetails].map(MatchDetailsCHModel.convert)
      .via(ClickhouseFlow[clickhouse.MatchDetailsCHModel](databaseName, "match_details"))
  }
}
