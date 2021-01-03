package requests

import akka.http.scaladsl.model.HttpRequest
import models.OauthTokens

abstract class AbstractRequest {
  def createRequest()(implicit oauthTokens: OauthTokens): HttpRequest
}
