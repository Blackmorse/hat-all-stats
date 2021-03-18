import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.FlowShape
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, Merge}
import chpp.OauthTokens
import com.crobox.clickhouse.stream.Insert
import com.typesafe.config.Config
import flows.ClickhouseFlow
import loadergraph.matchdetails.MatchDetailsFlow
import loadergraph.playerevents.PlayerEventsFlow
import loadergraph.playerinfos.PlayerInfoFlow
import loadergraph.teamdetails.TeamDetailsFlow
import loadergraph.teams.TeamsFlow
import models.clickhouse.{MatchDetailsCHModel, PlayerEventsModelCH, PlayerInfoModelCH, TeamDetailsModelCH}
import models.stream.StreamMatchDetails

import scala.concurrent.ExecutionContext

object FullLoaderFlow {
  def apply(config: Config, countryMap: Map[Int, Int])(implicit oauthTokens: OauthTokens, system: ActorSystem,
              executionContext: ExecutionContext): Flow[Int, Insert, NotUsed] = {
    val databaseName = config.getString("database_name")

    Flow.fromGraph(
      GraphDSL.create() { implicit builder =>
        import GraphDSL.Implicits._

        val teamFlow = builder.add(TeamsFlow())
        val matchDetailsFlow = builder.add(MatchDetailsFlow())
        val broadcast = builder.add(Broadcast[StreamMatchDetails](4).async)
        val matchDetailsCHModelFlow = builder.add(Flow[StreamMatchDetails].map(MatchDetailsCHModel.convert))
        val playerEventsFlow = builder.add(PlayerEventsFlow())
        val playerInfosFlow = builder.add(PlayerInfoFlow(countryMap))
        val teamDetailsFlow = builder.add(TeamDetailsFlow())

        val matchDetailsChFlow = builder.add(ClickhouseFlow[MatchDetailsCHModel](databaseName, "match_details"))
        val playerEventsChFlow = builder.add(ClickhouseFlow[PlayerEventsModelCH](databaseName, "player_events"))
        val playerInfosChFlow = builder.add(ClickhouseFlow[PlayerInfoModelCH](databaseName, "player_info"))
        val teamDetailsChFlow = builder.add(ClickhouseFlow[TeamDetailsModelCH](databaseName, "team_details"))

        val merge = builder.add(Merge[Insert](4))

        teamFlow ~> matchDetailsFlow ~>  broadcast ~> matchDetailsCHModelFlow ~> matchDetailsChFlow ~> merge
                                         broadcast ~> playerEventsFlow ~> playerEventsChFlow ~> merge
                                         broadcast ~> playerInfosFlow  ~> playerInfosChFlow  ~> merge
                                         broadcast ~> teamDetailsFlow  ~> teamDetailsChFlow  ~> merge
        FlowShape(teamFlow.in, merge.out)
      }
    )
  }
}
