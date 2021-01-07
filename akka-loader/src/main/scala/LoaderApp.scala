import akka.actor.ActorSystem
import akka.stream.{ClosedShape, Supervision}
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, RunnableGraph, Sink, Source}
import chpp.OauthTokens
import chpp.players.{PlayersHttpFlow, PlayersRequest}
import chpp.search.models.SearchType
import chpp.teamdetails.{TeamDetailsHttpFlow, TeamDetailsRequest}
import chpp.worlddetails.WorldDetailsRequest
import com.typesafe.config.ConfigFactory
import loadergraph.leagueunits.LeagueUnitIdsSource
import loadergraph.matchdetails.MatchDetailsFlow
import loadergraph.playerevents.PlayerEventsFlow
import loadergraph.playerinfos.PlayerInfoFlow
import loadergraph.teamdetails.TeamDetailsFlow
import models.clickhouse.MatchDetailsCHModel
import models.stream.StreamMatchDetails

object LoaderApp extends  App {
  implicit val actorSystem = ActorSystem("LoaderActorSystem")
  import actorSystem.dispatcher
//  implicit val executionContext = actorSystem.dispatchers.lookup("my-dispatcher")

  val config = ConfigFactory.load()

  val authToken = config.getString("tokens.authToken")
  val authCustomerKey = config.getString("tokens.authCustomerKey")
  val clientSecret = config.getString("tokens.clientSecret")
  val tokenSecret = config.getString("tokens.tokenSecret")

  implicit val oauthTokens: OauthTokens = OauthTokens(authToken, authCustomerKey, clientSecret, tokenSecret)

  val decider: Supervision.Decider = {
    case _ : RuntimeException => Supervision.Stop
    case _ => Supervision.Restart
  }



  val matchDetailsSource = LeagueUnitIdsSource(100)
    .via(MatchDetailsFlow())
    .async

  val graph = RunnableGraph.fromGraph(
    GraphDSL.create() { implicit builder =>
      import GraphDSL.Implicits._

      val broadcast = builder.add(Broadcast[StreamMatchDetails](4).async)
      val matchDetailsFlow = builder.add(Flow[StreamMatchDetails].map(MatchDetailsCHModel.convert))
      val playerEventsFlow = builder.add(PlayerEventsFlow())
      val playerInfosFlow = builder.add(PlayerInfoFlow())
      val teamDetailsFlow = builder.add(TeamDetailsFlow())

      matchDetailsSource ~> broadcast ~> matchDetailsFlow ~> Sink.ignore
                            broadcast ~> playerEventsFlow ~> Sink.ignore
                            broadcast ~> playerInfosFlow  ~> Sink.ignore
                            broadcast ~> teamDetailsFlow  ~> Sink.ignore
      ClosedShape
    }
  )

  graph.run()


//  Source.single(615797)
//    .map(id => (TeamDetailsRequest(teamId = Some(615797), includeFlags = Some(true), includeDomesticFlags = Some(true)), id))
//    .via(TeamDetailsHttpFlow())
//    .log("asd")
//    .runForeach(println)
}
