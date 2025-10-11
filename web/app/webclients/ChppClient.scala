package webclients

import chpp.ZChppRequestExecutor.{ChppErrorResponseZ, ExternalChppError, UnparsableChppError, UnparsableModelErrorZ}
import chpp.chpperror.ChppError
import chpp.{AbstractRequest, ChppRequestExecutor, OauthTokens, ZChppRequestExecutor}
import com.lucidchart.open.xtract.XmlReader
import models.web.{BadGatewayError, BadRequestError, HattidError}
import org.apache.pekko.actor.ActorSystem
import play.api.{Configuration, Logger}
import zio.{IO, ZIO, ZLayer}

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

  def executeZio[Model, Request <: AbstractRequest[Model]](request: Request)(implicit reader: XmlReader[Model], t: zio.Tag[Model]): IO[HattidError, Model] =
    ZChppRequestExecutor.executeWithRetry(request)
      .mapError{
        case ExternalChppError(t) => BadGatewayError("CHPP service is unavailable, error: " + t.getMessage)
        case UnparsableModelErrorZ(errors, rawResponse, req) => 
          val errorsString = errors.map(_.toString).mkString(", ")
          BadGatewayError("Bad response from CHPP, errors: " + errorsString.substring(0, Math.min(300, errorsString.length)))
        case UnparsableChppError(errors, rawResponse) => BadGatewayError("Unclear error from CHPP, errors: " + rawResponse.substring(0, Math.min(300, rawResponse.length)))
        case ChppErrorResponseZ(chppError) => BadRequestError(chppError.error)
      }
      .provide(
        ZLayer.succeed(oauthTokens),
        ZLayer.succeed(actorSystem),
        ZLayer.succeed(reader)
      )

  @deprecated
  def executeUnsafe[Model, Request <: AbstractRequest[Model]](request: Request)(implicit reader: XmlReader[Model]): Future[Model] = {
    import actorSystem.dispatcher
    ChppRequestExecutor.executeWithRetry(request) map {
      case Right(value) => value
      case Left(err) => throw new Exception(s"Error while parsing: $err")
    }
  }
}
