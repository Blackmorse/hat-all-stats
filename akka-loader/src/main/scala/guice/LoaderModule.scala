package guice

import akka.actor.ActorSystem
import chpp.OauthTokens
import com.crobox.clickhouse.ClickhouseClient
import com.google.inject.AbstractModule
import com.typesafe.config.Config

class LoaderModule(config: Config, actorSystem: ActorSystem) extends AbstractModule {
  override def configure(): Unit = {
    val authToken = config.getString("tokens.authToken")
    val authCustomerKey = config.getString("tokens.authCustomerKey")
    val clientSecret = config.getString("tokens.clientSecret")
    val tokenSecret = config.getString("tokens.tokenSecret")

    val oauthTokens: OauthTokens = OauthTokens(authToken, authCustomerKey, clientSecret, tokenSecret)

    bind(classOf[ActorSystem]).toInstance(actorSystem)
    bind(classOf[Config]).toInstance(config)
    bind(classOf[ClickhouseClient]).toInstance(new ClickhouseClient(Some(config)))
    bind(classOf[OauthTokens]).toInstance(oauthTokens)
  }
}