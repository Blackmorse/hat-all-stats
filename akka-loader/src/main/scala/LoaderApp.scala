import akka.actor.ActorSystem
import akka.stream.Supervision
import akka.stream.scaladsl.Source
import com.typesafe.config.ConfigFactory
import flows.SearchFlow
import models.OauthTokens
import models.search.SearchType
import requests.SearchRequest

object LoaderApp extends  App {
  implicit val actorSystem = ActorSystem("LoaderActorSystem")
  import actorSystem.dispatcher

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

  Source(1 to 2)
//    .map(leagueId => (WorldDetailsRequest(leagueId = Some(leagueId)), leagueId))
//    .via(WorldDetailsFlow.create)
    .map(leagueId => (SearchRequest(searchType = Some(SearchType.TEAMS), searchString = Some("asd")), leagueId))
    .via(SearchFlow.create)
    .log("logger")
    .runForeach(println)
}
