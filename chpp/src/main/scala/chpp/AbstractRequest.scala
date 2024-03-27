package chpp

import org.apache.pekko.http.scaladsl.model.HttpRequest

abstract class AbstractRequest[Model] {
  type T = Model
  def createRequest()(implicit oauthTokens: OauthTokens): HttpRequest

  def preprocessResponseBody(body: String): String =
    body.replace("<?xml version=\"1.0\" encoding=\"utf-8\"?>", "")
}
