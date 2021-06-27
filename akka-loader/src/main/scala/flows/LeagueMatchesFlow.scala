package flows

import akka.actor.ActorSystem
import akka.stream.FlowShape
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, Merge}
import chpp.OauthTokens
import chpp.matchesarchive.models.MatchType
import com.crobox.clickhouse.stream.Insert
import com.typesafe.config.Config
import loadergraph.matchdetails.{MatchDetailsCHModelFlow, MatchDetailsFlow}
import loadergraph.playerevents.PlayerEventsFlow
import loadergraph.playerinfos.PlayerInfoFlow
import loadergraph.promotions.PromotionsSink
import loadergraph.teamdetails.TeamDetailsFlow
import loadergraph.teams.{LeagueUnitDetailsFlow, TeamsFlow}
import models.stream.{StreamMatchDetails, StreamTeam}

import scala.concurrent.{ExecutionContext, Future}

object LeagueMatchesFlow {
  def apply(config: Config, countryMap: Map[Int, Int])(implicit oauthTokens: OauthTokens, system: ActorSystem,
                                                       executionContext: ExecutionContext): Flow[Int, Insert, Future[List[StreamTeam]]] = {
    val databaseName = config.getString("database_name")

    Flow.fromGraph(
      GraphDSL.create(PromotionsSink()) { implicit builder =>
        promotionsSink =>
          import GraphDSL.Implicits._

          val teamFlow = builder.add(TeamsFlow())
          val leagueUnitDetailsFlow = builder.add(LeagueUnitDetailsFlow())
          val matchDetailsFlow = builder.add(MatchDetailsFlow(MatchType.LEAGUE_MATCH))

          val broadcast = builder.add(Broadcast[StreamMatchDetails](4).async)

          val matchDetailsCHModelFlow = builder.add(MatchDetailsCHModelFlow(databaseName))
          val playerEventsFlow = builder.add(PlayerEventsFlow(databaseName))
          val playerInfosFlow = builder.add(PlayerInfoFlow(databaseName, countryMap))
          val teamDetailsFlow = builder.add(TeamDetailsFlow(databaseName))

          val merge = builder.add(Merge[Insert](4))

          val teamBroadcast = builder.add(Broadcast[StreamTeam](2))

          leagueUnitDetailsFlow ~> teamFlow ~> teamBroadcast ~> matchDetailsFlow ~> broadcast
          teamBroadcast ~> promotionsSink
          broadcast ~> matchDetailsCHModelFlow ~> merge
          broadcast ~> playerEventsFlow ~> merge
          broadcast ~> playerInfosFlow ~> merge
          broadcast ~> teamDetailsFlow ~> merge
          FlowShape(leagueUnitDetailsFlow.in, merge.out)
      }
    )
  }
}
