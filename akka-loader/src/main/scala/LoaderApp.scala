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

  val databaseName = config.getString("database_name")

  implicit val oauthTokens: OauthTokens = OauthTokens(authToken, authCustomerKey, clientSecret, tokenSecret)

  val countryMapFuture = Source.single((WorldDetailsRequest(), Unit))
    .via(WorldDetailsHttpFlow())
    .map(_._1)
    .runFold(null.asInstanceOf[WorldDetails])((_, wd) => wd)
//    .map{worldDetails =>
//      worldDetails.leagueList
//        .view
//        .map(league => league.country.map(country => (country.countryId, league.leagueId)))
//        .filter(_.isDefined)
//        .map(_.get)
//        .toMap
//    }


  val worldDetails = Await.result(countryMapFuture, 30 seconds)
//  println(countryMap)

  val countryMap = worldDetails.leagueList
    .view
    .map(league => league.country.map(country => (country.countryId, league.leagueId)))
    .filter(_.isDefined)
    .map(_.get)
    .toMap


  private val leagueIdNumber = 35
  val matchDetailsSource = TeamsSource(leagueIdNumber)
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

      val matchDetailsChFlow = builder.add(ClickhouseFlow[MatchDetailsCHModel](databaseName, "match_details"))
      val playerEventsChFlow = builder.add(ClickhouseFlow[PlayerEventsModelCH](databaseName, "player_events"))
      val playerInfosChFlow = builder.add(ClickhouseFlow[PlayerInfoModelCH](databaseName, "player_info"))
      val teamDetailsChFlow = builder.add(ClickhouseFlow[TeamDetailsModelCH](databaseName, "team_details"))

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
  graph.run().onComplete(_ => {
    val league = worldDetails.leagueList.filter(_.leagueId == leagueIdNumber).head
    val leagueId = leagueIdNumber
    val round = league.matchRound - 1
    val season = league.season - league.seasonOffset

    client.execute(s"""INSERT INTO $databaseName.player_stats SELECT
        |player_info.season,
        |player_info.league_id,
        |player_info.division_level,
        |player_info.league_unit_id,
        |player_info.league_unit_name,
        |player_info.team_id,
        |player_info.team_name,
        |player_info.time,
        |player_info.dt,
        |player_info.round,
        |player_info.match_id,
        |player_info.player_id,
        |player_info.first_name,
        |player_info.last_name,
        |player_info.age,
        |player_info.days,
        |player_info.role_id,
        |player_info.played_minutes,
        |player_info.rating,
        |player_info.rating_end_of_match,
        |player_info.injury_level,
        |player_info.tsi,
        |player_info.salary,
        |player_events.yellow_cards,
        |player_events.red_cards,
        |player_events.goals,
        |player_info.nationality
      |FROM $databaseName.player_info
      |LEFT JOIN
      |(
        |SELECT *
        |FROM $databaseName.player_events
        |WHERE (season = $season) AND (round = $round)
      |)
        |AS player_events ON (player_info.player_id = player_events.player_id) AND (player_info.season = player_events.season) AND (player_info.round = player_events.round)
      |WHERE (season = $season) AND (league_id = $leagueId) AND (round = $round)""".stripMargin)
      .onComplete(println)
  })


}
