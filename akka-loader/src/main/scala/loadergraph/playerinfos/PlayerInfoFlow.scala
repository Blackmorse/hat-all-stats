package loadergraph.playerinfos

import akka.actor.ActorSystem
import akka.stream.scaladsl.{Flow, Source}
import flows.LogProgressFlow
import flows.http.PlayersFlow
import models.OauthTokens
import models.clickhouse.PlayerInfoModelCH
import models.stream.StreamMatchDetails
import requests.PlayersRequest

import scala.concurrent.ExecutionContext

object PlayerInfoFlow {
  def apply()(implicit oauthTokens: OauthTokens, system: ActorSystem,
              executionContext: ExecutionContext) = {
    Flow[StreamMatchDetails]
      .map(matchDetails => (PlayersRequest(teamId = Some(matchDetails.matc.team.id)), matchDetails))
      .async
      .via(PlayersFlow())
      .via(LogProgressFlow("Players of teams"))
      .flatMapConcat{case(players, matchDetails) =>
        val playerInfos = players.team.playerList.map(player => PlayerInfoModelCH.convert(player, matchDetails))
        Source(playerInfos.toList)
      }
  }
}
