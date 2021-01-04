import akka.actor.ActorSystem
import akka.stream.Supervision
import akka.stream.scaladsl.Source
import com.typesafe.config.ConfigFactory
import flows.http.LeagueDetailsFlow
import models.OauthTokens
import models.chpp.search.SearchType
import requests.{LeagueDetailsRequest, SearchRequest, WorldDetailsRequest}
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

  Source.single(3193)
    .map(id => (LeagueDetailsRequest(leagueUnitId = Some(id)), id))
    .via(LeagueDetailsFlow())
    .log("log")
    .runForeach(println)
}
