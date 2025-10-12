package webclients

import chpp.ChppRequestExecutor.{ChppErrorResponse, ChppUnparsableErrorResponse, ModelUnparsableResponse}
import chpp.chpperror.ChppError
import chpp.{AbstractRequest, OauthTokens, ResponseParser}
import com.lucidchart.open.xtract.{ParseError, XmlReader}
import models.web.{BadGatewayError, BadRequestError, HattidError, HattidInternalError}
import webclients.ChppClient.{ChppErrorResponseZ, ExternalChppError, UnparsableChppError, UnparsableModelErrorZ}
import zio.http.codec.TextBinaryCodec.fromSchema
import zio.http.{Client, Headers, Request, ZClient}
import zio.{Schedule, ZIO}

import javax.inject.{Inject, Singleton}
import scala.util.Try

case class AuthConfig(customerKey: String, customerSecret: String, accessToken: String, accessTokenSecret: String)

object ChppClient {
  trait ChppErrorZ

  case class ExternalChppError(t: Throwable) extends ChppErrorZ
  case class UnparsableModelErrorZ(errors: Seq[ParseError], rawResponse: String, request: String) extends ChppErrorZ
  case class UnparsableChppError(errors: Seq[ParseError], rawResponse: String) extends ChppErrorZ
  case class ChppErrorResponseZ(chppError: ChppError) extends ChppErrorZ
}

@Singleton
class ChppClient @Inject()() {
  def executeZio[Model, Request <: AbstractRequest[Model]](request: Request)(implicit reader: XmlReader[Model]): ZIO[AuthConfig & Client, HattidError, Model] = {
    ZIO.attemptBlocking {
      executeZioInner(request).retry(Schedule.exponential(zio.Duration.fromMillis(800L)) && Schedule.recurs(4))
    }.flatten
      .mapError {
        case e: HattidError => e
        case t: Throwable  => HattidInternalError("CHPP service is unavailable, error: " + t.getMessage)
      }
  }

  private def executeZioInner[Model, Request <: AbstractRequest[Model]](request: Request)(implicit reader: XmlReader[Model]): ZIO[AuthConfig & Client, HattidError, Model] = {
    ZIO.scoped {
      (for {
        authConfig       <- ZIO.service[AuthConfig]
        requestData      = request.requestData(OauthTokens(authConfig.accessToken, authConfig.customerKey, authConfig.customerSecret, authConfig.accessTokenSecret))
        client           <- ZIO.serviceWith[Client](_.host("chpp.hattrick.org/chppxml.ashx").port(443))
        zioHttpRequest   = Request.get(requestData.uri).setHeaders(Headers.apply("Authorization" -> requestData.header))
        _ <- ZIO.debug(zioHttpRequest.toString)
        res              <- ZClient.batched(zioHttpRequest)
        body             <- res.bodyAs[String]
        _ <- ZIO.debug(body.substring(0, 20))
        response         <- ZIO.fromTry( Try { ResponseParser.parseResponse(request, body) } )
      } yield response)
        //TODO legacy double errors conversion, should be fixed later
        .mapError {
          case ChppErrorResponse(chppError) => ChppErrorResponseZ(chppError)
          case ChppUnparsableErrorResponse(errors, rawResponse) => UnparsableChppError(errors, rawResponse)
          case ModelUnparsableResponse(errors, rawResponse, req) => UnparsableModelErrorZ(errors, rawResponse, req)
          case e: Throwable => ExternalChppError(e)
        }.mapError {
          case ExternalChppError(t) => BadGatewayError("CHPP service is unavailable, error: " + t.getMessage)
          case UnparsableModelErrorZ(errors, rawResponse, req) =>
            val errorsString = errors.map(_.toString).mkString(", ")
            BadGatewayError("Bad response from CHPP, errors: " + errorsString.substring(0, Math.min(300, errorsString.length)))
          case UnparsableChppError(errors, rawResponse) => BadGatewayError("Unclear error from CHPP, errors: " + rawResponse.substring(0, Math.min(300, rawResponse.length)))
          case ChppErrorResponseZ(chppError) => BadRequestError(chppError.error)
        }
    }
  }
}
