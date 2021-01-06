import akka.actor.ActorSystem
import akka.stream.{ClosedShape, Supervision}
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, RunnableGraph, Sink, Source}
import com.typesafe.config.ConfigFactory
import flows.http.{LeagueDetailsFlow, MatchDetailsHttpFlow, MatchesArchiveFlow, PlayersFlow}
import loadergraph.leagueunits.LeagueUnitIdsSource
import loadergraph.matchdetails.MatchDetailsFlow
import loadergraph.playerevents.PlayerEventsFlow
import models.OauthTokens
import models.chpp.search.SearchType
import models.clickhouse.MatchDetailsCHModel
import models.stream.StreamMatchDetails
import requests.{LeagueDetailsRequest, MatchDetailsRequest, MatchesArchiveRequest, PlayersRequest, SearchRequest, WorldDetailsRequest}

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



//  val matchDetailsSource = LeagueUnitIdsSource(35)
//    .via(MatchDetailsFlow())
//
//  val graph = RunnableGraph.fromGraph(
//    GraphDSL.create() { implicit builder =>
//      import GraphDSL.Implicits._
//
//      val broadcast = builder.add(Broadcast[StreamMatchDetails](2))
//      val matchDetailsFlow = builder.add(Flow[StreamMatchDetails].map(MatchDetailsCHModel.convert))
//      val playerEventsFlow = builder.add(PlayerEventsFlow())
//
//      matchDetailsSource ~> broadcast ~> matchDetailsFlow ~> Sink.foreach(println)
//                            broadcast ~> playerEventsFlow ~> Sink.foreach(println)
//      ClosedShape
//    }
//  )
//
//  graph.run()


  Source.single(615797)
    .map(id => (PlayersRequest(teamId = Some(id), includeMatchInfo = Some(true)), id))
    .via(PlayersFlow())
    .log("asd")
    .runForeach(println)
}
