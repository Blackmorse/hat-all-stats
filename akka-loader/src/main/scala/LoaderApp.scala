import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.{ClosedShape, RestartSettings, SinkShape, SourceShape, Supervision}
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, Keep, Merge, RestartSource, RunnableGraph, Sink, Source}
import akka.util.ByteString
import chpp.OauthTokens
import chpp.leaguefixtures.{LeagueFixturesHttpFlow, LeagueFixturesRequest}
import chpp.players.{PlayersHttpFlow, PlayersRequest}
import chpp.search.models.SearchType
import chpp.teamdetails.{TeamDetailsHttpFlow, TeamDetailsRequest}
import chpp.worlddetails.models.WorldDetails
import chpp.worlddetails.{WorldDetailsHttpFlow, WorldDetailsRequest}
import clickhouse.{PlayerStatsRequester, TableTruncater, TeamRankJoiner}
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
import scala.util.{Failure, Success}
import spray.json._
import utils.WorldDetailsSingleRequest

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

  val worldDetails = WorldDetailsSingleRequest.request(leagueId = None)

  val countryMap = worldDetails.leagueList
    .view
    .map(league => league.country.map(country => (country.countryId, league.leagueId)))
    .filter(_.isDefined)
    .map(_.get)
    .toMap


  private val leagueIdNumber = 100

  val teamsSource = TeamsSource(leagueIdNumber)
    .async
    .via(MatchDetailsFlow())
    .async

  val settings = RestartSettings(
    minBackoff = 1 minute,
    maxBackoff = 10 minutes,
    randomFactor = 0
  ).withMaxRestarts(10, 10 minutes)


  val client = new ClickhouseClient(Some(config))
  val chSink = Flow[Insert].log("pipeline_log").toMat(ClickhouseSink.insertSink(config, client))(Keep.right)

  val graph = Source.fromGraph(
    GraphDSL.create() { implicit builder =>
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

      teamsSource ~>  broadcast ~> matchDetailsFlow ~> matchDetailsChFlow ~> merge
                      broadcast ~> playerEventsFlow ~> playerEventsChFlow ~> merge
                      broadcast ~> playerInfosFlow  ~> playerInfosChFlow  ~> merge
                      broadcast ~> teamDetailsFlow  ~> teamDetailsChFlow  ~> merge
      SourceShape(merge.out)
    }
  )

  val graphBackoff = RestartSource.onFailuresWithBackoff(settings){() => graph}

    graphBackoff.toMat(chSink)(Keep.right).run().onComplete{
    case Failure(exception) =>
      println("Due  to some error : " + exception)
      throw new Exception(exception)
    case Success(_) =>
      println("SUCCESS!")
      val league = WorldDetailsSingleRequest.request(leagueId = Some(leagueIdNumber)).leagueList.head
      //worldDetails.leagueList.filter(_.leagueId == leagueIdNumber).head
      client.execute(PlayerStatsRequester.playerStatsJoinRequest(league, databaseName))
        .onComplete{
          case Failure(exception) =>
            println(exception)
          case Success(_) =>
            client.execute(TableTruncater.sql(league, "player_info", databaseName))
            client.execute(TableTruncater.sql(league, "player_events", databaseName))

            val sql = TeamRankJoiner.createSql(
              season = league.season - league.seasonOffset,
              leagueId = league.leagueId,
              round = league.matchRound - 1,
              divisionLevel = None,
              database = databaseName
            )
            client.execute(sql).onComplete(println)
            (1 to league.numberOfLevels).foreach(level => {
              val sql = TeamRankJoiner.createSql(
                season = league.season - league.seasonOffset,
                leagueId = league.leagueId,
                round = league.matchRound - 1,
                divisionLevel = Some(level),
                database = databaseName
              )
              client.execute(sql)
            })
        }
  }
}
