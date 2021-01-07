import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.{ClosedShape, Supervision}
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, RunnableGraph, Sink, Source}
import chpp.OauthTokens
import chpp.players.{PlayersHttpFlow, PlayersRequest}
import chpp.search.models.SearchType
import chpp.teamdetails.{TeamDetailsHttpFlow, TeamDetailsRequest}
import chpp.worlddetails.models.WorldDetails
import chpp.worlddetails.{WorldDetailsHttpFlow, WorldDetailsRequest}
import com.typesafe.config.ConfigFactory
import loadergraph.leagueunits.LeagueUnitIdsSource
import loadergraph.matchdetails.MatchDetailsFlow
import loadergraph.playerevents.PlayerEventsFlow
import loadergraph.playerinfos.PlayerInfoFlow
import loadergraph.teamdetails.TeamDetailsFlow
import models.clickhouse.{MatchDetailsCHModel, TeamDetailsModelCH}
import models.stream.StreamMatchDetails

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.Success

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

  val countryMapFuture = Source.single((WorldDetailsRequest(), Unit))
    .via(WorldDetailsHttpFlow())
    .map(_._1)
    .runFold(null.asInstanceOf[WorldDetails])((_, wd) => wd)
    .map{worldDetails =>
      worldDetails.leagueList
        .view
        .map(league => league.country.map(country => (country.countryId, league.leagueId)))
        .filter(_.isDefined)
        .map(_.get)
        .toMap
    }

  val countryMap = Await.result(countryMapFuture, 30 seconds)
  println(countryMap)

  val matchDetailsSource = LeagueUnitIdsSource(100)
    .via(MatchDetailsFlow())
    .async

  val teamDetailsModelCHSink = Sink.fold[List[TeamDetailsModelCH], TeamDetailsModelCH](List())((li, td) => li ::: List(td))

  val graph = RunnableGraph.fromGraph(
    GraphDSL.create(teamDetailsModelCHSink) { implicit builder => tmSink =>
      import GraphDSL.Implicits._

      val broadcast = builder.add(Broadcast[StreamMatchDetails](4).async)
      val matchDetailsFlow = builder.add(Flow[StreamMatchDetails].map(MatchDetailsCHModel.convert))
      val playerEventsFlow = builder.add(PlayerEventsFlow())
      val playerInfosFlow = builder.add(PlayerInfoFlow(countryMap))
      val teamDetailsFlow = builder.add(TeamDetailsFlow())

      matchDetailsSource ~> broadcast ~> matchDetailsFlow ~> Sink.ignore
                            broadcast ~> playerEventsFlow ~> Sink.ignore
                            broadcast ~> playerInfosFlow  ~> Sink.ignore
                            broadcast ~> teamDetailsFlow  ~> tmSink
      ClosedShape
    }
  )

  graph.run().onComplete(println)


}
