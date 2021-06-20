package chpp

import akka.http.scaladsl.model.HttpRequest

abstract class AbstractRequest[Model] {
  type T = Model
  def createRequest()(implicit oauthTokens: OauthTokens): HttpRequest

  def preprocessBody(body: String): String =
    body.replace("<?xml version=\"1.0\" encoding=\"utf-8\"?>", "")
}
