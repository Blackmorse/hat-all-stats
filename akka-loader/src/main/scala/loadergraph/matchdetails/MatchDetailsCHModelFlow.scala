package loadergraph.matchdetails

import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.stream.scaladsl.Flow
import com.crobox.clickhouse.stream.Insert
import loadergraph.ClickhouseFlow
import loadergraph.matchlineup.StreamMatchDetailsWithLineup
import models.clickhouse
import models.clickhouse.MatchDetailsCHModel

object MatchDetailsCHModelFlow {
  def apply(databaseName: String)(implicit system: ActorSystem): Flow[StreamMatchDetailsWithLineup, Insert, _] = {
    Flow[StreamMatchDetailsWithLineup].map(MatchDetailsCHModel.convert)
      .via(ClickhouseFlow[clickhouse.MatchDetailsCHModel](databaseName, "match_details"))
  }
}
