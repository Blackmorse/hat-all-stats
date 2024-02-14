package clickhouse

import akka.actor.ActorSystem
import chpp.commonmodels.MatchType
import chpp.worlddetails.models.League
import com.crobox.clickhouse.ClickhouseClient
import com.crobox.clickhouse.internal.QuerySettings
import com.typesafe.config.Config
import org.slf4j.LoggerFactory
import utils.realRound

import javax.inject.{Inject, Singleton}
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}

@Singleton
class HattidClickhouseClient @Inject()(val config: Config,
                                       implicit val actorSystem: ActorSystem)
{
  import actorSystem.dispatcher
  private val logger  = LoggerFactory.getLogger(this.getClass)

  private val client = new ClickhouseClient(Some(config))
  private val databaseName = config.getString("database_name")

  def join(league: League, matchType: MatchType.Value): Future[String] = {

    for{_ <- executePlayerJoinRequest(league, matchType)
        _ <- truncateTable("player_info", league, matchType)
        r <- truncateTable("player_events", league, matchType)
        } yield r
  }

  private implicit val querySettings: QuerySettings = QuerySettings(authentication = Some((
    config.getString("crobox.clickhouse.client.authentication.user"),
    config.getString("crobox.clickhouse.client.authentication.password"))))
  private def executePlayerJoinRequest(league: League, matchType: MatchType.Value): Future[String] = {
    logger.info(s"Joining player_info with player_events for (${league.leagueId}, ${league.leagueName})...")
    client.execute(PlayerStatsJoiner.playerStatsJoinRequest(league, matchType, databaseName))
  }

  private def truncateTable(table: String, league: League, matchType: MatchType.Value): Future[String] = {
    logger.info(s"Truncating table $table while joining for (${league.leagueId}, ${league.leagueName})...")
    client.execute(TableTruncater.sql(league, matchType, table, databaseName))
  }

  def checkDataInMatchDetails(league: League, matchType: MatchType.Value): Boolean = {
    val round = realRound(matchType, league)
    val season = league.season - league.seasonOffset
    val cupLevelCondition = if (matchType == MatchType.LEAGUE_MATCH) " = 0 " else " != 0"
    val f = client.query(s"SELECT count() from $databaseName.match_details where " +
      s" league_id = ${league.leagueId} and season = $season and round = $round and cup_level_index $cupLevelCondition")
    val res = Await.result(f, 30.seconds).trim.replace("\n", "").replace("\r", "")
    res != "0"
  }
}
