package chpp.translations

import org.apache.pekko.http.scaladsl.model.HttpRequest
import chpp.{AbstractRequest, OauthTokens, RequestCreator}
import chpp.translations.models.Translations

case class TranslationsRequest(languageId: Int) extends AbstractRequest[Translations] {
  override def createRequest()(implicit oauthTokens: OauthTokens): HttpRequest = {
    val map = RequestCreator.params("translations", "1.2",
    "languageId" -> Some(languageId))

    RequestCreator.create(map)
  }
}
