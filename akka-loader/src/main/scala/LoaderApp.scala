import akka.actor.ActorSystem
import akka.stream.{ClosedShape, Supervision}
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, RunnableGraph, Sink, Source}
import chpp.players.PlayersHttpFlow
import chpp.search.models.SearchType
import chpp.worlddetails.WorldDetailsRequest
import com.typesafe.config.ConfigFactory
import loadergraph.leagueunits.LeagueUnitIdsSource
import loadergraph.matchdetails.MatchDetailsFlow
import loadergraph.playerevents.PlayerEventsFlow
import loadergraph.playerinfos.PlayerInfoFlow
import models.OauthTokens
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



  val matchDetailsSource = LeagueUnitIdsSource(35)
    .via(MatchDetailsFlow())
    .async

  val graph = RunnableGraph.fromGraph(
    GraphDSL.create() { implicit builder =>
      import GraphDSL.Implicits._

      val broadcast = builder.add(Broadcast[StreamMatchDetails](3).async)
      val matchDetailsFlow = builder.add(Flow[StreamMatchDetails].map(MatchDetailsCHModel.convert))
      val playerEventsFlow = builder.add(PlayerEventsFlow())
      val playerInfosFlow = builder.add(PlayerInfoFlow())

      matchDetailsSource ~> broadcast ~> matchDetailsFlow ~> Sink.ignore
                            broadcast ~> playerEventsFlow ~> Sink.ignore
                            broadcast ~> playerInfosFlow  ~> Sink.ignore
      ClosedShape
    }
  )

  graph.run()


//  Source.single(615797)
//    .map(id => (PlayersRequest(teamId = Some(id), includeMatchInfo = Some(true)), id))
//    .via(PlayersFlow())
//    .log("asd")
//    .runForeach(println)
}
