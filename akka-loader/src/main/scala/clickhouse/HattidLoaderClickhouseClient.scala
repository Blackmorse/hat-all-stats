package clickhouse

import akka.actor.ActorSystem
import chpp.OauthTokens
import chpp.worlddetails.models.League
import com.crobox.clickhouse.ClickhouseClient
import com.typesafe.config.Config
import utils.WorldDetailsSingleRequest

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Success, Try}

class HattidLoaderClickhouseClient(config: Config)(implicit oauthTokens: OauthTokens,
                                                   actorSystem: ActorSystem,
                                                   executionContext: ExecutionContext) {
  private val client = new ClickhouseClient(Some(config))
  private val databaseName = config.getString("database_name")

  def join(leagueId: Int) = {
    for{league <- WorldDetailsSingleRequest.request(leagueId = Some(leagueId)).map(_.leagueList.head);
        _ <- client.execute(PlayerStatsJoiner.playerStatsJoinRequest(league, databaseName));
        _ <- client.execute(TableTruncater.sql(league, "player_info", databaseName));
        - <- client.execute(TableTruncater.sql(league, "player_events", databaseName));
        r <- createTeamRankJoinerSql(league)} yield r
  }

  private def createTeamRankJoinerSql(league: League): Future[Try[Unit]] = {
    val seqFuture = (1 to league.numberOfLevels).map(Some(_)).concat(Seq(None))
      .map(level => {
        val sql = TeamRankJoiner.createSql(
          season = league.season - league.seasonOffset,
          leagueId = league.leagueId,
          round = league.matchRound - 1,
          divisionLevel = level,
          database = databaseName
        )
        client.execute(sql)
      })

    Future.sequence(seqFuture).map(_ => Success(()))
  }
}
