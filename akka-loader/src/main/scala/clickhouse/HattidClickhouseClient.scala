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

  def truncateTable(table: String, league: League, matchType: MatchType.Value): Future[String] = {
    logger.info(s"Truncating table $table while joining for (${league.leagueId}, ${league.leagueName})...")
    client.execute(TableTruncater.sql(league, matchType, table, databaseName))
  }

  def checkInMatchDetailsAndPlayerStatsAreEmpty(league: League, matchType: MatchType.Value): Boolean = {
    val round = realRound(matchType, league)
    val season = league.season - league.seasonOffset
    val cupLevelCondition = if (matchType == MatchType.LEAGUE_MATCH) " = 0 " else " != 0"
    val matchDetailsCountFuture = client.query(s"SELECT count() from $databaseName.match_details where " +
      s" league_id = ${league.leagueId} and season = $season and round = $round and cup_level_index $cupLevelCondition")

    val playerStatsCountFuture = client.query(s"SELECT count() from $databaseName.player_stats where " +
      s" league_id = ${league.leagueId} and season = $season and round = $round and cup_level_index $cupLevelCondition")

    val matchDetailsCount = Await.result(matchDetailsCountFuture, 30.seconds).trim.replace("\n", "").replace("\r", "")
    val playerStatsCount = Await.result(playerStatsCountFuture, 30.seconds).trim.replace("\n", "").replace("\r", "")

    if (matchType == MatchType.LEAGUE_MATCH) {
      val teamDetailsCountrsFuture = client.query(s"SELECT count() from $databaseName.team_details where " +
        s" league_id = ${league.leagueId} and season = $season and round = $round")

      val teamRankingsCountFuture = client.query(s"SELECT count() from $databaseName.team_rankings where " +
        s" league_id = ${league.leagueId} and season = $season and round = $round")

      val teamDetailsCount = Await.result(teamDetailsCountrsFuture, 30.seconds).trim.replace("\n", "").replace("\r", "")
      val teamRankingsCount = Await.result(teamRankingsCountFuture, 30.seconds).trim.replace("\n", "").replace("\r", "")

      matchDetailsCount == "0" && playerStatsCount == "0" && teamDetailsCount == "0" && teamRankingsCount == "0"
    } else {
      matchDetailsCount == "0" && playerStatsCount == "0"
    }
  }

  def logUploadEntry(league: League, matchType: MatchType.Value): Future[_] = {
    val round = realRound(matchType, league)
    val season = league.season - league.seasonOffset
    val isLeagueMatch = if (matchType == MatchType.LEAGUE_MATCH) 1 else 0
    client.execute(s"INSERT INTO $databaseName.upload_history (league_id, season, round, is_league_match) VALUES " +
      s" (${league.leagueId}, $season, $round, $isLeagueMatch)")
  }

  def checkUploaded(league: League, matchType: MatchType.Value): Boolean = {
    val round = realRound(matchType, league)
    val season = league.season - league.seasonOffset
    val isLeagueMatch = if (matchType == MatchType.LEAGUE_MATCH) 1 else 0

    val resultFuture = client.query(s"SELECT count() from $databaseName.upload_history WHERE " +
      s" league_id = ${league.leagueId} AND season = $season AND round = $round and is_league_match = $isLeagueMatch")

    val result = Await.result(resultFuture, 30.seconds).trim.replace("\n", "").replace("\r", "")

    result != "0"
  }

  def tryToFixLeagueData(league: League): Future[_] = {
    val round = realRound(MatchType.LEAGUE_MATCH, league)
    val season = league.season - league.seasonOffset
    val cupLevelCondition = " AND cup_level_index = 0"

    def deleteSqlQuery(table: String, cupLevelCondition: String): String =
      s"DELETE FROM $databaseName.$table WHERE league_id = ${league.leagueId} AND season = $season AND round = $round  $cupLevelCondition"

    for {
      _ <- client.execute(deleteSqlQuery("match_details", cupLevelCondition))
      _ <- truncateTable("player_info", league, MatchType.LEAGUE_MATCH)
      _ <- truncateTable("player_events", league, MatchType.LEAGUE_MATCH)
      // Lightweight deletes is not supported for tables with projections
      _ <- client.execute(s"ALTER TABLE $databaseName.player_stats DELETE WHERE league_id = ${league.leagueId} AND season = $season AND round = $round  $cupLevelCondition SETTINGS mutations_sync = 1")
      _ <- client.execute(deleteSqlQuery("team_details", ""))
      r <- client.execute(deleteSqlQuery("team_rankings", ""))
    } yield r
  }

  def tryToFixCupData(league: League): Future[_] = {
    val round = realRound(MatchType.CUP_MATCH, league)
    val season = league.season - league.seasonOffset
    val cupLevelCondition = " AND cup_level_index != 0"

    def alterDeleteSqlQuery(table: String, cupLevelCondition: String): String =
      s"DELETE FROM $databaseName.$table WHERE league_id = ${league.leagueId} AND season = $season AND round = $round  $cupLevelCondition"

    for {
      _ <- client.execute(alterDeleteSqlQuery("match_details", cupLevelCondition))
      _ <- truncateTable("player_info", league, MatchType.CUP_MATCH)
      _ <- truncateTable("player_events", league, MatchType.CUP_MATCH)
      r <- client.execute(alterDeleteSqlQuery("player_stats", cupLevelCondition))
    } yield r
  }
}
