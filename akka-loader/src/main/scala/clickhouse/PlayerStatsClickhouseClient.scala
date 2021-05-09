package clickhouse

import akka.actor.ActorSystem
import chpp.OauthTokens
import chpp.matchesarchive.models.MatchType
import com.crobox.clickhouse.ClickhouseClient
import com.typesafe.config.Config
import utils.WorldDetailsSingleRequest

import scala.concurrent.ExecutionContext

class PlayerStatsClickhouseClient(config: Config)(implicit oauthTokens: OauthTokens,
                                                  actorSystem: ActorSystem,
                                                  executionContext: ExecutionContext) {
  private val client = new ClickhouseClient(Some(config))
  private val databaseName = config.getString("database_name")

  def join(leagueId: Int, matchType: MatchType.Value) = {
    for{league <- WorldDetailsSingleRequest.request(leagueId = Some(leagueId)).map(_.leagueList.head);
        _ <- client.execute(PlayerStatsJoiner.playerStatsJoinRequest(league, matchType, databaseName))
        _ <- client.execute(TableTruncater.sql(league, matchType, "player_info", databaseName))
        r <- client.execute(TableTruncater.sql(league, matchType, "player_events", databaseName))
        } yield r
  }
}
