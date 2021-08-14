package webclients

import akka.actor.ActorSystem
import chpp.{AbstractRequest, ChppRequestExecutor, OauthTokens}
import com.lucidchart.open.xtract.XmlReader
import play.api.{Configuration, Logger}

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

@Singleton
class ChppClient @Inject()(val configuration: Configuration,
                            implicit val actorSystem: ActorSystem
                          ) {
  val logger: Logger = Logger(this.getClass)

  private implicit val oauthTokens = OauthTokens(configuration.get[String]("hattrick.accessToken"),
    configuration.get[String]("hattrick.customerKey"),
    configuration.get[String]("hattrick.customerSecret"),
    configuration.get[String]("hattrick.accessTokenSecret")
    )

  def execute[Model, Request <: AbstractRequest[Model]](request: Request)(implicit reader: XmlReader[Model]): Future[Model] = {
    ChppRequestExecutor.execute(request)
  }
}
