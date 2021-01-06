package chpp

import akka.http.scaladsl.model.HttpRequest

abstract class AbstractRequest {
  def createRequest()(implicit oauthTokens: OauthTokens): HttpRequest
}
