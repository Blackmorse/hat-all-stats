package chpp.translations

import chpp.AbstractRequest
import chpp.translations.models.Translations

case class TranslationsRequest(languageId: Int) extends AbstractRequest[Translations]("translations", "1.2",
  "languageId" -> Some(languageId))
