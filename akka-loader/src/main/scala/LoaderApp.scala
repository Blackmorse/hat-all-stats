import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.{ClosedShape, Supervision}
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, Keep, Merge, RunnableGraph, Sink, Source}
import akka.util.ByteString
import chpp.OauthTokens
import chpp.leaguefixtures.{LeagueFixturesHttpFlow, LeagueFixturesRequest}
import chpp.players.{PlayersHttpFlow, PlayersRequest}
import chpp.search.models.SearchType
import chpp.teamdetails.{TeamDetailsHttpFlow, TeamDetailsRequest}
import chpp.worlddetails.models.WorldDetails
import chpp.worlddetails.{WorldDetailsHttpFlow, WorldDetailsRequest}
import com.crobox.clickhouse.ClickhouseClient
import com.crobox.clickhouse.stream.{ClickhouseSink, Insert}
import com.typesafe.config.ConfigFactory
import flows.ClickhouseFlow
import loadergraph.teams.{LeagueUnitIdsSource, TeamsSource}
import loadergraph.matchdetails.MatchDetailsFlow
import loadergraph.playerevents.PlayerEventsFlow
import loadergraph.playerinfos.PlayerInfoFlow
import loadergraph.teamdetails.TeamDetailsFlow
import models.clickhouse.{MatchDetailsCHModel, PlayerEventsModelCH, PlayerInfoModelCH, TeamDetailsModelCH}
import models.stream.StreamMatchDetails

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.Success
import spray.json._

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

  val matchDetailsSource = TeamsSource(100)
    .async
    .via(MatchDetailsFlow())
    .async

  val client = new ClickhouseClient(Some(config))
  val chSink = ClickhouseSink.insertSink(config, client)

  val graph = RunnableGraph.fromGraph(
    GraphDSL.create(chSink) { implicit builder => chSinkShape =>
      import GraphDSL.Implicits._

      val broadcast = builder.add(Broadcast[StreamMatchDetails](4).async)
      val matchDetailsFlow = builder.add(Flow[StreamMatchDetails].map(MatchDetailsCHModel.convert))
      val playerEventsFlow = builder.add(PlayerEventsFlow())
      val playerInfosFlow = builder.add(PlayerInfoFlow(countryMap))
      val teamDetailsFlow = builder.add(TeamDetailsFlow())

      val matchDetailsChFlow = builder.add(ClickhouseFlow[MatchDetailsCHModel]("match_details"))
      val playerEventsChFlow = builder.add(ClickhouseFlow[PlayerEventsModelCH]("player_events"))
      val playerInfosChFlow = builder.add(ClickhouseFlow[PlayerInfoModelCH]("player_info"))
      val teamDetailsChFlow = builder.add(ClickhouseFlow[TeamDetailsModelCH]("team_details"))

      val merge = builder.add(Merge[Insert](4))

      matchDetailsSource ~> broadcast ~> matchDetailsFlow ~> matchDetailsChFlow ~> merge
                            broadcast ~> playerEventsFlow ~> playerEventsChFlow ~> merge
                            broadcast ~> playerInfosFlow  ~> playerInfosChFlow  ~> merge
                            broadcast ~> teamDetailsFlow  ~> teamDetailsChFlow  ~> merge ~> chSinkShape
      ClosedShape
    }
  )

//
//  Source.single(615797)
//    .map(id => (PlayersRequest(teamId = Some(id)), id))
//    .via(PlayersHttpFlow())
//    .runForeach(println)

  graph.run().onComplete(c => {
    println(c)
    c
  })


}
