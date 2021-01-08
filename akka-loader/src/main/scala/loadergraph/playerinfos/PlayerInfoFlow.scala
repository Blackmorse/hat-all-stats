package loadergraph.playerinfos

import akka.actor.ActorSystem
import akka.stream.scaladsl.{Flow, Source}
import chpp.OauthTokens
import chpp.players.{PlayersHttpFlow, PlayersRequest}
import flows.LogProgressFlow
import models.clickhouse.PlayerInfoModelCH
import models.stream.StreamMatchDetails

import scala.concurrent.ExecutionContext

object PlayerInfoFlow {
  def apply(countryMap: Map[Int, Int])(implicit oauthTokens: OauthTokens, system: ActorSystem,
              executionContext: ExecutionContext): Flow[StreamMatchDetails, PlayerInfoModelCH, _] = {
    Flow[StreamMatchDetails]
      .map(matchDetails => (PlayersRequest(teamId = Some(matchDetails.matc.team.id)), matchDetails))
      .async
      .via(PlayersHttpFlow())
      .async
      .via(LogProgressFlow("Players of teams"))
      .flatMapConcat{case(players, matchDetails) =>
        val playerInfos = players.team.playerList.map(player => PlayerInfoModelCH.convert(player, matchDetails, countryMap))
        Source(playerInfos.toList)
      }.async
  }
}
