package webclients

import chpp.ZChppRequestExecutor.{ChppErrorResponseZ, ExternalChppError, UnparsableChppError, UnparsableModelErrorZ}
import chpp.{AbstractRequest, AuthConfig, ZChppRequestExecutor}
import com.lucidchart.open.xtract.XmlReader
import models.web.{BadGatewayError, BadRequestError, HattidError}
import org.apache.pekko.actor.ActorSystem
import play.api.Configuration
import zio.{ZIO, ZLayer}

import javax.inject.{Inject, Singleton}

@Singleton
class ChppClient @Inject()(val configuration: Configuration,
                            implicit val actorSystem: ActorSystem
                          ) {
  def executeZio[Model, Request <: AbstractRequest[Model]](request: Request)(implicit reader: XmlReader[Model], t: zio.Tag[Model]): ZIO[AuthConfig, HattidError, Model] = {
    ZChppRequestExecutor.executeWithRetry(request)
      .mapError {
        case ExternalChppError(t) => BadGatewayError("CHPP service is unavailable, error: " + t.getMessage)
        case UnparsableModelErrorZ(errors, rawResponse, req) => 
          val errorsString = errors.map(_.toString).mkString(", ")
          BadGatewayError("Bad response from CHPP, errors: " + errorsString.substring(0, Math.min(300, errorsString.length)))
        case UnparsableChppError(errors, rawResponse) => BadGatewayError("Unclear error from CHPP, errors: " + rawResponse.substring(0, Math.min(300, rawResponse.length)))
        case ChppErrorResponseZ(chppError) => BadRequestError(chppError.error)
      }
      .provideSome[AuthConfig](ZLayer.succeed(reader) ++ ZLayer.succeed(actorSystem))
  }
}
