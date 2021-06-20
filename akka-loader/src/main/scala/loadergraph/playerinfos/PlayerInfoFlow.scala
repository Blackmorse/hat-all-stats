package loadergraph.playerinfos

import akka.actor.ActorSystem
import akka.stream.scaladsl.{Flow, Source}
import chpp.OauthTokens
import chpp.players.PlayersRequest
import com.crobox.clickhouse.stream.Insert
import flows.{ClickhouseFlow, LogProgressFlow, PlayersHttpFlow}
import models.clickhouse.PlayerInfoModelCH
import models.stream.StreamMatchDetails

import scala.concurrent.ExecutionContext

object PlayerInfoFlow {
  def apply(databaseName: String, countryMap: Map[Int, Int])(implicit oauthTokens: OauthTokens, system: ActorSystem,
              executionContext: ExecutionContext): Flow[StreamMatchDetails, Insert, _] = {
    Flow[StreamMatchDetails]
      .map(matchDetails => (PlayersRequest(teamId = Some(matchDetails.matc.team.id), includeMatchInfo = Some(true)), matchDetails))
      .async
      .via(PlayersHttpFlow())
      .async
      .via(LogProgressFlow("Players of teams", Some(_._2.matc.team.leagueUnit.league.activeTeams)))
      .flatMapConcat{case(players, matchDetails) =>
        val playerInfos = players.team.playerList.map(player => PlayerInfoModelCH.convert(player, matchDetails, countryMap))
        Source(playerInfos.toList)
      }
      .via(ClickhouseFlow[PlayerInfoModelCH](databaseName, "player_info"))
  }
}
