package clickhouse

import akka.actor.ActorSystem
import chpp.OauthTokens
import chpp.matchesarchive.models.MatchType
import chpp.worlddetails.models.League
import com.crobox.clickhouse.ClickhouseClient
import com.typesafe.config.Config
import org.slf4j.LoggerFactory
import utils.WorldDetailsSingleRequest

import scala.concurrent.{ExecutionContext, Future}

class PlayerStatsClickhouseClient(config: Config)(implicit oauthTokens: OauthTokens,
                                                  actorSystem: ActorSystem,
                                                  executionContext: ExecutionContext) {
  private val logger  = LoggerFactory.getLogger(this.getClass)

  private val client = new ClickhouseClient(Some(config))
  private val databaseName = config.getString("database_name")

  def join(leagueId: Int, matchType: MatchType.Value): Future[String] = {

    for{league <- WorldDetailsSingleRequest.request(leagueId = Some(leagueId)).map(_.leagueList.head);
        _ <- executePlayerJoinRequest(league, matchType)
        _ <- truncateTable("player_info", league, matchType)
        r <- truncateTable("player_events", league, matchType)
        } yield r
  }

  private def executePlayerJoinRequest(league: League, matchType: MatchType.Value): Future[String] = {
    logger.info(s"Joining player_info with player_events for (${league.leagueId}, ${league.leagueName})...")
    client.execute(PlayerStatsJoiner.playerStatsJoinRequest(league, matchType, databaseName))
  }

  private def truncateTable(table: String, league: League, matchType: MatchType.Value): Future[String] = {
    logger.info(s"Truncating table $table while joining for (${league.leagueId}, ${league.leagueName})...")
    client.execute(TableTruncater.sql(league, matchType, table, databaseName))
  }
}
