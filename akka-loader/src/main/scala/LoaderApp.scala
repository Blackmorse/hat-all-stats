import akka.actor.ActorSystem
import akka.stream.Supervision
import akka.stream.scaladsl.Source
import com.typesafe.config.ConfigFactory
import flows.{SearchFlow, WorldDetailsFlow}
import models.OauthTokens
import models.chpp.search.SearchType
import requests.{SearchRequest, WorldDetailsRequest}
import sources.leagueunits.{LeagueUnitIdsSource, LeagueWithLevelSource}

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

  LeagueUnitIdsSource(35)
    .runForeach(println)
}
