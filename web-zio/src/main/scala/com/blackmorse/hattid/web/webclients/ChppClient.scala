package com.blackmorse.hattid.web.webclients

import chpp.chpperror.ChppError
import com.blackmorse.hattid.web.models.web.{BadGatewayError, BadRequestError, HattidError, HattidInternalError}
import com.blackmorse.hattid.web.webclients.ChppClient.*
import com.lucidchart.open.xtract.{ParseError, XmlReader}
import zio.http.codec.TextBinaryCodec.fromSchema
import zio.http.{Client, Headers, Request, ZClient}
import zio.{IO, Schedule, URLayer, ZIO, ZLayer}
import chpp.*

import scala.util.Try

case class CustomerConfig(customerKey: String, customerSecret: String)

case class AccessConfig(accessToken: String, accessTokenSecret: String)

type AuthConfig = AccessConfig & CustomerConfig

object ChppClient {
  trait ChppErrorZ

  case class ExternalChppError(t: Throwable) extends ChppErrorZ
  case class UnparsableModelErrorZ(errors: Seq[ParseError], rawResponse: String, request: String) extends ChppErrorZ
  case class UnparsableChppError(errors: Seq[ParseError], rawResponse: String) extends ChppErrorZ
  case class ChppErrorResponseZ(chppError: ChppError) extends ChppErrorZ
  
  def make: URLayer[Client & AccessConfig & CustomerConfig, ChppClient] = ZLayer {
    for {
      customerConfig <- ZIO.service[CustomerConfig]
      accessConfig   <- ZIO.service[AccessConfig]
      client         <- ZIO.service[Client]
    } yield new ChppClient(client, customerConfig, accessConfig)
  }
}

class ChppClient(client: Client, customerConfig: CustomerConfig, accessConfig: AccessConfig) {
  def executeZio[Model, Request <: AbstractRequest[Model]](request: Request, userAccessConfig: Option[AccessConfig] = None)(implicit reader: XmlReader[Model]): IO[HattidError, Model] = {
    ZIO.attemptBlocking {
      executeZioInner(request, userAccessConfig).retry(Schedule.exponential(zio.Duration.fromMillis(800L)) && Schedule.recurs(4))
    }.flatten
      .mapError {
        case e: HattidError => e
        case t: Throwable  => HattidInternalError("CHPP service is unavailable, error: " + t.getMessage)
      }
    
  }

  private def executeZioInner[Model, Request <: AbstractRequest[Model]](request: Request, userAccessConfig: Option[AccessConfig])(implicit reader: XmlReader[Model]): IO[HattidError, Model] = {
    val accessConfig = userAccessConfig.getOrElse(this.accessConfig)
    ZIO.scoped {
      val requestData = request.requestData(OauthTokens(accessConfig.accessToken, customerConfig.customerKey, customerConfig.customerSecret, accessConfig.accessTokenSecret))
      val zClient = client.host("chpp.hattrick.org/chppxml.ashx").port(443)
      val zioHttpRequest = Request.get(requestData.uri).setHeaders(Headers.apply("Authorization" -> requestData.header))
      (for {
        res              <- ZClient.batched(zioHttpRequest).provide(ZLayer.succeed(zClient))
        body             <- res.bodyAs[String]
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
