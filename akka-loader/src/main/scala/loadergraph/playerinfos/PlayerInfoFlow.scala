package loadergraph.playerinfos

import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.stream.scaladsl.{Flow, Source}
import chpp.OauthTokens
import chpp.players.PlayersRequest
import com.crobox.clickhouse.stream.Insert
import httpflows.PlayersHttpFlow
import loadergraph.matchlineup.StreamMatchDetailsWithLineup
import loadergraph.{ClickhouseFlow, LogProgressFlow}
import models.clickhouse.PlayerInfoModelCH

import scala.concurrent.ExecutionContext

object PlayerInfoFlow {
  def apply(databaseName: String, countryMap: Map[Int, Int])(implicit oauthTokens: OauthTokens, system: ActorSystem,
              executionContext: ExecutionContext): Flow[StreamMatchDetailsWithLineup, Insert, _] = {
    Flow[StreamMatchDetailsWithLineup]
      .map(matchDetails => (PlayersRequest(teamId = Some(matchDetails.matc.team.id), includeMatchInfo = Some(true)), matchDetails))
      .async
      .via(PlayersHttpFlow())
      .async
      .via(LogProgressFlow("Players of teams", Some(_._2.matc.team.leagueUnit.league.activeTeams)))
      .flatMapConcat{case(players, matchDetails) =>
        val matchDuration = players.team.playerList
          .flatMap(_.lastMatch)
          .filter(lastMatch => lastMatch.matchId == matchDetails.matc.id)
          .map(_.playedMinutes)
          .maxOption
          .getOrElse(0)

        val playerInfos = players.team.playerList
          .map(player =>
            PlayerInfoModelCH.convert(player = player,
              matchDetails = matchDetails,
              matchDuration = matchDuration,
              countryMap = countryMap)
          )
        Source(playerInfos.toList)
      }
      .via(ClickhouseFlow[PlayerInfoModelCH](databaseName, "player_info"))
  }
}
