package webclients

import akka.actor.ActorSystem
import chpp.chpperror.ChppError
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

  private implicit val oauthTokens: OauthTokens = OauthTokens(configuration.get[String]("hattrick.accessToken"),
    configuration.get[String]("hattrick.customerKey"),
    configuration.get[String]("hattrick.customerSecret"),
    configuration.get[String]("hattrick.accessTokenSecret")
    )

  def execute[Model, Request <: AbstractRequest[Model]](request: Request)(implicit reader: XmlReader[Model]): Future[Either[ChppError, Model]] =
    ChppRequestExecutor.execute(request)

  @deprecated
  def executeUnsafe[Model, Request <: AbstractRequest[Model]](request: Request)(implicit reader: XmlReader[Model]): Future[Model] = {
    import actorSystem.dispatcher
    ChppRequestExecutor.execute(request) map {
      case Right(value) => value
      case Left(err) => throw new Exception(s"Error while parsing: $err")
    }
  }
}
