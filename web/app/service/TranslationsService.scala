package service

import models.web.HattidError
import models.web.translations.LanguageTranslations
import zio.{ZIO, ZLayer}

object TranslationsService {
  private val languages = Seq(
    "en" -> 2, //English
    "de" -> 3, //German
    "it" -> 4, //Italian
    "tr" -> 19, //Turkish
    "ru" -> 14, //Russian
    "es" -> 6, //Spanish
    "hr" -> 39, //Croatian
    "fa" -> 75 //Persian
  )
  
  lazy val layer: ZLayer[ChppService, HattidError, TranslationsService] = ZLayer {
    for {
      chppService  <- ZIO.service[ChppService]
      translations <- ZIO.foreach(languages.map { case (languageAbbr, languageId) =>
        languageAbbr -> chppService.translations(languageId)
      }) { case (languageAbbr, zio) => zio.map(translations => languageAbbr -> LanguageTranslations(translations)) }
    } yield TranslationsService(translations)
  }
}

case class TranslationsService(translations: Seq[(String, LanguageTranslations)])
