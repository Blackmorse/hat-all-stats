import akka.actor.ActorSystem
import akka.stream.Supervision
import akka.stream.scaladsl.Source
import com.typesafe.config.ConfigFactory
import flows.http.{LeagueDetailsFlow, MatchDetailsFlow, MatchesArchiveFlow}
import models.OauthTokens
import models.chpp.search.SearchType
import requests.{LeagueDetailsRequest, MatchDetailsRequest, MatchesArchiveRequest, SearchRequest, WorldDetailsRequest}
import sources.leagueunits.{LeagueUnitIdsSource, LeagueWithLevelSource}

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

//  LeagueUnitIdsSource(1)
//    .async
//    .runForeach(_ => {})

  Source.single(666296555L)
    .map(id => (MatchDetailsRequest(matchId = Some(id)), id))
    .via(MatchDetailsFlow())
    .log("log")
    .runForeach(println)
}
