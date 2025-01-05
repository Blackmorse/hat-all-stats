package loadergraph

import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.stream.FlowShape
import org.apache.pekko.stream.scaladsl.{Broadcast, Flow, GraphDSL, Merge}
import chpp.OauthTokens
import chpp.commonmodels.MatchType
import chpp.leaguedetails.models.LeagueDetails
import com.crobox.clickhouse.stream.Insert
import com.typesafe.config.Config
import loadergraph.matchdetails.{MatchDetailsCHModelFlow, MatchDetailsFlow}
import loadergraph.matchlineup.StreamMatchDetailsWithLineup
import loadergraph.playerevents.PlayerEventsFlow
import loadergraph.playerinfos.PlayerInfoFlow
import loadergraph.promotions.PromotionsSink
import loadergraph.teamdetails.TeamDetailsFlow
import loadergraph.teams.{LeagueUnitDetailsFlow, TeamsFlow}
import models.stream.{LeagueUnit, StreamTeam}

import scala.concurrent.{ExecutionContext, Future}

object LeagueMatchesFlow {
  def apply(config: Config, countryMap: Map[Int, Int], lastMatchesWindow: Int)(implicit oauthTokens: OauthTokens, system: ActorSystem,
                                                       executionContext: ExecutionContext): Flow[Int, Insert, Future[List[StreamTeam]]] = {
    val databaseName = config.getString("database_name")

    Flow.fromGraph(
      GraphDSL.create(PromotionsSink()) { implicit builder =>
        promotionsSink =>
          import GraphDSL.Implicits._

          val teamFlow: FlowShape[(LeagueDetails, LeagueUnit), StreamTeam] = builder.add(TeamsFlow())
          val leagueUnitDetailsFlow: FlowShape[Int, (LeagueDetails, LeagueUnit)] = builder.add(LeagueUnitDetailsFlow())
          val matchDetailsFlow: FlowShape[StreamTeam, StreamMatchDetailsWithLineup] = builder.add(MatchDetailsFlow(MatchType.LEAGUE_MATCH, lastMatchesWindow))

          val broadcast = builder.add(Broadcast[StreamMatchDetailsWithLineup](4).async)

          val matchDetailsCHModelFlow: FlowShape[StreamMatchDetailsWithLineup, Insert] = builder.add(MatchDetailsCHModelFlow(databaseName))
          val playerEventsFlow: FlowShape[StreamMatchDetailsWithLineup, Insert] = builder.add(PlayerEventsFlow(databaseName))
          val playerInfosFlow: FlowShape[StreamMatchDetailsWithLineup, Insert] = builder.add(PlayerInfoFlow(databaseName, countryMap))
          val teamDetailsFlow: FlowShape[StreamMatchDetailsWithLineup, Insert] = builder.add(TeamDetailsFlow(databaseName))

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
